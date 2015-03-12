import java.util.ArrayList;
import java.sql.Date;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class CaloriesTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private String[] columnNames = {"Product",
        "Weight",
        "Proteins",
        "Fats",
        "Carbohydrate", 
        "Calories"};
	private boolean stopEdit = false;
	private Date date = null;
	private DataSource dataSource; 
	private ArrayList<TableRowData> data; /* contains current table data,
	 										 what you see on screen right now */
	

	public CaloriesTableModel( Date date, DataSource dataSource ) {
		this.date = date;
		this.dataSource = dataSource;
		data = dataSource.getDataForDate(date);
	}
	public DataSource getDataSource() {
		return dataSource;
	}
	public void saveDataForCurrentDate() {
		dataSource.saveDailyConsumption(data, date);
	}
	public void changeDate( Date newDate ) {
		if (date.equals(newDate))
			return;
		dataSource.saveDailyConsumption(data, date);
		data = dataSource.getDataForDate(newDate);
		date = newDate;
		fireTableDataChanged();
	}
	@Override
	public String getColumnName(int col) {
        return columnNames[col];
    }
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data.size() + 1;
	}

	@Override
	public Object getValueAt( int row, int column ) {
		if (row < getRowCount() - 1) { //If not a last row
			switch (column) {
			case TableRowData.PROTEINS_INDEX: 
			case TableRowData.FATS_INDEX: 
			case TableRowData.CARBOHYDRATE_INDEX:
			case TableRowData.CALORIES_INDEX:
				if (data.get(row).get(TableRowData.WEIGHT_INDEX) != null && data.get(row).get(column) != null) {
					try {
						float weight = (float)data.get(row).get(TableRowData.WEIGHT_INDEX);
						float nutrition = (float)data.get(row).get(column); 
						return  ((weight * nutrition)) / 100; 
					} catch (Exception e) {
						e.printStackTrace();	
					}
				}
				break;
			default:
				return data.get(row).get(column);				
			}
			
		}
		return null;
	}

	@Override
	public boolean isCellEditable( int row, int column ) {
		if (stopEdit) {
			stopEdit = false;
			return false;
		}
		if (column < 2)
			return true; 
		return false;
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		if (col == TableRowData.PRODUCT_INDEX && ((String)value).equals(""))
			return;
		if (col == TableRowData.WEIGHT_INDEX && value == null)
			return;
		if (row == getRowCount() - 1){
			TableRowData newRow = new TableRowData();
			newRow.set(col , value);
			newRow.setState(TableRowData.State.ADDED);
			data.add(newRow);
		    fireTableRowsInserted(row + 1, row + 1);;
		}
		else {
			if (data.get(row).get(col) == value)
				return;
			data.get(row).set(col, value);
			TableRowData.State state = col == TableRowData.PRODUCT_INDEX ? 
					TableRowData.State.PRODUCT_CHANGED : TableRowData.State.PRODUCT_CHANGED;
			data.get(row).setState(state);			
		}
	    fireTableCellUpdated(row, col);
	}
	
	@Override
	public Class getColumnClass( int column ) {
		switch (column) {
		case TableRowData.PRODUCT_INDEX:
			return String.class;
		default:
			return Float.class;
		}
    }
	
	public void deleteRow( int row, int column ) {
		stopEdit = true;
		if (row < getRowCount() - 1) {
			data.remove(row);
			fireTableRowsDeleted(row, row);
		}
	}
	
	public boolean setNewProductName( int row, String newProductName ) {
		if (row == getRowCount() - 1) {
			TableRowData newRow = dataSource.getNutritions(newProductName);
			if (newRow == null)
				return false;
			newRow.setState(TableRowData.State.ADDED);
			data.add(newRow);
			fireTableRowsInserted(row, row);
			return true;
		} else if (data.get(row).get(TableRowData.PRODUCT_INDEX).equals(newProductName))
			return true;
		else {
			TableRowData currentRow = data.get(row);
			TableRowData newRow = dataSource.getNutritions(newProductName);
			if (newRow == null) {
				for (int i = TableRowData.PROTEINS_INDEX; i <= TableRowData.CALORIES_INDEX; i++) {
					currentRow.set(i, null);
				    fireTableCellUpdated(row, i);
				}
				return false;
			}
			currentRow.set(TableRowData.PRODUCT_INDEX,  newRow.get(TableRowData.PRODUCT_INDEX));
			for (int i = TableRowData.PROTEINS_INDEX; i <= TableRowData.CALORIES_INDEX; i++) {
				currentRow.set(i, newRow.get(i));
			    fireTableCellUpdated(row, i);
			}
		}
		return true;
	}
	
	public void changeNutrients( Object[] newProduct ) {
		for (int i = 0; i < data.size(); i++)
			if (data.get(i).get(TableRowData.PRODUCT_INDEX).equals(newProduct[TableRowData.PRODUCT_INDEX])) {
				for (int j = TableRowData.PROTEINS_INDEX; j <= TableRowData.CALORIES_INDEX; j++)
					data.get(i).set(j, newProduct[j - 1]);
				fireTableRowsUpdated(i, i);
			}
	}
}
