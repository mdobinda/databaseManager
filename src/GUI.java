
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


public class GUI extends JFrame {


    private JLabel driver, dbURL, usernameLbl, passwordLbl, status, status2, propertiesLabel;
    private JComboBox drivers, urlList, propertiesList;
    private JTextField username;
    private JPasswordField password;
    private JTextArea cmd;
    private JButton connect, clear, exec, clearRes, testButton;
    private ResultSetTableModel modelTable;
    private JTable table;
    private Connection conn;
    private boolean connected = false;
    String filePath_root = "C:\\Users\\Minnie\\Desktop\\databaseManager\\src\\db.properties";
    String filePath_client = "C:\\Users\\Minnie\\Desktop\\databaseManager\\src\\client.properties";


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
        GUI gui = new GUI();
        gui.setBackground(new Color(0xF7D1CD));
        gui.setVisible(true);
        gui.pack();
      //  gui.setLayout(new BorderLayout(1,0));
        gui.setResizable(true); // prevent from being resized

        //gui.setSize(500, 500); // sets the x and y dimension of our frame
    }

    public GUI() throws ClassNotFoundException, SQLException, IOException {
        this.init();

        this.connect.addActionListener(this.connectListener());
        this.connect.setBackground(new Color(0xD1B3C4));

        this.clear.addActionListener(this.clearCmdListener());
    //    this.clear.setForeground(Color.RED);
        this.clear.setBackground(new Color(0xD1B3C4));

        this.exec.addActionListener(this.execCmdListener());
     //   this.exec.setForeground(Color.BLACK);
        this.exec.setBackground(new Color(0xD1B3C4));

        this.clearRes.addActionListener(this.clearResultListener());

//        int i = 2;
//        int j = 4;
//
//        JPanel[][] btnPanel = new JPanel[i][j];
//        setLayout(new GridLayout(i,j));
//
//        for(int m = 0; m < i; m++) {
//            for(int n = 0; n < j; n++) {
//                btnPanel[m][n] = new JPanel();
//                add(btnPanel[m][n]);
//                btnPanel[1][1].add(this.status);
//                btnPanel[1][2].add(this.connect);
//                btnPanel[1][3].add(this.clear);
//                btnPanel[1][4].add(this.exec);
//            }
//        }



        JPanel btnPanel = new JPanel(new GridLayout(1, 4));
        btnPanel.setBackground(new Color(0xF7D1CD));
        btnPanel.add(this.status);
        btnPanel.add(this.connect);
        btnPanel.add(this.clear);
        btnPanel.add(this.exec);



        JLabel leftHeader = new JLabel("Connection Details", SwingConstants.CENTER);
     //   leftHeader.setForeground(Color.BLUE);
        JLabel rightHeader = new JLabel("Enter An SQL Command", SwingConstants.CENTER);
   //     rightHeader.setForeground(Color.BLUE);
        JLabel resultHeader = new JLabel("SQL Execution Result Window");
   //     resultHeader.setForeground(Color.BLUE);

        JPanel lblFields = new JPanel(new GridLayout(4, 2));
        lblFields.setBackground(new Color(0xF7D1CD));


        lblFields.add(this.propertiesLabel);
        lblFields.add(this.propertiesList);
        lblFields.add(this.usernameLbl);
        lblFields.add(this.username);
        lblFields.add(this.passwordLbl);
        lblFields.add(this.password);

        JPanel connectionStatus = new JPanel(new GridLayout(1,1));
        connectionStatus.add(this.status2);

        JPanel north = new JPanel(new GridLayout(2, 2));
        north.setBackground(new Color(0xF7D1CD));
        north.add(leftHeader);
        north.add(rightHeader);
        north.add(lblFields);
        north.add(this.cmd);
       // north.add(resultHeader);

        JPanel south = new JPanel();
        south.setBackground(new Color(0xF7D1CD));    
        south.setLayout(new BorderLayout(20,0));
        south.add(resultHeader, BorderLayout.NORTH);
        south.add(new JScrollPane(this.table), BorderLayout.CENTER);
        south.add(this.clearRes, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(btnPanel, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter(){
            public void windowClosed(WindowEvent event){
                try {
                    if(!conn.isClosed())
                        conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    private ActionListener connectListener() {


        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {

                if(connected){
                    try{
                        conn.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    status.setText("Not Connected");
                    status.setForeground(Color.RED);
                    table.setModel(new DefaultTableModel());
                    modelTable = null;
                    connected = false;
                }

                try{
                    Class.forName(String.valueOf(drivers.getSelectedItem()));
                } catch (ClassNotFoundException e) {
                    status.setText("Not Connected");
                    status.setForeground(Color.RED);
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
                            conn = DriverManager.getConnection(String.valueOf(selectedItem), rootUser.getProperty("username"), rootUser.getProperty("password"));
                            status.setText("Connected to " + selectedItem);
                            System.out.println("username: " + rootUser.getProperty("username"));
                            status.setForeground(new Color(0x325C44));
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
                           conn = DriverManager.getConnection(String.valueOf(selectedItem), clientUser.getProperty("username"), clientUser.getProperty("password"));
                           status.setText("Connected to " + selectedItem);
                           status.setForeground(new Color(0x325C44));
                           connected = true;
                           System.out.println("matches for client");
                       }
                    }
                   else {
                       System.out.println("no match for client");
                   }



                } catch (SQLException | IOException e) {
                    status.setText("Not Connected");
                    status.setForeground(Color.RED);
                    table.setModel(new DefaultTableModel());
                    modelTable = null;
                    e.printStackTrace();
                }
            }
        };
    }

    private ActionListener clearCmdListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a)
            {
                cmd.setText("");
            }
        };
    }

    private ActionListener execCmdListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a){
                if(connected && modelTable == null){
                    try {
                        modelTable = new ResultSetTableModel(cmd.getText(), conn);
                        table.setModel(modelTable);
                    } catch (ClassNotFoundException | IOException | SQLException e) {
                        table.setModel(new DefaultTableModel());
                        modelTable = null;
                        JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    }
                } else {
                    if(connected && modelTable != null){
                        String q = cmd.getText();

                        if(q.contains("select")){
                            try {
                                modelTable.setQuery(q);
                            } catch (IllegalStateException | SQLException e) {
                                table.setModel(new DefaultTableModel());
                                modelTable = null;
                                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                modelTable.setUpdate(q);
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


    private void init() throws ClassNotFoundException, SQLException, IOException {

        this.driver = new JLabel("JDBC Driver");
        this.dbURL = new JLabel("Database URL");
        this.propertiesLabel = new JLabel("Properties", SwingConstants.CENTER);
        this.usernameLbl = new JLabel("Username", SwingConstants.CENTER);
        this.passwordLbl = new JLabel("Password", SwingConstants.CENTER);
        this.status = new JLabel("No connection", SwingConstants.CENTER);
        this.status.setForeground(Color.RED);
        this.status2 = new JLabel("No connection");
        this.status2.setForeground(Color.RED);
        this.status2 = new JLabel("No connection");
        this.status2.setForeground(Color.RED);

        String[] driver = {"com.mysql.jdbc.Driver", ""};
        String[] url = {"jdbc:mysql://localhost:3306/project3", "jdbc:mysql://127.0.0.1:3306/project3"};
        String[] properties = {"root.properties", "client.properties"};

        this.drivers = new JComboBox(driver);
        this.drivers.setSelectedIndex(0);
        this.urlList = new JComboBox(url);
        this.propertiesList = new JComboBox(properties);

        this.username = new JTextField();
        this.password = new JPasswordField();

        this.cmd = new JTextArea(3, 80);
        this.cmd.setWrapStyleWord(true);
        this.cmd.setLineWrap(true);
        this.cmd.setBackground(new Color(0xF3F3F3));


        Border border = BorderFactory.createLineBorder(Color.BLACK);
        cmd.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        this.connect = new JButton("Connect to Database");
        this.clear = new JButton("Clear SQL Command");
        this.exec = new JButton("Execute SQL Command");
        this.clearRes = new JButton("Clear Result Window");
        this.clearRes.setBackground(new Color(0xF3F3F3));

        this.table = new JTable();
    }

}