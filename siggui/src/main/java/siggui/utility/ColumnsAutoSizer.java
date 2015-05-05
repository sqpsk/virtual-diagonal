package siggui.utility;

import java.awt.Component;
import java.awt.FontMetrics;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class ColumnsAutoSizer {

	public static int sizeColumnsToFit(JTable table) {
		return sizeColumnsToFit(table, 5);
	}

	public static int sizeColumnsToFit(JTable table, int columnMargin) {
		FontMetrics fm = table.getFontMetrics(table.getFont());
		int[] widths = new int[table.getColumnCount()];

		for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
			int headerWidth = fm.stringWidth(table.getColumnName(columnIndex));
			int maxWidth = calculateMaximumColumnWidth(table, columnIndex, headerWidth);
			widths[columnIndex] = maxWidth + columnMargin;
		}

		int sum = 0;
		for (int i = 0; i < widths.length; i++) {
			int width = widths[i];
			TableColumn tc = table.getColumnModel().getColumn(i);
			tc.setMinWidth(width);
			//tc.setMaxWidth(maxWidth);
			tc.setWidth(width);
			sum += width;
		}
		return sum;
	}

	private static int calculateMaximumColumnWidth(JTable table, int columnIndex, int maxWidth) {
		TableColumn column = table.getColumnModel().getColumn(columnIndex);
		TableCellRenderer cellRenderer = column.getCellRenderer();

		if (cellRenderer == null) {
			cellRenderer = new DefaultTableCellRenderer();
		}

		for (int row = 0; row < table.getModel().getRowCount(); row++) {
			Component rc = cellRenderer.getTableCellRendererComponent(
					table,
					table.getModel().getValueAt(row, columnIndex),
					false,
					false,
					row,
					columnIndex);

			int width = rc.getPreferredSize().width;
			maxWidth = Math.max(maxWidth, width);
		}

		return maxWidth;
	}

	private ColumnsAutoSizer() {
	}
}
