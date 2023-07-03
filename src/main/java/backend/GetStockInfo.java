package backend;

import java.io.*;
import java.net.*;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.*;
import java.text.*;

// this class will contain static variables and methods that will be used throughout the project
// it will get different pieces of stock data using APIs
public class GetStockInfo {
    // API key for Finnhub
    public final static String apiKey = new String("ci3o721r01ql8aaulc20ci3o721r01ql8aaulc2g");

    // this method will be called throughout this class to send and HTTP request to the Finnhub API and get the JSON of the information
    private static String getStockData(String ticker) throws Exception {
        // creates API URL at which the stock data is located in
        String apiUrl = "https://finnhub.io/api/v1/quote?symbol=" + ticker + "&token=" + apiKey;

        // sends an HTTP request to acquire the JSON
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = "";
        String line = null;
        do {
            line = br.readLine();
            response += line;
        } while(line != null);

        br.close();
        return response; // returns a string containing the full JSON file
    }
    // gets the price of the stock based on the ticker
    public static double getPrice(String ticker) throws Exception {
        String response = getStockData(ticker);
        JSONObject jsonObject = new JSONObject(response);
        return jsonObject.getDouble("c"); // "c" represents the current price in the response JSON
    }

    // get the full name of a stock based on its ticker symbol
    public static String getFullName(String ticker) throws Exception {
        // we must use two APIs as finnhub does not has access to ETF stock data
        String apiUrl = null;
        boolean isETF = false;
        if(GetStockInfo.getType(ticker).equals("ETF")) {
            apiUrl = "https://cloud.iexapis.com/stable/stock/" + ticker + "/company?token=pk_8d10e34ff65e445381cba45c5e979c84";
            isETF = true;
        }
        else {
            apiUrl = "https://finnhub.io/api/v1/stock/profile2?symbol=" + ticker + "&token=" + apiKey;
        }

        // sends an HTTP request to the corresponding URL, depending on if it's an ETF or not
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = "";
        String line = null;

        do {
            line = br.readLine();
            response += line;
        } while(line != null);
        br.close();

        JSONObject jsonObject = new JSONObject(response); // creates a JSONObject from the string containing the full JSON
        if(isETF)
            return jsonObject.getString("companyName"); // "companyName" represents the full name in the response JSON for IEX Cloud APU
        else
            return jsonObject.getString("name"); // "name" represents the full name in the response JSON for Finnhub APU
    }

    // determine if a stock is a single stock or an ETF based on its ticker symbol
    // get the type of the stock using Yahoo Finance API as other APIs don't have this data
    public static String getType(String ticker) {
        try {
            String apiUrl = "https://query1.finance.yahoo.com/v1/finance/search?q=" + ticker;

            // sends an HTTP request to the Yahoo Finance API and gets a JSON full of stock data
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = "";
            String line = null;

            do {
                line = br.readLine();
                response += line;
            } while(line != null);

            br.close();

            // make a JSON object from the API response
            JSONObject jsonObject = new JSONObject(response);

            // extract the "quotes" array from the JSON
            JSONArray quotes = jsonObject.getJSONArray("quotes");
            if (quotes.length() > 0) {
                // extract the first element from the "quotes" array
                JSONObject firstQuote = quotes.getJSONObject(0);
                String quoteType = firstQuote.getString("quoteType"); // quoteType contains the type of stock it is
                if (quoteType.equals("EQUITY")) { // "EQUITY" means its a single stock
                    return "Single";
                }
                else {
                    return "ETF";
                }
            }
            else {
                return "Unknown";
            }
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    // checks if a stock exists based on its ticker symbol
    public static boolean stockExists(String ticker) throws Exception {
        String response = getStockData(ticker);
        JSONObject jsonObject = new JSONObject(response);
        double price = jsonObject.getDouble("c");
        // if the cost of the stock is $0, that means it doesnt exist
        if(price == 0)
            return false;
        else
            return true;
    }
    // get the last time the stock data was updated
    public static String getLastUpdateTime(String ticker) throws Exception {
        String response = getStockData(ticker);
        JSONObject jsonObject = new JSONObject(response);
        long timestamp = jsonObject.getLong("t"); // "t" represents the timestamp of the last update in the response JSON
        return convertTimestampToString(timestamp); // convert the timestamp to a readable date and time format
    }

    // convert timestamp to a readable date and time format
    // it will take in a unix timestamp (time elapsed since 00:00:00 UTC on January 1 1970) and provide a readable date
    private static String convertTimestampToString(long timestamp) {
        long timestampMillis = timestamp * 1000L;

        // create a Date object from the timestamp
        Date date = new Date(timestampMillis);

        // set the format for the date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy, hh:mm:ss a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        // implement the format for the date and time
        String formattedDateTime = dateFormat.format(date);
        return formattedDateTime;
    }
}