import java.sql.*;

public class myJDBC {

    public static void main(String[] args) {

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
