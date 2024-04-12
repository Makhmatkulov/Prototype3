package w4;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataToDatabase {
    // Constants for configuration
    private static final String API_KEY = "a31e7a106635faa6152c41a052f1ab1b";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/weatherApp";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Murod9059";
    private static final String CITY = "Huddersfield"; // Default city

    // Method to fetch weather data from OpenWeatherMap API
    private static JSONObject fetchWeatherData(String city) throws IOException {
        String apiUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return new JSONObject(response.toString());
        }
    }

    // Method to insert weather data into PostgreSQL database
    private static void insertWeatherData(JSONObject weatherData) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            String city = weatherData.optString("name");
            JSONObject main = weatherData.optJSONObject("main");
            if (city.isEmpty() || main == null) {
                throw new JSONException("Invalid JSON data received from OpenWeatherMap API");
            }

            // Prepare SQL statement
            String sql = "INSERT INTO weather_data(city, temperature, pressure) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, city);
                preparedStatement.setDouble(2, main.optDouble("temp"));
                preparedStatement.setDouble(3, main.optDouble("pressure"));
                // Execute SQL statement
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Weather data for " + city + " inserted successfully!");
                } else {
                    System.out.println("Failed to insert weather data for " + city);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            try {
                JSONObject weatherData = fetchWeatherData(CITY);
                insertWeatherData(weatherData);
            } catch (IOException e) {
                System.err.println("Error fetching weather data: " + e.getMessage());
            } catch (JSONException e) {
                System.err.println("Error parsing JSON: " + e.getMessage());
            }
        };

        // Schedule the task to run every hour
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.HOURS);
    }
}
