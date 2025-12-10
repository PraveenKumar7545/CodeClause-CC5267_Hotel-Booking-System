/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package forum01.hotelmanagement;

/**
 *
 * @author Praveen
 */
public class Session {
    
    private static boolean loggedIn = false;
    private static String username = "";

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        Session.loggedIn = loggedIn;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Session.username = username;
    }
    
}
