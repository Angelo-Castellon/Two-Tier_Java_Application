/*
Name: Angelo Castellon
Course: CNT 4714 Spring 2025
Assignment title: Project 3 – A Specialized Accountant Application
Date: March 14, 2025
Class: Enterprise Computing
*/

import javax.swing.*;

public class theAccountant {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                JDBCHandler jdbcHandler = new JDBCHandler();
                AccountantInterfaceGUI gui = new AccountantInterfaceGUI(jdbcHandler, 800, 600);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
