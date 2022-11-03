/*
  Name: Magdalena Dobinda
  Course: CNT 4714 Fall 2022
  Assignment title: Project 3 – A Two-tier Client-Server Application
  Date:  October 30, 2022

  Class:  Enterprise Computing
*/



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;


public class SQLClientApp extends JFrame {

    // Creating Labels
    private JLabel usernameLabel = new JLabel("Username", SwingConstants.CENTER);
    private JLabel passwordLabel = new JLabel("Password", SwingConstants.CENTER);
    private JLabel statusLabel = new JLabel("No connection", SwingConstants.CENTER);
    private JLabel propertiesLabel = new JLabel("Properties", SwingConstants.CENTER);

    // User, pass and text are
    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private JTextArea SQLcommand;

    // Drop down list
    String[] properties = {"root.properties", "client.properties"};
    private JComboBox propertiesList = new JComboBox(properties);

    // Buttons
    private JButton connectButton  = new JButton("Connect to Database");
    private JButton SQLclearButton = new JButton("Clear SQL Command");
    private JButton executeCommandButton  = new JButton("Execute SQL Command");
    private JButton clearResultButton  = new JButton("Connect to Database");

    // Table model
    private ResultSetTableModel modelTable;
    private JTable table;

    // headers
    JLabel leftHeader = new JLabel("Connection Details", SwingConstants.CENTER);
    JLabel rightHeader = new JLabel("Enter An SQL Command", SwingConstants.CENTER);
    JLabel resultHeader = new JLabel("SQL Execution Result Window");

    private Connection connection;
    private boolean connected = false;
    String filePath_root = "C:\\Users\\Minnie\\Desktop\\databaseManager\\src\\db.properties";
    String filePath_client = "C:\\Users\\Minnie\\Desktop\\databaseManager\\src\\client.properties";

    Properties rootUser = readPropertiesFile("C:\\Users\\Minnie\\Desktop\\databaseManager\\src\\db.properties");
    Properties clientUser = readPropertiesFile("C:\\Users\\Minnie\\Desktop\\databaseManager\\src\\client.properties");


    // reading in property files
    public static Properties readPropertiesFile(String fileName) throws IOException {
        FileInputStream fis = null;
        Properties prop = null;
        try {
            fis = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(fis);
        } catch(IOException fnfe) {
            fnfe.printStackTrace();
        } finally {
            fis.close();
        }
        return prop;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        SQLClientApp gui = new SQLClientApp();
        gui.setBackground(new Color(0xF7D1CD));
        gui.setVisible(true);
        gui.pack();
      //  gui.setLayout(new BorderLayout(1,0));
        gui.setResizable(true); // prevent from being resized

        //gui.setSize(500, 500); // sets the x and y dimension of our frame
    }

    public SQLClientApp() throws ClassNotFoundException, SQLException, IOException {
        this.init();

        connectButton.addActionListener(this.actionListener());
        connectButton.setBackground(new Color(0xD1B3C4));

        SQLclearButton.addActionListener(this.clearCommandListener());
    //    this.clear.setForeground(Color.RED);
        SQLclearButton.setBackground(new Color(0xD1B3C4));

        executeCommandButton.addActionListener(this.exececuteCommandListener());
     //   this.exec.setForeground(Color.BLACK);
        executeCommandButton.setBackground(new Color(0xD1B3C4));

        clearResultButton.addActionListener(this.clearResultListener());

//        int i = 2;
//        int j = 4;
//
//        JPanel[][] buttonPanel = new JPanel[i][j];
//        setLayout(new GridLayout(i,j));
//
//        for(int m = 0; m < i; m++) {
//            for(int n = 0; n < j; n++) {
//                buttonPanel[m][n] = new JPanel();
//                add(buttonPanel[m][n]);
//                buttonPanel[1][1].add(this.status);
//                buttonPanel[1][2].add(this.connect);
//                buttonPanel[1][3].add(this.clear);
//                buttonPanel[1][4].add(this.exec);
//            }
//        }


        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        buttonPanel.setBackground(new Color(0xF7D1CD));
        buttonPanel.add(statusLabel);
        buttonPanel.add(connectButton);
        buttonPanel.add(SQLclearButton);
        buttonPanel.add(executeCommandButton);


        JPanel labelFields = new JPanel(new GridLayout(4, 2));
        labelFields.setBackground(new Color(0xF7D1CD));
        labelFields.add(propertiesLabel);
        labelFields.add(propertiesList);
        labelFields.add(usernameLabel);
        labelFields.add(username);
        labelFields.add(passwordLabel);
        labelFields.add(password);

       // JPanel connectionStatus = new JPanel(new GridLayout(1,1));
      //  connectionStatus.add(this.status2);

        // all the stuff at the top
        JPanel topStuff = new JPanel(new GridLayout(2, 2));
        topStuff.setBackground(new Color(0xF7D1CD));
        topStuff.add(leftHeader);
        topStuff.add(rightHeader);
        topStuff.add(labelFields);
        topStuff.add(SQLcommand);

        // all the stuff at the bottom
        JPanel bottomStuff = new JPanel();
        bottomStuff.setBackground(new Color(0xF7D1CD));
        bottomStuff.setLayout(new BorderLayout(20,0));
        bottomStuff.add(resultHeader, BorderLayout.NORTH);
        bottomStuff.add(new JScrollPane(this.table), BorderLayout.CENTER);
        bottomStuff.add(this.clearResultButton, BorderLayout.SOUTH);

        add(topStuff, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(bottomStuff, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // close database connection if window closes
        addWindowListener(new WindowAdapter(){
            public void windowClosed(WindowEvent event){
                try {
                    if(!connection.isClosed())
                        connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    private ActionListener actionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {

                if(connected){
                    try{
                        connection.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    statusLabel.setText("Not Connected to Database");
                    statusLabel.setForeground(Color.RED);
                    table.setModel(new DefaultTableModel());
                    modelTable = null;
                    connected = false;
                }

                try{
                    Properties rootUser = readPropertiesFile("C:\\Users\\Minnie\\Desktop\\databaseManager\\src\\db.properties");
                    Class.forName(String.valueOf(rootUser.getProperty("URL")));
                } catch (ClassNotFoundException | IOException e) {
                    statusLabel.setText("Not Connected");
                    statusLabel.setForeground(Color.RED);
                    table.setModel(new DefaultTableModel());
                    modelTable = null;
                    e.printStackTrace();
                }

                try {
                    Properties rootUser = readPropertiesFile("C:\\Users\\Minnie\\Desktop\\databaseManager\\src\\db.properties");
                    Properties clientUser = readPropertiesFile("C:\\Users\\Minnie\\Desktop\\databaseManager\\src\\client.properties");
                    System.out.println("username: " + rootUser.getProperty("username"));

                    if(Objects.equals(username.getText(), rootUser.getProperty("username")) && (Objects.equals(String.valueOf(password.getPassword()), rootUser.getProperty("password"))  )) {

                        if(Objects.equals(String.valueOf(propertiesList.getSelectedItem()), "root.properties"))
                        {
                            String selectedItem = rootUser.getProperty("URL");
                            String operations = rootUser.getProperty("operations");
                            connection = DriverManager.getConnection(String.valueOf(selectedItem), rootUser.getProperty("username"), rootUser.getProperty("password"));
                            statusLabel.setText("Connected to " + selectedItem);
                            System.out.println("username: " + rootUser.getProperty("username"));
                            statusLabel.setForeground(new Color(0x325C44));
                            connected = true;
                            System.out.println("matches for root");
                        }

                    }
                    else {
                        System.out.println("no match for root");
                    }

                   if (Objects.equals(username.getText(), clientUser.getProperty("username")) && (Objects.equals(String.valueOf(password.getPassword()), clientUser.getProperty("password"))  )) {

                       if(Objects.equals(String.valueOf(propertiesList.getSelectedItem()), "client.properties")) {
                           String selectedItem = clientUser.getProperty("URL");
                           connection = DriverManager.getConnection(String.valueOf(selectedItem), clientUser.getProperty("username"), clientUser.getProperty("password"));
                           statusLabel.setText("Connected to " + selectedItem);
                           statusLabel.setForeground(new Color(0x325C44));
                           connected = true;
                           System.out.println("matches for client");
                       }
                    }
                   else {
                       System.out.println("no match for client");
                   }


                } catch (SQLException | IOException e) {
                    statusLabel.setText("Not Connected");
                    statusLabel.setForeground(Color.RED);
                    table.setModel(new DefaultTableModel());
                    modelTable = null;
                    e.printStackTrace();
                }
            }
        };
    }

    private ActionListener clearCommandListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a)
            {
                SQLcommand.setText("");
            }
        };
    }

    private ActionListener exececuteCommandListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a){
                if(connected && modelTable == null){
                    try {
                        modelTable = new ResultSetTableModel(SQLcommand.getText(), connection);
                        table.setModel(modelTable);
                    } catch (ClassNotFoundException | IOException | SQLException e) {
                        table.setModel(new DefaultTableModel());
                        modelTable = null;
                        JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    }
                } else {
                    if(connected){
                        String query = SQLcommand.getText();

                        if(query.contains("select")){
                            try {
                                modelTable.setQuery(query);
                            } catch (IllegalStateException | SQLException e) {
                                table.setModel(new DefaultTableModel());
                                modelTable = null;
                                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                modelTable.setUpdate(query);
                                table.setModel(new DefaultTableModel());
                                modelTable = null;
                            } catch (IllegalStateException | SQLException e) {
                                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        };
    }

    private ActionListener clearResultListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a){
                table.setModel(new DefaultTableModel());
                modelTable = null;
            }
        };
    }


    private void init(){

        statusLabel.setForeground(Color.RED);

        SQLcommand = new JTextArea(3, 80);
        SQLcommand.setWrapStyleWord(true);
        SQLcommand.setLineWrap(true);
        SQLcommand.setBackground(new Color(0xF3F3F3));

        Border border = BorderFactory.createLineBorder(Color.BLACK);
        SQLcommand.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        this.clearResultButton.setBackground(new Color(0xF3F3F3));
        this.table = new JTable();


    }

}