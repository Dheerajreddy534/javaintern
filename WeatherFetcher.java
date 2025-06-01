import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherFetcher {

    // OpenWeather API endpoint - Free tier requires API key
    private static final String API_KEY = ""; // Replace with your API key
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public static void main(String[] args) {
        String city = "London"; // Change city as needed
        fetchWeather(city);
    }

    private static void fetchWeather(String city) {
        try {
            String urlString = String.format("%s?q=%s&appid=%s&units=metric", BASE_URL, city, API_KEY);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse JSON
                JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();

                displayWeather(json);
            } else {
                System.out.println("GET request failed. Response Code: " + responseCode);
            }
            conn.disconnect();
        } catch (Exception e) {
            System.err.println("Error fetching weather data: " + e.getMessage());
        }
    }

    private static void displayWeather(JsonObject json) {
        String cityName = json.get("name").getAsString();

        JsonObject main = json.getAsJsonObject("main");
        double temp = main.get("temp").getAsDouble();
        double feelsLike = main.get("feels_like").getAsDouble();
        int humidity = main.get("humidity").getAsInt();

        JsonObject wind = json.getAsJsonObject("wind");
        double windSpeed = wind.get("speed").getAsDouble();

        String weatherDescription = json.getAsJsonArray("weather")
                .get(0)
                .getAsJsonObject()
                .get("description")
                .getAsString();

        System.out.println("Weather Data for " + cityName);
        System.out.println("----------------------------");
        System.out.println("Temperature: " + temp + "°C");
        System.out.println("Feels Like: " + feelsLike + "°C");
        System.out.println("Humidity: " + humidity + "%");
        System.out.println("Wind Speed: " + windSpeed + " m/s");
        System.out.println("Description: " + weatherDescription);
    }
}
