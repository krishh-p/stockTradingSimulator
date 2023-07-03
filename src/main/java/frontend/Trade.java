/*
  Jeremy Chong, Krish Patel, and Mika Vohl
  06/14/2023
  Trade page
  This file displays the trade menu for the user. This allows them to buy and sell various stocks.
*/

package frontend;
import backend.GetStockInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Trade extends JFrame implements ActionListener, Page {
  //Variables for the main content of the Trade page
  private JPanel panel = null;
  private JButton returnButton = null;
  private JButton tradeButton = null;
  private JLabel title = null;
  private JRadioButton buyButton = null;
  private JRadioButton sellButton = null;
  private ButtonGroup options = null;
  private JLabel stockLabel = null;
  private JTextField stockName = null;
  private JLabel quantityLabel = null;
  private JTextField quantityInput = null;
  private JButton priceButton = null;
  private JLabel tradeMsg = null;
  private JLabel balanceLabel = null;
  //Variables for the detailed side panel for the current stock the user is buying/selling
  private JPanel detailPanel = null;
  private JLabel fullStockName = null;
  private JLabel stockSymbol = null;
  private JLabel pricePerShare = null;
  private JLabel typeStock = null;
  private JLabel currOwnedShares = null;
  private JLabel sideBarLabel = null;
  private JLabel feeWarning = null;
  String tickerSymbol = null;


  //Constructor
  public Trade() {
    //Frame styling
    setTitle("Trading Menu");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    setSize(1000, 650);
    setLocationRelativeTo(null);

    //Creating main panel
    panel = new JPanel(new GridBagLayout());

    //Creating the side panel
    detailPanel = new JPanel(new GridBagLayout());

    //Layout for the main panel
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);

    gbc.gridx = 0;
    gbc.gridy = GridBagConstraints.RELATIVE;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0; // Occupy horizontal space

    //Fonts
    Font titleFont = new Font("Arial", Font.BOLD, 27);
    Font radFont = new Font("Arial", Font.ITALIC, 25);
    Font boldText = new Font("Arial", Font.BOLD, 20);
    Font textFont = new Font("Arial", Font.PLAIN, 20);
    Font sideBarTitle = new Font("Arial", Font.BOLD, 35);
    Font sideBarInfo = new Font("Arial", Font.PLAIN, 27);

    //Main panel content
    returnButton = new JButton("Return to menu");
    returnButton.setFont(textFont);
    returnButton.addActionListener(this);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.weighty = 0.0;
    panel.add(returnButton, gbc);

    balanceLabel = new JLabel("Balance: $"+Portfolio.df.format(USER.getBalance()));
    balanceLabel.setFont(titleFont);
    balanceLabel.setForeground(Color.WHITE);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    panel.add(balanceLabel, gbc);

    //Title creation and styling
    title = new JLabel("Current Trades");
    title.setForeground(Color.WHITE);
    title.setFont(titleFont);
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    panel.add(title, gbc);

    //Creating and styling the buy/sell options
    options = new ButtonGroup();

    buyButton = new JRadioButton("Buy", true);
    buyButton.setBackground(Main.BACKGROUND_COLOR);
    buyButton.setForeground(Color.WHITE);
    buyButton.setFont(radFont);
    options.add(buyButton);
    gbc.gridx = 0;
    gbc.gridy = 3;
    panel.add(buyButton, gbc);

    sellButton = new JRadioButton("Sell");
    sellButton.setBackground(Main.BACKGROUND_COLOR);
    sellButton.setForeground(Color.WHITE);
    sellButton.setFont(radFont);
    options.add(sellButton);
    gbc.gridx = 1;
    gbc.gridy = 3;
    panel.add(sellButton, gbc);

    buyButton.addActionListener(this);
    sellButton.addActionListener(this);

    //Stock input for what the user wants to buy/sell
    stockLabel = new JLabel("Enter the ticker symbol:");
    stockLabel.setForeground(Color.WHITE);
    stockLabel.setFont(boldText);
    gbc.gridx = 0;
    gbc.gridy = 4;
    panel.add(stockLabel, gbc);

    stockName = new JTextField();
    stockName.setFont(textFont);
    gbc.gridx = 1;
    gbc.gridy = 4;
    panel.add(stockName, gbc);

    //Checking for the price of stock
    priceButton = new JButton("Select Stock");
    priceButton.setFont(textFont);
    priceButton.addActionListener(this);
    gbc.gridx = 1;
    gbc.gridy = 5;
    panel.add(priceButton, gbc);

    //Giving a warning if the user wants to buy an ETF
    feeWarning = new JLabel("*ETFs have a buy fee of 0.5%*");
    feeWarning.setVisible(false);
    feeWarning.setFont(textFont);
    feeWarning.setForeground(Color.RED);
    gbc.gridx = 0;
    gbc.gridy = 5;
    panel.add(feeWarning, gbc);

    //Input for how much the user wants to buy/sell
    quantityLabel = new JLabel("Enter quantity:");
    quantityLabel.setForeground(Color.WHITE);
    quantityLabel.setFont(boldText);
    gbc.gridx = 0;
    gbc.gridy = 6;
    panel.add(quantityLabel, gbc);

    quantityInput = new JTextField();
    quantityInput.setFont(textFont);
    gbc.gridx = 1;
    gbc.gridy = 6;
    panel.add(quantityInput, gbc);

    quantityInput.setVisible(false);
    quantityLabel.setVisible(false);

    //Submitting the trade form
    tradeButton = new JButton("Buy");
    tradeButton.setFont(textFont);
    tradeButton.setPreferredSize(new Dimension(200, 30));
    tradeButton.setVisible(false);
    tradeButton.addActionListener(this);
    gbc.gridx = 1;
    gbc.gridy = 7;
    gbc.weightx = 0;
    panel.add(tradeButton, gbc);

    //Message that tells the user whether or not the trade was successful
    tradeMsg = new JLabel("");
    tradeMsg.setFont(boldText);
    gbc.gridx = 1;
    gbc.gridy = 8;
    panel.add(tradeMsg, gbc);

    //Detail sidebar creation and styling
    GridBagConstraints gbc2 = new GridBagConstraints();
    gbc2.insets = new Insets(10, 10, 10, 10);
    gbc2.gridx = 0;
    gbc2.gridy = GridBagConstraints.RELATIVE;
    gbc2.fill = GridBagConstraints.HORIZONTAL;
    gbc2.weightx = 1.0; // Occupy horizontal space
    //Title for sidebar
    sideBarLabel = new JLabel("<html>Current stock information:</html>");
    sideBarLabel.setForeground(Color.WHITE);
    sideBarLabel.setFont(sideBarTitle);

    //Actual details about the stock
    fullStockName = new JLabel("<html>No stock selected</html>");
    fullStockName.setForeground(Color.WHITE);
    fullStockName.setFont(sideBarInfo);

    stockSymbol = new JLabel();
    stockSymbol.setForeground(Color.WHITE);
    stockSymbol.setFont(sideBarInfo);

    pricePerShare = new JLabel();
    pricePerShare.setForeground(Color.WHITE);
    pricePerShare.setFont(sideBarInfo);

    currOwnedShares = new JLabel();
    currOwnedShares.setForeground(Color.WHITE);
    currOwnedShares.setFont(sideBarInfo);

    typeStock = new JLabel();
    typeStock.setForeground(Color.WHITE);
    typeStock.setFont(sideBarInfo);

    //Adding all content to sidebar
    detailPanel.add(sideBarLabel, gbc2);
    detailPanel.add(fullStockName, gbc2);
    detailPanel.add(stockSymbol, gbc2);
    detailPanel.add(pricePerShare, gbc2);
    detailPanel.add(currOwnedShares, gbc2);
    detailPanel.add(typeStock, gbc2);

    //Adding the panels to the JFrame and styling them
    detailPanel.setBackground(Main.BACKGROUND_COLOR2);
    detailPanel.setPreferredSize(new Dimension(300, getHeight()));

    panel.setBackground(Main.BACKGROUND_COLOR);
    panel.setPreferredSize(new Dimension(700, getHeight()));

    setLayout(new BorderLayout());
    //Adding the panels to the frames
    add(panel, BorderLayout.EAST);
    add(detailPanel, BorderLayout.CENTER);
  }

  @Override
  public void deletePage() {
    panel.setVisible(false);
    detailPanel.setVisible(false);
    dispose();
  }

  @Override
  //Overriding the method from the ActionListener interface
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == buyButton) {
      tradeButton.setText("Buy");
    } else if (e.getSource() == sellButton) {
      tradeButton.setText("Sell");
    }
    // Rest of the code...
    if (e.getSource() == returnButton) {
      new MenuPage().setVisible(true);
      deletePage();
    }
    if (e.getSource() == priceButton) {
      //do stuff if price button is pressed
      try {
        feeWarning.setVisible(false);
        tickerSymbol = stockName.getText().toUpperCase().trim();
        if(GetStockInfo.stockExists(tickerSymbol)){
          if(sellButton.isSelected())
            tradeButton.setText("Sell");
          else if(buyButton.isSelected())
            tradeButton.setText("Buy");
            if(GetStockInfo.getType(tickerSymbol).equals("ETF"))
              feeWarning.setVisible(true);
          //Side bar details
          fullStockName.setForeground(Color.WHITE);
          fullStockName.setText("<html>" + GetStockInfo.getFullName(tickerSymbol) + "</html>");
          stockSymbol.setText(tickerSymbol);
          pricePerShare.setText("$"+GetStockInfo.getPrice(tickerSymbol)+"/share");
          if(USER.getNumberOfShares(tickerSymbol) > 0)
            currOwnedShares.setText("<html>Currently owned shares: " + USER.getNumberOfShares(tickerSymbol) + "</html>");
          else
            currOwnedShares.setText("<html>You do not currently own any "+tickerSymbol+" shares</html>");
          typeStock.setText("Type: " + GetStockInfo.getType(tickerSymbol));

          detailPanel.setVisible(true);
          quantityLabel.setVisible(true);
          quantityInput.setVisible(true);
          tradeButton.setVisible(true);
        }
        else{
          fullStockName.setForeground(Color.RED);
          fullStockName.setText("<html>Stock does not exist!</html>");
          stockSymbol.setText("");
          pricePerShare.setText("");
          currOwnedShares.setText("");
          typeStock.setText("");
          quantityLabel.setVisible(false);
          quantityInput.setVisible(false);
          tradeButton.setVisible(false);
          tradeMsg.setText("");

        }
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }

    }
    else if (e.getSource() == tradeButton) {

      //If the user wants to buy
      if (buyButton.isSelected()) {
        try {
          if(USER.buyStock(Integer.parseInt(quantityInput.getText()), tickerSymbol)){
            tradeMsg.setForeground(Color.GREEN);
            tradeMsg.setText("Successful Purchase!");
            tradeButton.setVisible(false);
            quantityInput.setVisible(false);
            quantityLabel.setVisible(false);
          }
          else{
            tradeMsg.setForeground(Color.RED);
            tradeMsg.setText("Insufficient Funds");
          }
        }catch(Exception err){
          tradeMsg.setText("Invalid Input");
          tradeMsg.setForeground(Color.RED);
        }
      }
      //If the user wants to sell
      else if (sellButton.isSelected()) {
        try {
          if (USER.sellStock(Integer.parseInt(quantityInput.getText()), tickerSymbol)) {
            tradeMsg.setForeground(Color.GREEN);
            tradeMsg.setText("Successful Sell!");
          }
          else {
            tradeMsg.setForeground(Color.RED);
            tradeMsg.setText("Insufficient Stocks");
          }
        } catch (Exception err) {
          tradeMsg.setText("Invalid Input");
          tradeMsg.setForeground(Color.RED);
        }
      }

      balanceLabel.setText("Balance: $"+Portfolio.df.format(USER.getBalance()));
      if(USER.getNumberOfShares(tickerSymbol) > 0)
        currOwnedShares.setText("<html>Currently owned shares: " + USER.getNumberOfShares(tickerSymbol) + "</html>");
      else
        currOwnedShares.setText("<html>You do not currently own any "+tickerSymbol+" shares</html>");
    }
  }
}