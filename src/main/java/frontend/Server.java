/*
  Jeremy Chong, Krish Patel, and Mika Vohl
  06/14/2023
  Server code
  This code gets the JSON file from the PHP website through HTTP requests.
*/

package frontend;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import backend.Stock;
import org.json.*;

class Server {
  public static String jsonContent = null;
  public static String fileName = "accounts.json";


  public static void getJSONFromPHP() {
    try {
      String websiteURL = "https://stocksummative.mikavohl.repl.co/data.php"; //URL to JSON file from PHP

      URL url = new URL(websiteURL);
      //Sending an HTTP request
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      // Check the response status code
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        // Read the response body as a JSON string
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();

        //Read the JSON file from the connection established
        String line = null;
        do {
          line = reader.readLine();

          if (line != null)
            response.append(line);
        } while(line != null);
        reader.close();

        //Process the JSON data as needed
        jsonContent = response.toString();
        updateAccounts(jsonContent);

      }

      // Close the connection
      connection.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void updateAccounts(String usersJson) {

    try {
      File accountsFile = new File(fileName);

      if (!accountsFile.exists()) {
        accountsFile.createNewFile();
        JSONObject json = new JSONObject();
        json.put("users", new JSONArray()); // Create an empty array for "users"

        try (FileWriter fileWriter = new FileWriter(fileName)) {
          fileWriter.write(json.toString());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      // Read the existing accounts.json file
      String accountsJson = new String(Files.readAllBytes(Paths.get(fileName)));

      // Parse the accountsJson string into a JSONObject
      JSONObject accountsObject = new JSONObject(accountsJson);

      // Get the "users" array from the accountsObject
      JSONArray usersArray = accountsObject.getJSONArray("users");

      // Parse the usersJson string into a JSONObject
      JSONObject usersObject = new JSONObject(usersJson);

      // Get the "users" array from the usersObject
      JSONArray newUsersArray = usersObject.getJSONArray("users");

      // Iterate over the new users and check if they exist in the accounts.json file
      for (int i = 0; i < newUsersArray.length(); i++) {
        JSONObject newUser = newUsersArray.getJSONObject(i);
        String newUsername = newUser.getString("username");

        // Check if the new user exists in the accounts.json file
        boolean userExists = false;
        for (int j = 0; j < usersArray.length(); j++) {
          JSONObject existingUser = usersArray.getJSONObject(j);
          String existingUsername = existingUser.getString("username");

          if (newUsername.equals(existingUsername)) {
            userExists = true;
            break;
          }
        }

        // If the new user doesn't exist, add it to the usersArray
        if (!userExists) {
          // Set the balance and stocks for the new user
          newUser.put("balance", 10000);
          newUser.put("stocks", new ArrayList<Stock>());
          // Add the new user to the usersArray
          usersArray.put(newUser);

        }
      }

      // Update the "users" array in the accountsObject
      accountsObject.put("users", usersArray);

      // Write the updated accounts.json file
      FileWriter fileWriter = new FileWriter(fileName);
      fileWriter.write(accountsObject.toString(4));
      fileWriter.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}