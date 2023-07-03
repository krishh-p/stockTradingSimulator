/*
  Jeremy Chong, Krish Patel, and Mika Vohl
  06/14/2023
  ETF
  This file is what is used to make an ETF object in our project
*/

package backend;

// ETF inherits abstract class Stock
public class ETF extends Stock {
    // 0.5% buy and sell tax on ETFs
    public static final float feePercent = 0.5f;

    // ETF constructor, all of these instance variables are initialized in the abstract Stock class that ETF inherits from
    public ETF(String ticker, int shares, String type) {
        this.ticker = ticker;
        this.shares = shares;
        this.type = type;
    }
}