/*
  Jeremy Chong, Krish Patel, and Mika Vohl
  06/14/2023
  Portfolio page
  This file displays the user's stock portfolio, which also shows the user's holdings and other stock information that they own.
*/

package frontend;
import backend.*;

import javax.sound.sampled.Port;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Portfolio extends JFrame implements ActionListener, Page { // This class inherits from JFrame and the interfaces ActionListener & Page
  // The following code initializes private instance variables for different Java Swing components
  private JPanel panel = null, stockContainer = null;
  private JButton returnButton = null;
  private JScrollPane scrollPane = null;
  private JLabel updatedMsg = null, overallPercent = null, overallMoney = null, portfolioValue = null, epsLabel = null, title = null, balanceLabel = null;
  // The following line initializes a decimal format that will be followed throughout the class
  public static final DecimalFormat df = new DecimalFormat("0.00");
  public String oldUpdateTime = null;
  private double balance = USER.getBalance();


  // Contructor that will initialize the state of the portfolio page
  public Portfolio() {
    // Set up the portfolio menu window
    setTitle("Portfolio Menu");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    setSize(1000, 650);
    setLocationRelativeTo(null);

    // Set up the panel and scroll pane
    panel = new JPanel(new GridBagLayout());
    scrollPane = new JScrollPane(panel);
    scrollPane.setPreferredSize(new Dimension(800, 600)); // Set preferred size of the scrollable area
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);

    gbc.gridx = 0;
    gbc.gridy = GridBagConstraints.RELATIVE;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0; // Occupy horizontal space

    // Set up fonts for labels and text
    Font titleFont = new Font("Arial", Font.BOLD, 27);
    Font subtitleFont = new Font("Arial", Font.BOLD, 22);
    Font subsubtitleFont = new Font("Arial", Font.BOLD, 20);
    Font boldText = new Font("Arial", Font.BOLD, 18);
    Font textFont = new Font("Arial", Font.PLAIN, 17);

    // Create the return button and add it to the panel
    returnButton = new JButton("Return to menu");
    returnButton.addActionListener(this);
    returnButton.setFont(textFont);
    panel.add(returnButton, gbc);

    // Create and style the portfolio title
    title = new JLabel(USER.getFirstName() + " " + USER.getLastName() + "'s Portfolio");
    title.setFont(titleFont);
    title.setForeground(Color.WHITE);
    panel.add(title, gbc);

    // Create and style the balance label
    balanceLabel = new JLabel("Current Balance: $" + df.format(balance));
    balanceLabel.setFont(subtitleFont);
    balanceLabel.setForeground(Color.WHITE);
    panel.add(balanceLabel, gbc);

    try {
      // Show message that displays the last time the stocks were updated with the API
      updatedMsg = new JLabel("Last updated: " + GetStockInfo.getLastUpdateTime("AAPL"));
      updatedMsg.setFont(subsubtitleFont);
      updatedMsg.setForeground(Color.WHITE);
      panel.add(updatedMsg, gbc);
      oldUpdateTime = updatedMsg.getText();
    } catch (Exception e) {
      updatedMsg = new JLabel(oldUpdateTime);
    }

    // Create and style labels for overall portfolio performance
    overallMoney = new JLabel("All Time Profit: $" + USER.getAllTimeUpOrDownMoney());
    // Set color based on profit/loss
    if (USER.getAllTimeUpOrDownMoney() < 0)
      overallMoney.setForeground(Color.RED);
    else if (USER.getAllTimeUpOrDownMoney() > 0)
      overallMoney.setForeground(Color.GREEN);
    else
      overallMoney.setForeground(Color.WHITE);
    overallMoney.setFont(subsubtitleFont);
    panel.add(overallMoney, gbc);

    overallPercent = new JLabel("All Time % Increase: " + USER.getAllTimeUpOrDownPercent() + "%");
    // Set color based on increase/decrease
    if (USER.getAllTimeUpOrDownPercent() < 0)
      overallPercent.setForeground(Color.RED);
    else if (USER.getAllTimeUpOrDownPercent() > 0)
      overallPercent.setForeground(Color.GREEN);
    else
      overallPercent.setForeground(Color.WHITE);
    overallPercent.setFont(subsubtitleFont);
    panel.add(overallPercent, gbc);

    portfolioValue = new JLabel("Portfolio Value: $" + USER.getTotalPortfolioValue());
    // Set color based on value
    if (USER.getTotalPortfolioValue() < 0)
      portfolioValue.setForeground(Color.RED);
    else
      portfolioValue.setForeground(Color.WHITE);
    portfolioValue.setFont(subsubtitleFont);
    panel.add(portfolioValue, gbc);

    ArrayList<Stock> sortedPortfolio = quickSortPortfolio(USER.getPortfolio(), 0, USER.getPortfolio().size()-1);

    // Create sub panels for each stock in the portfolio
    for (int i = 0; i < USER.getPortfolio().size(); i++) {
      Stock currentStock = USER.getPortfolio().get(i);
      stockContainer = new JPanel(new GridBagLayout());
      GridBagConstraints miniGBC = new GridBagConstraints();
      miniGBC.insets = new Insets(10, 10, 10, 10);
      miniGBC.gridx = 0;
      miniGBC.gridy = 0;
      miniGBC.fill = GridBagConstraints.HORIZONTAL;
      miniGBC.weightx = 1.0; // Occupy horizontal space

      // Create and style labels for stock information
      JLabel stockName = new JLabel(currentStock.getFullName());
      stockName.setFont(boldText);
      stockName.setForeground(Color.WHITE);

      JLabel stockTicker = new JLabel(currentStock.getTicker());
      stockTicker.setFont(boldText);
      stockTicker.setForeground(Color.WHITE);

      JLabel stockType = new JLabel("Type: " + currentStock.getType());
      stockType.setFont(boldText);
      stockType.setForeground(Color.WHITE);

      double sharePrice = currentStock.getPrice();
      JLabel stockValue = new JLabel("Price Per Share: $" + sharePrice);
      stockValue.setFont(textFont);
      stockValue.setForeground(Color.WHITE);

      JLabel totalLabel = new JLabel("Total Value: $" + df.format(sharePrice * currentStock.getShares()));
      totalLabel.setFont(textFont);
      totalLabel.setForeground(Color.WHITE);

      JLabel numShares = new JLabel(currentStock.getShares() + " shares");
      numShares.setFont(textFont);
      numShares.setForeground(Color.WHITE);

      stockContainer.add(stockName, miniGBC);
      miniGBC.gridy = 1;
      stockContainer.add(stockTicker, miniGBC);
      miniGBC.gridy = 2;
      stockContainer.add(stockType, miniGBC);
      miniGBC.anchor = GridBagConstraints.EAST;
      miniGBC.gridx = 1;
      miniGBC.gridy = 0;
      stockContainer.add(stockValue, miniGBC);
      miniGBC.gridx = 1;
      miniGBC.gridy = 1;
      stockContainer.add(numShares, miniGBC);
      miniGBC.gridx = 1;
      miniGBC.gridy = 2;
      stockContainer.add(totalLabel, miniGBC);

      if (currentStock.getType() == "SINGLE") {
        epsLabel = new JLabel("Earnings per share: $" + Double.toString(((SingleStock)currentStock).getEPS()));
        epsLabel.setForeground(Color.WHITE);
        epsLabel.setFont(textFont);
        miniGBC.gridx = 1;
        miniGBC.gridy = 4;
        stockContainer.add(epsLabel, miniGBC);
      }

      stockContainer.setBackground(Main.BACKGROUND_COLOR2);
      stockContainer.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10))); // Set margin of 10 pixels
      panel.add(stockContainer, gbc);
    }

    panel.setBackground(Main.BACKGROUND_COLOR);
    add(scrollPane);
  }

  @Override
  public void deletePage() {
    // Hide the portfolio menu and dispose the frame
    panel.setVisible(false);
    dispose();
  }

  @Override
  //Overriding the actionPreformed method from the ActionListener interface
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == returnButton) {
      // Return to the menu page and delete the portfolio menu
      new MenuPage().setVisible(true);
      deletePage();
    }
  }

  public static ArrayList<Stock> quickSortPortfolio(ArrayList<Stock> unsorted, int left, int right) {
    if (left >= right)
      return unsorted;

    final int FIRST_LEFT = left;
    final int FIRST_RIGHT = right;
    boolean leftSide = true;
    Stock temp = null;
    int pivot = 0;

    while (left != right) {
      if (leftSide) {
        // LEFT PIVOT
        pivot = left;
        if (unsorted.get(pivot).getTotalValue() < unsorted.get(right).getTotalValue()) {
          temp = unsorted.get(right);
          unsorted.set(right, unsorted.get(pivot));
          unsorted.set(pivot, temp);
          leftSide = false;
        } else {
          right--;
        }
      } else {
        // RIGHT PIVOT
        pivot = right;
        if (unsorted.get(pivot).getTotalValue() > unsorted.get(left).getTotalValue()) {
          temp = unsorted.get(left);
          unsorted.set(left, unsorted.get(pivot));
          unsorted.set(pivot, temp);
          leftSide = true;
        } else
          left++;
      }
    }

    // Recursively sort the left and right side of the array
    quickSortPortfolio(unsorted, FIRST_LEFT, pivot - 1); // left side of array
    quickSortPortfolio(unsorted, pivot + 1, FIRST_RIGHT); // right side of array

    return unsorted;
  }
}