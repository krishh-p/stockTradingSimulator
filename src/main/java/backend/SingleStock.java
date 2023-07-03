/*
  Jeremy Chong, Krish Patel, and Mika Vohl
  06/14/2023
  SingleStock
  This file is what is used to make an SingleStock object in our project
*/

package backend;

import java.net.*;
import java.io.*;
import org.json.JSONObject;
import org.json.JSONArray;

// SingleStock inherits abstract class Stock
public class SingleStock extends Stock {
    double EPS = 0;

    // SingleStock constructors
    public SingleStock(String ticker, int shares, String type) {
        this.ticker = ticker;
        this.shares = shares;
        this.type = type;
        this.EPS = retrieveEPS(this.ticker);
    }

    public SingleStock(String ticker, int shares) {
        this.ticker = ticker;
        this.shares = shares;
        this.type = "UNKNOWN";
        this.EPS = retrieveEPS(this.ticker);
    }

    // access method to get the earnings per share of a single stock
    public double getEPS() {
        return this.EPS;
    }

    // method to retrieve the EPS from Yahoo Finance API
    private double retrieveEPS(String ticker) {
        try {
            // sends an HTTP request to the Yahoo Finance API
            String apiUrl = "https://query1.finance.yahoo.com/v10/finance/quoteSummary/" + ticker + "?modules=defaultKeyStatistics";

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

            // response holds the entire JSON file in string format now

            // make a JSON object from the API response
            JSONObject jsonObject = new JSONObject(response);

            JSONArray resultArray = jsonObject.getJSONObject("quoteSummary").getJSONArray("result");
            if (resultArray.length() > 0) { // if the array is not empty
                // goes through and finding the trailingEPS property of the stock
                JSONObject resultObject = resultArray.getJSONObject(0);
                JSONObject keyStatistics = resultObject.getJSONObject("defaultKeyStatistics");
                if (keyStatistics.has("trailingEps")) {
                    JSONObject trailingEpsObject = keyStatistics.getJSONObject("trailingEps");
                    if (trailingEpsObject.has("raw")) {
                        return trailingEpsObject.getDouble("raw");
                    }
                }
            }

            return 0;

        }
        catch (Exception e) {
            return 0;
        }
    }
}