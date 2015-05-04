package siggui.properties;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import siggui.utility.ColumnsAutoSizer;

public class PropertiesTablePanel {

    public static JPanel makePropertiesPanel(String[][] rows) {
        JPanel panel = new JPanel(new GridLayout(1, 0));
        final JTable table = new JTable(rows, new String[]{"Name", "Value"});
        table.setFillsViewportHeight(true);
        table.setTableHeader(null);
        int totalWidth = ColumnsAutoSizer.sizeColumnsToFit(table);
        table.setPreferredScrollableViewportSize(new Dimension(totalWidth, rows.length * table.getRowHeight()));

        panel.add(new JScrollPane(table));
        panel.setOpaque(true);
        return panel;
    }

    private PropertiesTablePanel() {
    }
}
