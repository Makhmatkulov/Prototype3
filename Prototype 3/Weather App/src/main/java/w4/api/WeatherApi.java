package w4.api;
import org.json.JSONArray;
import spark.Request;
import spark.Response;
import w4.controller.WeatherDataFetcher;

import static spark.Spark.*;

public class WeatherApi {
    private static String getWeatherDataAsJson(Request req, Response res) {
        JSONArray jsonArray = new JSONArray();
        // Your existing method to retrieve weather data
        // Assuming it's in WeatherDataFetcher class
        try {
            jsonArray = WeatherDataFetcher.getWeatherDataAsJson();
        } catch (Exception e) {
            e.printStackTrace();
            res.status(500);
            return "Internal Server Error";
        }
        res.type("application/json");
        return jsonArray.toString();
    }



    public static void main(String[] args) {
        port(8080); // Set port
// Enable CORS for all origins, methods, and headers
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
            res.header("Access-Control-Allow-Credentials", "true");
        });
// Define route to fetch weather data
        get("/weather", (req, res) -> getWeatherDataAsJson(req, res));
    }
}
