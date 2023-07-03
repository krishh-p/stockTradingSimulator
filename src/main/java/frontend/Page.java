/*
  Jeremy Chong, Krish Patel, and Mika Vohl
  06/14/2023
  Page Interface
  This file acts as a guideline to creating pages.
*/

package frontend;

import backend.User;

public interface Page {
    final User USER = Main.loginPage.currentUser; // Defines a constant that references the current user who is signed in
    void deletePage(); // Tells child classes that they must create a deletePage() method

}
