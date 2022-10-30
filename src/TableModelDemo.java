import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class TableModelDemo extends JPanel {
    public TableModelDemo() {
        initializePanel();
    }

    public static void showFrame() {
        JPanel panel = new TableModelDemo();
        panel.setOpaque(true);

        JFrame frame = new JFrame("Premiere League - Season 2021-2022");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TableModelDemo::showFrame);
    }

    private void initializePanel() {
        // Creates an instance of PremiereLeagueTableModel
        PremiereLeagueTableModel tableModel = new PremiereLeagueTableModel();

        // Creates an instance of JTable with a TableModel
        // as the constructor parameters.
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(table);

        this.setPreferredSize(new Dimension(500, 500));
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    static class PremiereLeagueTableModel extends AbstractTableModel {
        // TableModel's column names
        private final String[] columnNames = {
                "CLUB", "MP", "W", "D", "L", "GF", "GA", "GD", "PTS"
        };

        // TableModel's data
        private final Object[][] data = {
                {"Chelsea", 8, 6, 1, 1, 16, 3, 13, 19},
                {"Liverpool", 8, 5, 3, 0, 22, 6, 16, 18},
                {"Manchester City", 8, 5, 2, 1, 16, 3, 13, 17},
                {"Brighton", 8, 4, 3, 1, 8, 5, 3, 15},
                {"Tottenham", 8, 5, 0, 3, 9, 12, -3, 15}
        };

        /**
         * Returns the number of rows in the table model.
         */
        public int getRowCount() {
            return data.length;
        }

        /**
         * Returns the number of columns in the table model.
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * Returns the column name for the column index.
         */
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        /**
         * Returns data type of the column specified by its index.
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }

        /**
         * Returns the value of a table model at the specified
         * row index and column index.
         */
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }
    }
}