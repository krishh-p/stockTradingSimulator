/*
  Jeremy Chong, Krish Patel, and Mika Vohl
  06/14/2023
  JsonTools
  This files provides us with tools to read a JSON file and add a JSON array of stocks to an ArrayList of portfolio
*/

package backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class JsonTools {
    // this function will retrieve a JSON from inputted file in the project
    public static String readJson(String file) {
        String jsonString = "";

        // read each line, adding it to jsonString
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = null;
            do {
                line = br.readLine();
                jsonString += line;
            } while(line != null);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        // return the string with the full JSON file
        return jsonString;
    }

    // this method will create a portfolio from a JSONArray
    // the user's portfolio is stored in a JSON array, this is not usable so we add everything in the JSON to an ArrayList full of the user's stocks
    public static ArrayList<Stock> createPortfolio(JSONArray jsonPortfolio) {
        // create an empty ArrayList to store the stocks
        ArrayList<Stock> portfolio = new ArrayList<>();
        // iterate over each element in the jsonPortfolio array
        for (int i = 0; i < jsonPortfolio.length(); i++) {
            // get current stock info
            JSONObject portfolioObject = jsonPortfolio.getJSONObject(i);
            String ticker = portfolioObject.keys().next();
            int quantity = portfolioObject.getInt(ticker);

            // if the stock is an ETF, add an ETF to the portfolio
            if(GetStockInfo.getType(ticker).equals("ETF")) {
                Stock stock = new ETF(ticker, quantity, "ETF");
                portfolio.add(stock);
            }
            // if the stock is a Single Stock, add a Single Stock to the portfolio
            else {
                Stock stock = new SingleStock(ticker, quantity, "SINGLE");
                portfolio.add(stock);
            }
        }
        // return the stock portfolio
        return portfolio;
    }
}
