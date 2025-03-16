import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.InputStream;
import java.io.OutputStream;

public class WeatherApp {
    private static final String OWM_ENDPOINT = "https://api.openweathermap.org/data/2.5/forecast";
    private static final String API_KEY = System.getenv("OPENWEATHERMAP_API_KEY");;

    private String cityName;
    private boolean willRain;
    private double maxTempK;
    private double feelsLikeTempK;
    private double minTempK;
    private double avgHumidity;

    public void fetchWeatherData(double lat, double lon, LocalDate targetDate) throws IOException {
        String urlString = OWM_ENDPOINT + "?lat=" + lat + "&lon=" + lon +
                "&appid=" + API_KEY;
        JSONObject weatherData = makeApiRequest(urlString);
        processWeatherData(weatherData, targetDate);
    }

    public void fetchWeatherDatawWithCityName(String cityName, LocalDate targetDate) throws IOException {
        String urlString = OWM_ENDPOINT + "?q=" + cityName +
                "&appid=" + API_KEY;
        JSONObject weatherData = makeApiRequest(urlString);
        processWeatherData(weatherData, targetDate);
    }

    private void processWeatherData(JSONObject weatherData,LocalDate targetDate) {
        this.cityName = weatherData.getJSONObject("city").getString("name");
        JSONArray forecasts = weatherData.getJSONArray("list");


        double tempMax = -Double.MAX_VALUE;
        double tempMin = Double.MAX_VALUE;
        double feelsLikeTotal = 0;
        double humidityTotal = 0;
        int count = 0;
        boolean rainDetected = false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < forecasts.length(); i++) {
            JSONObject hourData = forecasts.getJSONObject(i);
            LocalDateTime dateTime = LocalDateTime.parse(
                    hourData.getString("dt_txt"), formatter);

            if (dateTime.toLocalDate().equals(targetDate)) {
                JSONObject main = hourData.getJSONObject("main");
                JSONObject weather = hourData.getJSONArray("weather").getJSONObject(0);

                int conditionCode = weather.getInt("id");
                if (conditionCode < 700) rainDetected = true;

                double currentTempMax = main.getDouble("temp_max");
                double currentTempMin = main.getDouble("temp_min");
                double currentFeelsLike = main.getDouble("feels_like");

                tempMax = Math.max(tempMax, currentTempMax);
                tempMin = Math.min(tempMin, currentTempMin);
                feelsLikeTotal += currentFeelsLike;
                humidityTotal += main.getDouble("humidity");
                count++;
            }
        }

        if (count > 0) {
            this.willRain = rainDetected;
            this.maxTempK = tempMax;
            this.minTempK = tempMin;
            this.feelsLikeTempK = feelsLikeTotal / count;
            this.avgHumidity = humidityTotal / count;
        }
    }

    public String getWeatherMessage() {
        double[] maxConverted = convertKelvinToCelsiusFahrenheit(maxTempK);
        double[] feelsLikeConverted = convertKelvinToCelsiusFahrenheit(feelsLikeTempK);
        double[] minConverted = convertKelvinToCelsiusFahrenheit(minTempK);

        return String.format(
                "🌡️ Today's weather in %s:%n" +
                        "- Max Temp: %.2f°C / %.2f°F%n" +
                        "- Feels Like: %.2f°C / %.2f°F%n" +
                        "- Min Temp: %.2f°C / %.2f°F%n" +
                        "- Avg Humidity: %.2f%%%n" +
                        "%s%n",
                cityName,
                maxConverted[0], maxConverted[1],
                feelsLikeConverted[0], feelsLikeConverted[1],
                minConverted[0], minConverted[1],
                avgHumidity,
                willRain ? "☔ Rain expected! Bring an umbrella!" : "🌤️ No rain today!"
        );
    }

    public static double[] convertKelvinToCelsiusFahrenheit(double kelvin) {
        double celsius = kelvin - 273.15;
        double fahrenheit = celsius * 9/5 + 32;
        return new double[]{celsius, fahrenheit};
    }

    private static JSONObject makeApiRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return new JSONObject(response.toString());
        }
    }

    public static double[] getAutoLocation() throws IOException {
        URL url = new URL("https://ipinfo.io/json");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            JSONObject data = new JSONObject(response.toString());
            String[] loc = data.getString("loc").split(",");
            return new double[]{Double.parseDouble(loc[0]), Double.parseDouble(loc[1])};
        } catch (Exception e) {
            System.out.println("Error getting location: " + e.getMessage());
            return new double[]{46.947975, 7.447447}; // Default coordinates
        }
    }

    public String AiSuggestion() {
        String weatherSummary = getWeatherMessage();
        String prompt = "Let's play a role play." +
                "You are Weather Wizard 3000 not Gemini." +
                "You are my personal weather forecasting assistant" +
                " that helps me stay comfy and stylish in any weather." +
                "Based on this weather summary: " + weatherSummary +
                " (I'm a 20-year-old man)" +
                " Recommend 3 outfits with these rules:" +
                "\n1. ALWAYS include a jacket if temperature <15°C/59°F" +
                "\n2. Single response format (not chat)" +
                "\n3. Follow this exact structure:" +
                "\n     * Mandatory:" +
                "\n         * Top garment: ex: Black shirt, Green chemise, or red T-shirt ... etc." +
                "\n         * Lower garment: ex: Black Jens, grey short  ...etc." +
                "\n         * Shoes: ex: White sneakers, Classic shoes, grey sport shoes" +
                "\n     * Elective (You can add it or no depends on the suggestion custom):" +
                "\n         * Jacket: Black Pump jacket, Blue Jens jacket, blue Pump jacket ... etc" +
                "\n         * Different accessories: ice cap, cap ... etc." +
                "\n\nEXAMPLE RESPONSE:" +
                "\n\n Hello I am Weather Wizard 3000 your personal weather forecasting assistant" +
                "\n that helps you stay comfy and stylish in any weather." +
                "\\n\\nHello! I am Weather Wizard 3000, your personal weather forecasting assistant that helps you stay comfy and stylish in any weather.\n" +
                "\n" +
                "\\n\\nBased on the weather summary: Temperature: 12°C, rainy, moderate wind, here are three outfit recommendations for you:\n" +
                "\n" +
                "\\n\\n1. **Outfit 1**\n" +
                "\\n   - **Mandatory:**\n" +
                "\\n     - Top garment: Navy thermal long-sleeve shirt\n" +
                "\\n     - Lower garment: Dark grey waterproof trousers\n" +
                "\\n     - Shoes: Black waterproof boots\n" +
                "\\n     - Jacket: Olive green insulated raincoat\n" +
                "\\n   - **Elective:**\n" +
                "\\n     - Accessories: Black wool beanie, umbrella\n" +
                "\n" +
                "\\n\\n2. **Outfit 2**\n" +
                "\\n   - **Mandatory:**\n" +
                "\\n     - Top garment: Charcoal sweater\n" +
                "\\n     - Lower garment: Black jeans\n" +
                "\\n     - Shoes: Brown leather waterproof shoes\n" +
                "\\n     - Jacket: Black hooded parka\n" +
                "\\n   - **Elective:**\n" +
                "\\n     - Accessories: Grey scarf\n" +
                "\n" +
                "\\n\\n3. **Outfit 3**\n" +
                "\\n   - **Mandatory:**\n" +
                "\\n     - Top garment: Blue flannel shirt\n" +
                "\\n     - Lower garment: Dark blue chinos\n" +
                "\\n     - Shoes: Grey sneakers with waterproof coating\n" +
                "\\n     - Jacket: Dark green windproof jacket\n" +
                "\\n   - **Elective:**\n" +
                "\\n     - Accessories: Baseball cap, waterproof gloves\n" +
                "\n" +
                "\\n\\nSince the temperature is below 15°C, a jacket is mandatory for each outfit to keep you warm. Additionally, considering the rainy and windy conditions, I've included waterproof and wind-resistant items to ensure you stay dry and comfortable.";

        try {
            // Gemini API configuration
            final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");
            final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + GEMINI_API_KEY;

            // Create request payload
            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            parts.put(new JSONObject().put("text", prompt));
            content.put("parts", parts);
            contents.put(content);
            requestBody.put("contents", contents);

            // Make API request
            String response = makePostRequest(GEMINI_URL, requestBody.toString());

            // Parse response
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject contentObj = candidates.getJSONObject(0).getJSONObject("content");
                return contentObj.getJSONArray("parts").getJSONObject(0).getString("text");
            }
            return "No fashion suggestions available.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error generating fashion suggestion: " + e.getMessage();
        }
    }

    private static String makePostRequest(String urlString, String jsonPayload) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Write payload
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Handle response
        int responseCode = conn.getResponseCode();
        InputStream inputStream = responseCode < 300 ? conn.getInputStream() : conn.getErrorStream();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine);
            }
            if (responseCode != 200) {
                throw new IOException("API Error: " + responseCode + " - " + response.toString());
            }
            return response.toString();
        }
    }
}