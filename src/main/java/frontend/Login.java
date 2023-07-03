/*
  Jeremy Chong, Krish Patel, and Mika Vohl
  14/09/2023
  Login page
  This file allows the user log into the stock trading simulator after registering through the PHP website.
*/

package frontend;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import backend.*;
import org.json.*;

public class Login extends JFrame implements ActionListener { // This class inherits from JFrame and the interface ActionListener
  // In the following lines, different swing component variables are initialized to be accessed anywhere within the class, but not outside of it
  private JPanel panel = null;
  private JButton loginButton = null;
  private JLabel title = null, userLabel = null, passwordLabel = null, loginMsg = null;
  private JTextField usernameField = null;
  private JPasswordField passwordField = null;
  private MenuPage mainMenu = null;
  // In the following lines, we are initializing variables that will hold useful user information
  public JSONObject foundUser = null;
  public User currentUser = null;
  String enteredUsername = null, enteredPassword = null;

  // Login() is a constructor that initializes the state of the page when it is instantiated
  public Login() {

    setTitle("Login page");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Stops application when the "x" button is pressed
    // The code below instantiates the formatting of the entire window
    setResizable(false);
    setSize(1000, 650);
    setLocationRelativeTo(null);

    panel = new JPanel(); // Adds a new Panel object that will contain different swing components
    panel.setLayout(new GridBagLayout()); // Sets the layout that components in the panel will follow

    // The following code creates positioning anchors for the layout of components
    GridBagConstraints gbc = new GridBagConstraints(); //Java swing layout
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.insets = new Insets(10, 10, 10, 10); //Adds padding of 10px

    // The following creates font presets
    Font textFont = new Font("Arial", Font.PLAIN, 24);
    Font titleFont = new Font("Arial", Font.BOLD, 30);

    // The following code handles menu title creation and styling
    title = new JLabel("Start your stock trading journey!");
    title.setForeground(Color.RED);
    gbc.gridx = 0; //x-position
    gbc.gridy = 0; //y-position
    gbc.gridwidth = 3; //setting the width of label
    title.setFont(titleFont);
    panel.add(title, gbc);

    // The following code handles username label creation and styling
    userLabel = new JLabel("Username:");
    userLabel.setForeground(Color.WHITE);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    userLabel.setFont(textFont);
    panel.add(userLabel, gbc);

    // The following code handles username input field creation and styling
    usernameField = new JTextField(20);
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    usernameField.setColumns(10);
    usernameField.setFont(textFont);
    panel.add(usernameField, gbc);

    // The following code handles password label creation and styling
    passwordLabel = new JLabel("Password:");
    passwordLabel.setForeground(Color.WHITE);
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    passwordLabel.setFont(textFont);
    panel.add(passwordLabel, gbc);

    // The following code handles password input field creation and styling
    passwordField = new JPasswordField(20);
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    passwordField.setColumns(10);
    passwordField.setFont(textFont);
    panel.add(passwordField, gbc);

    // The following code handles login button creation and styling
    loginButton = new JButton("Login");
    loginButton.setFont(textFont);
    loginButton.addActionListener(this); // listens for activity (pressing the button) and reports it to a function below
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    panel.add(loginButton, gbc);

    // The following code handles login status message creation and styling
    loginMsg = new JLabel("");
    loginMsg.setFont(textFont);
    loginMsg.setForeground(Color.RED);
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 3;
    panel.add(loginMsg, gbc);

    //styling the panel and adding it to the JFrame
    panel.setBackground(Main.BACKGROUND_COLOR);
    add(panel);
  }

  public static String decrypt(String word, int key) { // This method decrypts passwords encrypted with the encrypt method
    String decrypted = "";
    int ascii = 0;
    char letter = ' ';
    for (int i = 0; i < word.length(); i++) {
      ascii = word.charAt(i) - key;
      letter = (char) ascii;
      decrypted += letter;
    }
    return decrypted; // returns the decrypted password
  }

  //Overriding the actionPreformed method from the ActionListener interface
  public void actionPerformed(ActionEvent e) { // This method processes actions/input made to an action listener (ex. Button)
    String filePath = Server.fileName; // Saves the file path to the json file
    String jsonString = null;
    try {
      jsonString = new String(Files.readAllBytes(Paths.get(filePath))); // Reads the json file into a string
    }catch(IOException err){
      err.printStackTrace(); // If an error occurs, it is printed
    }
    JSONObject accountsObj = new JSONObject(jsonString); // Creates a JSONObject that represents a json file in the form of an object
    JSONArray users = accountsObj.getJSONArray("users"); // Creates an array of user's JSON information from the JSONObject

    if (e.getSource() == loginButton) { // If the login button is pressed
      enteredUsername = usernameField.getText();
      enteredPassword = new String(passwordField.getPassword());
      usernameField.setText("");
      passwordField.setText("");
      // find the user with the username in the JSON
      foundUser = null; // store the found user's data in JSONObject

      for(int i = 0; i < users.length(); i++) { // loops through each of the users, looking for the username
        JSONObject user = users.getJSONObject(i);
        String currUsername = user.getString("username");
        //If the username matches
        if(currUsername.equals(enteredUsername)) {
          //Checking if the password is correct
          String currPassword = decrypt(user.getString("password"), 2);
          foundUser = user;
          if (currPassword.equals(enteredPassword)) {
            loginMsg.setText("");
            setVisible(false);
            JSONArray extractedPortfolio = user.getJSONArray("stocks");
            currentUser = new User(user.getString("username"), user.getString("first"), user.getString("last"), user.getDouble("balance"), JsonTools.createPortfolio(extractedPortfolio)); // Creates a new User object
            mainMenu = new MenuPage(); // Creates a new menu page object
            mainMenu.setVisible(true); // Ensures the menu page is visible
          }
          else {
            loginMsg.setText("WRONG PASSWORD, TRY AGAIN"); // If the password is incorrect, tell the user
          }
          break;
        }
      }
      //If the user was not found
      if (foundUser == null)
        loginMsg.setText("USER NOT FOUND, REGISTER ON THE PHP WEBSITE"); // If the username is not registered, tell the user
    }
  }
}