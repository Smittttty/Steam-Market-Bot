package struts;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class SteamItemTable extends AbstractTableModel {
    private String[] columnNames = {"Item Name", "Search Terms", "Max Buyout", "Match Name"};
    ArrayList<Object[]> data = new ArrayList<Object[]>();
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
    	Object[] b = data.get(row);
        return b[col];
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public void addItem(Object[] values){
    	data.add(values);
    	fireTableDataChanged();
    }
    
    public void removeItem(int row){
    	data.remove(row);
    	fireTableDataChanged();
    }
}