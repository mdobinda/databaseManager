

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


public class GUI extends JFrame {
    private JLabel driver, dbURL, usernameLbl, passwordLbl, status;
    private JComboBox drivers, urlList;
    private JTextField username;
    private JPasswordField password;
    private JTextArea cmd;
    private JButton connect, clear, exec, clearRes;
    private SQLTableModel modelTable;
    private JTable table;
    private Connection conn;
    private boolean connected = false;

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        GUI gui = new GUI();
        gui.setVisible(true);
        gui.pack();
        gui.setLayout(new BorderLayout(2,0));
    }

    public GUI() throws ClassNotFoundException, SQLException, IOException {
        this.init();

        this.connect.addActionListener(this.connectListener());
        this.connect.setForeground(Color.YELLOW);
        this.connect.setBackground(Color.BLUE);

        this.clear.addActionListener(this.clearCmdListener());
        this.clear.setForeground(Color.RED);
        this.clear.setBackground(Color.WHITE);

        this.exec.addActionListener(this.execCmdListener());
        this.exec.setForeground(Color.BLACK);
        this.exec.setBackground(Color.GREEN);

        this.clearRes.addActionListener(this.clearResultListener());

        JPanel btnPanel = new JPanel(new GridLayout(1, 4));
        btnPanel.add(this.status);
        btnPanel.add(this.connect);
        btnPanel.add(this.clear);
        btnPanel.add(this.exec);

        JLabel leftHeader = new JLabel("Enter Database Information");
        leftHeader.setForeground(Color.BLUE);
        JLabel rightHeader = new JLabel("Enter A SQL Command");
        rightHeader.setForeground(Color.BLUE);
        JLabel resultHeader = new JLabel("Execution Result Window");
        resultHeader.setForeground(Color.BLUE);

        JPanel lblFields = new JPanel(new GridLayout(4, 2));
        lblFields.add(this.driver);
        lblFields.add(this.drivers);
        lblFields.add(this.dbURL);
        lblFields.add(this.urlList);
        lblFields.add(this.usernameLbl);
        lblFields.add(this.username);
        lblFields.add(this.passwordLbl);
        lblFields.add(this.password);

        JPanel north = new JPanel(new GridLayout(2, 2));
        north.add(leftHeader);
        north.add(rightHeader);
        north.add(lblFields);
        north.add(this.cmd);

        JPanel south = new JPanel();
        south.setLayout(new BorderLayout(20,0));
        south.add(resultHeader);
        south.add(this.clearRes, BorderLayout.SOUTH);
        south.add(new JScrollPane(this.table), BorderLayout.NORTH);

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
            public void actionPerformed(ActionEvent a){
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
                    conn = DriverManager.getConnection(String.valueOf(urlList.getSelectedItem()), username.getText(), password.getText());
                    status.setText("Connected to " + String.valueOf(urlList.getSelectedItem()));
                    status.setForeground(Color.GREEN);
                    connected = true;
                } catch (SQLException e) {
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
                        modelTable = new SQLTableModel(cmd.getText(), conn);
                        table.setModel(modelTable);
                    } catch (ClassNotFoundException | IOException | SQLException e) {
                        table.setModel(new DefaultTableModel());
                        modelTable = null;
                        JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    }
                } else {
                    if(connected && modelTable != null){
                        String q = cmd.getText().toLowerCase();

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
        this.usernameLbl = new JLabel("Username");
        this.passwordLbl = new JLabel("Password");
        this.status = new JLabel("No connection");
        this.status.setForeground(Color.RED);

        String[] driver = {"com.mysql.jdbc.Driver", ""};
        String[] url = {"jdbc:mysql://localhost:3306/project3", "jdbc:mysql://127.0.0.1:3306/project3"};

        this.drivers = new JComboBox(driver);
        this.drivers.setSelectedIndex(0);
        this.urlList = new JComboBox(url);

        this.username = new JTextField();
        this.password = new JPasswordField();

        this.cmd = new JTextArea(3, 80);
        this.cmd.setWrapStyleWord(true);
        this.cmd.setLineWrap(true);

        this.connect = new JButton("Connect to DB");
        this.clear = new JButton("Clear Command");
        this.exec = new JButton("Execute");
        this.clearRes = new JButton("Clear Results");

        this.table = new JTable();
    }

}