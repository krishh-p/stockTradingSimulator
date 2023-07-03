/*
  Jeremy Chong, Krish Patel, and Mika Vohl
  06/14/2023
  User
  This file is what is used to make an User object in our project
*/

package backend;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.*;
import java.io.*;
import java.lang.*;

public class User {
    private String username = null;
    private String firstName = null;
    private String lastName = null;
    private double balance = 0;
    private ArrayList<Stock> portfolio = null;

    // creates a user object using the following parameters
    public User(String uName, String fName, String lName, double balance, ArrayList<Stock> stocks) {
        this.username = uName;
        this.firstName = fName;
        this.lastName = lName;
        this.balance = balance;
        this.portfolio = stocks;
    }

    // access methods for all the parameters
    public String getUsername() {
        return this.username;
    }
    public String getFirstName() {
        return this.firstName;
    }
    public String getLastName() {
        return this.lastName;
    }
    public double getBalance() {
        return this.balance;
    }
    public ArrayList<Stock> getPortfolio() {
        return this.portfolio;
    }


    // this will be called when the user buys a stock, it takes in the number of shares and ticker symbol
    // false means buyStock is unsuccessful, true means it is successful
    public boolean buyStock(int shares, String ticker) {
        try {
            if (!GetStockInfo.stockExists(ticker)) { // if the stock doesn't exist, it returns false
                return false;
            }

            double balanceChecker = 0.0;

            // ETFs will have an added 0.5% tax when buying it
            if (GetStockInfo.getType(ticker).equals("ETF")) {
                balanceChecker = GetStockInfo.getPrice(ticker) * shares * (1+((double)ETF.feePercent/100));
            }
            else {
                balanceChecker = GetStockInfo.getPrice(ticker) * shares;
            }

            // checking to see if the user has enough money to buy the stock
            if (this.balance >= balanceChecker) {

                // updates user's balance
                this.balance -= balanceChecker;

                // updates user's portfolio
                boolean stockInPortfolio = false;

                // checks if the stock is in the portfolio
                // if it is, it increases the shares in the portfolio
                for (int i = 0; i < this.portfolio.size(); i++) {
                    Stock currStock = this.portfolio.get(i);
                    if (currStock.getTicker().equals(ticker)) {
                        stockInPortfolio = true;
                        currStock.addShares(shares);
                        break;
                    }
                }
                // if the stock isn't in the portfolio
                if (!stockInPortfolio) {
                    Stock stock = null;
                    if (GetStockInfo.getType(ticker).equals("ETF")) {
                        stock = new ETF(ticker, shares, "ETF");
                    }
                    else if (GetStockInfo.getType(ticker).equals("Single")){
                        stock = new SingleStock(ticker, shares, "SINGLE");
                    }
                    else { // if the stock is not an ETF or Single
                        stock = new SingleStock(ticker, shares);
                    }
                    this.portfolio.add(stock);
                }
                // updates the JSON with new balance and portfolio
                updateBalanceJSON();
                addStockJSON(ticker, shares);
                return true;
            }
            return false;
        }
        catch(Exception e) {
            return false;
        }
    }

    // this will be called when the user sells a stock, it takes in the number of shares and ticker symbol
    // false means sellStock is unsuccessful, true means it is successful
    public boolean sellStock(int shares, String ticker) throws Exception {
        // sorts the portfolio
        ArrayList<Stock> sortedTickerPortfolio = sortPortfolio(this.portfolio, 0, this.portfolio.size()-1);
        // uses binary search to see if the user has the stock in their portfolio
        int indexOfStock = binarySearch(sortedTickerPortfolio, ticker);
        // -1 means that the stock is not the portfolio
        if(indexOfStock == -1) return false;
        Stock stockToSell = sortedTickerPortfolio.get(indexOfStock);

        if (indexOfStock != -1) { // if stock is found in the portfolio
            if (shares > stockToSell.getShares()) {
                return false; // return false if not enough shares to sell
            }

            // update user's balance
            double stockPrice = GetStockInfo.getPrice(ticker);
            this.balance += stockPrice * shares;

            stockToSell.subtractShares(shares);

            // check if all shares of the stock have been sold
            if (stockToSell.getShares() == 0) {
                this.portfolio.remove(stockToSell);
            }

            // updates the JSON will new balance and portfolio
            updateBalanceJSON();
            removeStockJSON(ticker, shares);
            return true;
        }
        return false;
    }

    // method to add a stock into the JSON
    private void addStockJSON(String ticker, int shares) {
        try {
            // reasons the json and stores it all in a string
            String jsonContent = JsonTools.readJson("accounts.json");
            JSONObject jsonObject = new JSONObject(jsonContent);

            // find the user with the given username
            JSONArray users = jsonObject.getJSONArray("users");
            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                if (user.getString("username").equals(this.username)) {
                    // find the stock in the user's stocks array
                    JSONArray stocks = user.getJSONArray("stocks");
                    boolean stockFound = false;
                    for (int j = 0; j < stocks.length(); j++) {
                        JSONObject stock = stocks.getJSONObject(j);
                        if (stock.has(ticker)) {
                            // update the stock count
                            int currentCount = stock.getInt(ticker);
                            stock.put(ticker.toUpperCase(), currentCount + shares);
                            stockFound = true;
                            break;
                        }
                    }
                    if (!stockFound) {
                        // add a new stock entry
                        JSONObject stock = new JSONObject();
                        stock.put(ticker, shares);
                        stocks.put(stock);
                    }
                    break;
                }
            }

            // write the updated JSON back to the file
            BufferedWriter bw = new BufferedWriter(new FileWriter("accounts.json"));
            bw.write(jsonObject.toString(4));
            bw.close();
        }

        catch (JSONException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // method to remove a stock into the JSON
    private void removeStockJSON(String ticker, int shares) {
        try {
            String jsonContent = JsonTools.readJson("accounts.json");
            JSONObject jsonObject = new JSONObject(jsonContent);

            // find the user with the given username
            JSONArray users = jsonObject.getJSONArray("users");
            for(int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                if(user.getString("username").equals(this.username)) {
                    // find the stock in the user's stocks array
                    JSONArray stocks = user.getJSONArray("stocks");
                    for(int j = 0; j < stocks.length(); j++) {
                        JSONObject stock = stocks.getJSONObject(j);
                        if(stock.has(ticker)) {
                            int currentCount = stock.getInt(ticker);
                            if(currentCount == shares){
                                // if all shares are sold, remove the stock from JSON
                                stocks.remove(j);
                            }
                            else {
                                // reduce the number of shares
                                stock.put(ticker, currentCount - shares);
                            }
                            break;
                        }
                    }
                    break;
                }
            }

            // write the updated JSON back to the file
            BufferedWriter bw = new BufferedWriter(new FileWriter("accounts.json"));
            bw.write(jsonObject.toString(4));
            bw.close();
        }
        catch (JSONException e) {
            System.out.println(e.getMessage());
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateBalanceJSON() throws IOException {
        String jsonString = JsonTools.readJson("accounts.json");
        JSONObject jsonObject = new JSONObject(jsonString);

        JSONArray users = jsonObject.getJSONArray("users");
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (user.getString("username").equals(this.username)) {
                // Update the balance for the user
                user.put("balance", this.balance);
                break;
            }
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter("accounts.json"));
        bw.write(jsonObject.toString());
        bw.close();
    }

    // this quicksort method will sort the portfolio in alphabetical order based on their ticker
    // we have this so we can binary search to find whether the stock is in the portfolio and if it is, which index is it at
    private static ArrayList<Stock> sortPortfolio(ArrayList<Stock> unsorted, int left, int right) {
        if (left >= right)
            return unsorted;

        final int FIRST_LEFT = left;
        final int FIRST_RIGHT = right;
        boolean leftSide = true;
        Stock temp = null;
        int pivot = 0;

        while (left != right) {
            if(leftSide) {
                // LEFT PIVOT
                pivot = left;
                if(unsorted.get(pivot).getTicker().compareTo(unsorted.get(right).getTicker()) > 0) {
                    temp = unsorted.get(right);
                    unsorted.set(right, unsorted.get(pivot));
                    unsorted.set(pivot, temp);
                    leftSide = false;
                }
                else {
                    right--;
                }
            }

            else {
                // RIGHT PIVOT
                pivot = right;
                if(unsorted.get(pivot).getTicker().compareTo(unsorted.get(left).getTicker()) < 0) {
                    temp = unsorted.get(left);
                    unsorted.set(left, unsorted.get(pivot));
                    unsorted.set(pivot, temp);
                    leftSide = true;
                }
                else
                    left++;
            }
        }
        sortPortfolio(unsorted, FIRST_LEFT, pivot - 1); // left side of array
        sortPortfolio(unsorted, pivot + 1, FIRST_RIGHT); // right side of array
        return unsorted;
    }

    // this binary search will go through the portfolio and find the Stock with the corresponding ticker
    // it will return the index of the stock, if it returns -1, that means that the stock is not in the portfolio
    public static int binarySearch(ArrayList<Stock> portfolio, String ticker) {
        int left = 0;
        int right = portfolio.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Stock midStock = portfolio.get(mid);
            int comparison = midStock.getTicker().compareTo(ticker);

            if (comparison == 0) {
                // ticker found
                return portfolio.indexOf(midStock);
            }
            else if (comparison < 0) {
                // ticker may be in the right half
                left = mid + 1;
            }
            else {
                // ticker may be in the left half
                right = mid - 1;
            }
        }
        // ticker not found
        return -1;
    }

    // this method will return the number of shares that the User has of a certain stock, take in the ticker
    public int getNumberOfShares(String ticker) {
        ArrayList<Stock> sortedTickerPortfolio = sortPortfolio(this.portfolio, 0, this.portfolio.size()-1);
        int indexOfStock = binarySearch(sortedTickerPortfolio, ticker);
        if(indexOfStock == -1) {
            return -1;
        }
        else {
            Stock stock = sortedTickerPortfolio.get(indexOfStock);
            return stock.getShares();
        }
    }

    // this method will return the total portfolio value that the user has
    public double getTotalPortfolioValue() {
        return Math.round(calculateTotalPortfolioValue(this.portfolio, 0, 0) * 100.0) / 100.0;
    }

    // this method is used to perform the calculations to get the total portfolio value
    private double calculateTotalPortfolioValue(ArrayList<Stock> portfolio, int index, double portfolioValue) {
        if (index >= portfolio.size()) {
            return portfolioValue;
        }
        Stock currStock = portfolio.get(index);
        double currStockPrice = currStock.getPrice() * currStock.getShares();
        portfolioValue += currStockPrice;

        // recursive function to go through each stock in the portfolio
        return calculateTotalPortfolioValue(portfolio, index+1, portfolioValue);
    }

    // this method how much money the user is up or down all time, can be compared to original $10000 that user gets in the beginning
    public double getAllTimeUpOrDownMoney() {
        double portfolioValue = getTotalPortfolioValue();
        double totalAccountValue = portfolioValue + this.balance;

        return Math.round((totalAccountValue - 10000)*100.0) / 100.0;
    }

    // this method how much percent the user is up or down all time, can be compared to original $10000 that user gets in the beginning
    public double getAllTimeUpOrDownPercent() {
        double value = 100 * (getAllTimeUpOrDownMoney() / 10000);
        return Math.round(value * 100.0) / 100.0;
    }
}