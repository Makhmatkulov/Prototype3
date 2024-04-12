let weatherShown = false;

function toggleWeather() {
    const weatherButton = document.getElementById('weatherButton');
    const weatherDataDiv = document.getElementById('weatherInfo');

    if (weatherShown) {
        // Hide weather
        weatherButton.textContent = 'Get Weather';
        weatherDataDiv.innerHTML = '';
    } else {
        // Show weather
        weatherButton.textContent = 'Hide Weather';
        const cachedWeatherData = getCachedWeatherData(); // Check if cached data exists
        if (cachedWeatherData) {
            displayWeather(cachedWeatherData); // Display cached data
        } else {
            getWeather(); // Fetch new data if no cached data found
        }
    }

    // Toggle weatherShown flag
    weatherShown = !weatherShown;
}

function getWeather() {
    fetchWeatherFromServer();
}

function fetchWeatherFromServer() {
    fetch('http://localhost:8080/weather')
        .then(response => response.json())
        .then(data => {
            displayWeather(data);
            cacheWeatherData(data); // Cache the fetched data
        })
        .catch(error => console.error('Error fetching weather:', error));
}

function displayWeather(weatherData) {
    let weatherDataDiv = document.getElementById('weatherInfo');
    weatherDataDiv.innerHTML = ''; // Clear previous weather data
    weatherData.forEach(entry => {
        let city = entry.city;
        let temperature = entry.temperature;
        let pressure = entry.pressure;
        weatherDataDiv.innerHTML += `<p>City: ${city}<br> Temperature: ${temperature}°C<br> Pressure: ${pressure}</p>`;
    });
}

function cacheWeatherData(weatherData) {
    localStorage.setItem('weatherData', JSON.stringify(weatherData)); // Store weather data in localStorage
}

function getCachedWeatherData() {
    const cachedData = localStorage.getItem('weatherData'); // Retrieve cached data from localStorage
    return cachedData ? JSON.parse(cachedData) : null; // Parse and return the cached data if exists
}
