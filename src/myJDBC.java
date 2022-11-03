import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

// Hi grader if you're seeing this file it's just me referencing my practice sheets!!! practicing connecting to database and whatnot

public class myJDBC {

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

    public static void main(String[] args) throws IOException{

        String filePath = "C:\\Users\\Minnie\\Desktop\\databaseManager\\src\\db.properties";

        Properties prop = readPropertiesFile("C:\\Users\\Minnie\\Desktop\\databaseManager\\src\\db.properties");
        System.out.println("username: " + prop.getProperty("username"));

//        String propertyUsername;
//        Properties prop = null;
//        prop = new Properties();
//        try {
//            FileInputStream ip = new FileInputStream(filePath);
//            prop.load(ip);
//            propertyUsername = prop.getProperty("MY_SQL_USERNAME");
//            System.out.println("****" + propertyUsername);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {



        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bikedb", "root", "Tumblr716!");

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("select * from bikes");

        while (resultSet.next()) {
            System.out.println(resultSet.getString("bikename"));
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
