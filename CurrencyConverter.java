import java.util.Scanner;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConverter {

    private static final String EXCHANGE_RATE_API_URL = "https://api.exchangeratesapi.io/latest";

    private static Map<String, Double> exchangeRates = new HashMap<>();

    public static double convertCurrency(String baseCurrency, String targetCurrency, double amount) throws IOException {
        // Fetch the exchange rate if it is not already cached
        if (!exchangeRates.containsKey(baseCurrency + "-" + targetCurrency)) {
            exchangeRates.put(baseCurrency + "-" + targetCurrency, getExchangeRate(baseCurrency, targetCurrency));
        }

        // Convert the amount using the cached exchange rate
        double exchangeRate = exchangeRates.get(baseCurrency + "-" + targetCurrency);
        return amount * exchangeRate;
    }

    private static double getExchangeRate(String baseCurrency, String targetCurrency) throws IOException {
        String url = EXCHANGE_RATE_API_URL + "?base=" + baseCurrency + "&symbols=" + targetCurrency;
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to fetch exchange rate: " + responseCode);
        }
        Scanner scanner = new Scanner(conn.getInputStream());
        String responseBody = scanner.useDelimiter("\\A").next();
        scanner.close();
        conn.disconnect();

        // Parse the JSON response body
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject rates = jsonObject.getJSONObject("rates");
        double exchangeRate = rates.getDouble(targetCurrency);
        return exchangeRate;
    }

    public static void main(String[] args) throws IOException {
        // Allow the user to choose the base currency and the target currency
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the base currency: ");
        String baseCurrency = scanner.nextLine();
        System.out.println("Enter the target currency: ");
        String targetCurrency = scanner.nextLine();

        // Fetch the real-time exchange rate
        double exchangeRate = getExchangeRate(baseCurrency, targetCurrency);

        // Take input from the user for the amount they want to convert
        System.out.println("Enter the amount to convert: ");
        double amount = scanner.nextDouble();

        // Convert the amount
        double convertedAmount = convertCurrency(baseCurrency, targetCurrency, amount);

        // Display the result
        System.out.println("The converted amount is: " + convertedAmount + " " + targetCurrency);
    }
}