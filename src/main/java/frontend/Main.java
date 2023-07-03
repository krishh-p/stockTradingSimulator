/*
  Jeremy Chong, Krish Patel, and Mika Vohl
  14/09/2023
  Main Source File
  This file holds and initializes the start of the program.
*/

package frontend;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Main {
  public static Login loginPage = null; // Creates a reference variable that will point to a login page object
  // Declares constants for often used background colors
  public final static Color BACKGROUND_COLOR = new Color(38, 38, 38);
  public final static Color BACKGROUND_COLOR2 = new Color(91, 123, 122);

  public static void main(String[] args) {
    Server.getJSONFromPHP(); // Retrieves current JSON information from the PHP website
    loginPage = new Login(); // Creates a new Login object
    loginPage.setVisible(true); // Ensures the login page is visible
  }
}
