/*
  Jeremy Chong, Krish Patel, and Mika Vohl
  06/14/2023
  Menu page
  This file displays the menu for the user to navigate through each page.
*/

package frontend;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPage extends JFrame implements ActionListener { // This class inherits from JFrame and the interface ActionListener
    // The following code initializes private instance variables for different Java Swing components
    private JPanel panel = null;
    private JButton tradeButton =  null, logoutButton = null, viewPortfolioButton = null;
    private JLabel title = null;
    // The following code initializes variables for the different pages
    private Portfolio userPortfolio = null;
    private Trade tradeMenu = null;

    // Contructor that will initialize the state of the menu page
    public MenuPage() {
        // Set up the menu page window
        setTitle("Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        // Set up the panel and its layout
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Set up fonts for buttons and title
        Font buttonFont = new Font("Arial", Font.PLAIN, 24);
        Font titleFont = new Font("Arial", Font.BOLD, 40);

        // Menu title creation and styling
        title = new JLabel("Stock Trading Simulator");
        title.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 0;
        title.setFont(titleFont);
        panel.add(title, gbc);

        // View portfolio button creation and styling
        viewPortfolioButton = new JButton("View Portfolio");
        viewPortfolioButton.setFont(buttonFont);
        viewPortfolioButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.ipadx = 50; // Increases the button width
        gbc.ipady = 20; // Increases the button height
        viewPortfolioButton.setPreferredSize(new Dimension(200, 30));
        panel.add(viewPortfolioButton, gbc);

        // Trade button creation and styling
        tradeButton = new JButton("Trade");
        tradeButton.setFont(buttonFont);
        tradeButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 2;
        tradeButton.setPreferredSize(new Dimension(200, 30));
        panel.add(tradeButton, gbc);

        // Logout button creation and styling
        logoutButton = new JButton("Logout");
        logoutButton.setFont(buttonFont);
        logoutButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 3;
        logoutButton.setPreferredSize(new Dimension(200, 30));
        panel.add(logoutButton, gbc);

        // Styling the panel and adding it to the JFrame
        panel.setBackground(Main.BACKGROUND_COLOR);
        add(panel);
    }

    //Overriding the actionPreformed method from the ActionListener interface
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewPortfolioButton) {
            // Disable the button and create a new portfolio if necessary
            viewPortfolioButton.setVisible(false);
            viewPortfolioButton.setEnabled(false);
            if (userPortfolio == null) {
                userPortfolio = new Portfolio();
            }
            // Hide the menu and display the user's portfolio
            setVisible(false);
            userPortfolio.setVisible(true);
            // Re-enable the button
            viewPortfolioButton.setEnabled(true);
        } else if (e.getSource() == tradeButton) {
            // Display the trade menu
            setVisible(false);
            // Check if the trade menu is already created, if not, create it
            if (tradeMenu == null)
                tradeMenu = new Trade();
            tradeMenu.setVisible(true);
        } else if (e.getSource() == logoutButton) {
            // Hide the menu and display the login page
            setVisible(false);
            Main.loginPage.currentUser = null;
            Main.loginPage.setVisible(true);
        }
    }
}