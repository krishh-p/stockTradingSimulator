/*
  Jeremy Chong, Krish Patel, and Mika Vohl
  06/14/2023
  Stock
  This file is what is used to make a Stock abstract object in our project
*/

package backend;
import java.io.*;


// abstract class of Stock
// this means we can't create an instance of Stock
// SingleStock and ETF both inherit from this
public abstract class Stock {
    public String ticker = null;
    public int shares = 0;
    public String type = null;

    // access methods to see the state of the properties of the stock
    public String getTicker() {
        return this.ticker;
    }
    public int getShares() {
        return this.shares;
    }
    public String getType() {
        return this.type;
    }
    public void addShares(int shares) {
        this.shares += shares;
    }
    public void subtractShares(int shares) {
        this.shares -= shares;
    }

    // retrieves the price of the stock using the ticker symbol, uses method from GetStockInfo to do this
    public double getPrice() {
        try {
            return GetStockInfo.getPrice(this.ticker);
        }
        catch(Exception e) {
            return 0;
        }
    }

    // calculates the total value of the stock by multiplying the price with the number of shares.
    public double getTotalValue() {
        double total = -1;
        try {
            total = getPrice() * getShares();
        } catch (Exception err) {
            err.printStackTrace();
        }
        return total;
    }

    // gets the full name of the stock using the ticker symbol, uses method from GetStockInfo to do this
    public String getFullName() {
        try {
            return GetStockInfo.getFullName(this.ticker);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
