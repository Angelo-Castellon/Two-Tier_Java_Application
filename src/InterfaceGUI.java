import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.sql.*;

public class InterfaceGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea commandTextArea;
    private JTable resultsTable;
    private JLabel statusLabel;
    private final Font font = new Font("Consolas", Font.BOLD, 15);

   public InterfaceGUI(JDBCHandler jdbcHandler, int width, int height, String[] urlProperty, String[] userProperty){


       setTitle("SQL CLIENT APPLICATION - (ARC - CNT 4714 - SPRING 2025 - PROJECT 3");
       setSize(width, height);
       setLayout(new BorderLayout());
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        JPanel bottomPanel = new JPanel();

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel leftLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel connectionLabel = new JLabel("Connection Details", SwingConstants.LEFT);
        connectionLabel.setFont(font);
        connectionLabel.setForeground(Color.BLUE);
        leftPanel.add(connectionLabel);

        JPanel urlPropertiesPanel = new JPanel();
        JLabel urlPropertiesLabel = new JLabel("DB URL Properties", SwingConstants.LEFT);
       JComboBox<String> urlPropertiesDropDown = new JComboBox<>(urlProperty);  urlPropertiesPanel.add(urlPropertiesLabel);
       urlPropertiesPanel.add(urlPropertiesDropDown);
       leftPanel.add(urlPropertiesPanel);

        JPanel userPropertiesPanel = new JPanel();
        JLabel userPropertiesLabel = new JLabel("User Properties", SwingConstants.LEFT);
        JComboBox<String> userPropertiesDropDown = new JComboBox<>(userProperty);
        userPropertiesPanel.add(userPropertiesLabel);
        userPropertiesPanel.add(userPropertiesDropDown);
        leftPanel.add(userPropertiesPanel);

        JPanel usernamePanel = new JPanel();
        JLabel usernameLabel = new JLabel("Username");
        usernameField = new JTextField(20);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);
        leftPanel.add(usernamePanel);

        JPanel passwordPanel = new JPanel();
        JLabel passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField(20);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        leftPanel.add(passwordPanel);

       // Connect and Disconnect buttons
       JButton connectButton = new JButton("Connect to Database");
       connectButton.setBackground(Color.BLUE);
       connectButton.setForeground(Color.WHITE);
       leftPanel.add(connectButton);

       JButton disconnectButton = new JButton("Disconnect from Database");
       disconnectButton.setBackground(Color.RED);
       disconnectButton.setForeground(Color.WHITE);
       leftPanel.add(disconnectButton);

       // Right Panel
       rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
       rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

       JLabel queryLabel = new JLabel("Enter SQL Command");
       commandTextArea = new JTextArea(10, 30);
       JScrollPane queryScrollPane = new JScrollPane(commandTextArea);
       rightPanel.add(queryLabel);
       rightPanel.add(queryScrollPane);

       JPanel rightButtonsPanel = new JPanel();
       JButton clearCommand = new JButton("Clear SQL Command");
       clearCommand.setBackground(Color.WHITE);
       clearCommand.setForeground(Color.RED);
       JButton executeCommand = new JButton("Execute SQL Command");
       executeCommand.setBackground(Color.GREEN);
       executeCommand.setForeground(Color.BLACK);
       rightButtonsPanel.add(clearCommand);
       rightButtonsPanel.add(executeCommand);
       rightPanel.add(rightButtonsPanel);

       // Bottom Panel
       bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
       bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
       bottomPanel.setPreferredSize(new Dimension(width, 250));

       statusLabel = new JLabel("NO CONNECTION", SwingConstants.CENTER);
       bottomPanel.add(statusLabel);

       resultsTable = new JTable();
       JScrollPane resultsScrollPane = new JScrollPane(resultsTable);
       bottomPanel.add(resultsScrollPane);

       JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
       JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

// Clear Result Window button
       JButton clearResult = new JButton("Clear Result Window");
       clearResult.setBackground(Color.YELLOW);  // Yellow background
       clearResult.setForeground(Color.BLACK);   // Black text
       leftButtonPanel.add(clearResult);

// Close Application button
       JButton closeApplication = new JButton("Close Application");
       closeApplication.setBackground(Color.RED);  // Red background
       closeApplication.setForeground(Color.BLACK); // Black text
       rightButtonPanel.add(closeApplication);

       JPanel buttonContainer = new JPanel(new BorderLayout());
       buttonContainer.add(leftButtonPanel, BorderLayout.WEST);
       buttonContainer.add(rightButtonPanel, BorderLayout.EAST);

       bottomPanel.add(buttonContainer, BorderLayout.SOUTH);

       connectButton.addActionListener(e -> {
           String urlProp = (String) urlPropertiesDropDown.getSelectedItem();
           String userProp = (String) userPropertiesDropDown.getSelectedItem();

           try {
               // Use the selected properties from the dropdowns
               String url = jdbcHandler.connect(urlProp, userProp);
               statusLabel.setText("CONNECTED TO: " + url);
           } catch (Exception ex) {
               statusLabel.setText("CONNECTION FAILED");
               ex.printStackTrace();
           }
       });

       disconnectButton.addActionListener(e -> {
           try {
               jdbcHandler.close();
               statusLabel.setText("DISCONNECTED");
           } catch (Exception ex) {
               statusLabel.setText("DISCONNECTION FAILED");
               ex.printStackTrace();
           }
       });


       clearCommand.addActionListener(e -> commandTextArea.setText(""));

       executeCommand.addActionListener(e -> {
           String command = commandTextArea.getText();
           if (command.isEmpty()) {
               JOptionPane.showMessageDialog(null, "No SQL command entered");
               return;
           }

           try {
               if (command.toUpperCase().startsWith("SELECT")) {
                   // Handle SELECT queries
                   ResultSet rs = jdbcHandler.executeQuery(command);

                   // Extract column names for the table model
                   ResultSetMetaData metaData = rs.getMetaData();
                   int columnCount = metaData.getColumnCount();
                   Vector<String> columnNames = new Vector<>();
                   for (int i = 1; i <= columnCount; i++) {
                       columnNames.add(metaData.getColumnName(i));
                   }

                   // Extract rows for the table model
                   Vector<Vector<Object>> data = new Vector<>();
                   while (rs.next()) {
                       Vector<Object> row = new Vector<>();
                       for (int i = 1; i <= columnCount; i++) {
                           row.add(rs.getObject(i));
                       }
                       data.add(row);
                   }

                   // Set the table model with the retrieved data
                   resultsTable.setModel(new DefaultTableModel(data, columnNames));

                   String currentUsername = (String) userPropertiesDropDown.getSelectedItem();
                   jdbcHandler.updateOperationsCount(currentUsername, false);
               } else {
                   // Handle non-SELECT queries (INSERT, UPDATE, DELETE)
                   int rowsAffected = jdbcHandler.executeUpdate(command);
                   statusLabel.setText("Query executed. Rows affected: " + rowsAffected);

                   String currentUsername = (String) userPropertiesDropDown.getSelectedItem();
                   jdbcHandler.updateOperationsCount(currentUsername, true);
               }
           } catch (SQLException sqle) {
               JOptionPane.showMessageDialog(null, sqle.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
           }
       });

       clearResult.addActionListener(e -> resultsTable.setModel(new DefaultTableModel()));

       closeApplication.addActionListener(e -> System.exit(0));

       setVisible(true);
   }
}
