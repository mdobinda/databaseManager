
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

public class ResultSetTableModel extends AbstractTableModel {
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private int numberOfRows;

    //keep track of database connection status.
    private boolean isConnected = false;

    //constructor initializes resultSet and obtains its metaData object;
    //determines number of rows.
    public ResultSetTableModel(String query, Connection c) throws SQLException, ClassNotFoundException, IOException {
        try {
            this.connection = c;
            if(this.connection.isClosed())
                return;

            isConnected = true;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // toLowerCase to support uppercase instances
            // note that this will not be done ahead of time in processing as to not add incorrect case senstive updates to database
            if(query.toLowerCase().contains("select")){
                try {
                    setQuery(query);
                } catch (IllegalStateException | SQLException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            } else {
                try {
                    setUpdate(query);
                } catch (IllegalStateException | SQLException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }


        } catch (IllegalStateException | SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    //get class that represents column type
    public Class getColumnClass(int columnIndex) throws IllegalStateException {
        //ensure database connection is available
        if(!isConnected)
            throw new IllegalStateException("Database not connected.");
        //determine Java class of column
        try {
            String className = metaData.getColumnClassName(columnIndex + 1);
            return Class.forName(className);
        }  //end try
        catch (Exception e) {
            e.printStackTrace();
        }  //end catch

        //if problems occur above, assume type Object
        return Object.class;
    }

    //get number of columns in ResultSet
    public int getColumnCount() throws IllegalStateException {
        //ensure database connection is available
        if(!isConnected)
            throw new IllegalStateException("Not Connected to Database.");

        //determine number of columns
        try {
            return metaData.getColumnCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //if problems occur above, return 0 for number of columns
        return 0;
    }

    //get name of a particular column in ResultSet
    public String getColumnName(int column) throws IllegalStateException {

        //ensure database connections is available
        if(!isConnected)
            throw new IllegalStateException("Not Connected to Database.");
        //determine column name
        try {
            return metaData.getColumnName(column + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //if problem occurs above, return empty string for column name
        return "";
    }

    //return number of rows in ResultSet
    public int getRowCount() throws IllegalStateException {
        //ensure database connection is available
        if(!isConnected)
            throw new IllegalStateException("Not Connected to Database.");
        return numberOfRows;
    }

    //obtain value in particular row and column
    public Object getValueAt(int rowIndex, int columnIndex) throws IllegalStateException {

        //ensure database connection is available
        if(!isConnected)
            throw new IllegalStateException("Database not connected.");

        //obtain a value at specified ResultSet row and column
        try {
            resultSet.next();
            resultSet.absolute(rowIndex + 1);
            return resultSet.getObject(columnIndex + 1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //if problem occurs above, return empty string
        return "";
    }

    //set new database query string
    public void setQuery(String query) throws SQLException, IllegalStateException {

        //ensure database connection is available
        if(!isConnected)
            throw new IllegalStateException("Database not connected.");

        //specify query and execute it
        resultSet = statement.executeQuery(query);

        //obtain metaData for ResultSet
        metaData = resultSet.getMetaData();

        //determine number of rows in ResultSet + move to last row
        resultSet.last();
        numberOfRows = resultSet.getRow(); //get row number
        fireTableStructureChanged();
    }

    //add code here to update the operations log db as a root user client: +1 to update count
    //1. get a connection as a root user to the operationslog db
    //2. using the connection from step 1, send the following update command to the operationslog


    //set new database update-query string
    public void setUpdate(String query) throws SQLException, IllegalStateException {

        //ensure database connection is available
        if(!isConnected)
            throw new IllegalStateException("Database not connected.");

        //specify query and execute it

        statement.executeUpdate(query);
        fireTableStructureChanged();
    }

    //close Statement and Connection
    public void disconnectFromDatabase(){
        if(!isConnected)
            return;
        //close Statement and Connection
    }
}