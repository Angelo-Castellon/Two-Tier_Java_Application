/*
Name: Angelo Castellon
Course: CNT 4714 Spring 2025
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: March 14, 2025
Class: Enterprise Computing
*/
import javax.swing.*;

public class Project3Application {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                JDBCHandler jdbcHandler = new JDBCHandler();

                String[] urlProperty = {"project3.properties", "bikedb.properties"};
                String[] userProperty = {"root.properties", "client1.properties", "client2.properties"};
                InterfaceGUI gui = new InterfaceGUI(jdbcHandler, 800, 600, urlProperty, userProperty);
                gui.setTitle("SQL CLIENT APPLICATION - (ARC - CNT 4714 - SPRING 2025 - PROJECT 3");
                gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gui.pack();  // Adjust size based on components
                gui.setVisible(true);  // Make the frame visible

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
