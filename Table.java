import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.sql.Date;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;


public class Table extends JTable {
	private static final long serialVersionUID = 1L;
	private CaloriesTableModel tableModel;
	private final JLabel[] labels;
	private final int CAPACITY = 100;
    
	static class FloatRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private DecimalFormat df;

	    public FloatRenderer() {
	    	super(); 
			df = new DecimalFormat("0.00");
			setHorizontalAlignment(JLabel.RIGHT);
	    }

	    public void setValue(Object value) {
	        if (value != null)
	        	setText(df.format(value));
	    }
	}
	
	public void setTotalDataLabels() {
		DecimalFormat df = new DecimalFormat("0.00");
		for (int i = 0; i < labels.length; i++) {
			float total = 0;
			for (int j = 0; j < tableModel.getRowCount() - 1; j++) {
				Object value = tableModel.getValueAt(j, i + TableRowData.PROTEINS_INDEX);
				if (value != null)
					total += (float)value;
			}
			labels[i].setText("   " + df.format(total));
		}
	}
	
	public Table( JLabel[] labels, Date date )  {
		super();
		this.labels = labels;
		try {
			tableModel = new CaloriesTableModel(date, new DataBase(CAPACITY));
		} catch (ClassNotFoundException | SQLException e) {
			//e.printStackTrace();
	    	Toolkit.getDefaultToolkit().beep();
	        JOptionPane.showMessageDialog(
	            this,
	            "Can't connect to database\n"
	            + "Program will work, but after closing\n"
	            + "Your data wouldn't be saved"
	            ,
	            "Error",
	            JOptionPane.ERROR_MESSAGE);
	        tableModel = new CaloriesTableModel(date, new temporaryDataSource(date, CAPACITY));
		}
		setModel(tableModel);
		setSurrendersFocusOnKeystroke(true);
		/* Set columns's width */
    	TableColumn column = null;
    	for (int i = 0; i < tableModel.getColumnCount(); i++) {
    		column = getColumnModel().getColumn(i);
    		if (i == 0) {
    			column.setPreferredWidth(120); 
    		} else {
    			column.setPreferredWidth(60);
    		}
    	}
    	setTotalDataLabels();
	    getColumnModel().getColumn(TableRowData.WEIGHT_INDEX).setCellEditor(new WeightEditor());
	    getColumnModel().getColumn(TableRowData.PRODUCT_INDEX).setCellEditor(new ProductNameEditor(tableModel));
        /* Unable to select more then one row at one time */
    	setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		getModel().addTableModelListener(new TableModelListener(){
		@Override
		public void tableChanged( TableModelEvent e ) {
			setTotalDataLabels();
			if (e.getType() == TableModelEvent.UPDATE) {
                int column = e.getColumn();
                int row = e.getFirstRow();
                if (column == TableRowData.PRODUCT_INDEX) {
                    setRowSelectionInterval(row, row);
                    setColumnSelectionInterval(TableRowData.WEIGHT_INDEX, 
                    		TableRowData.WEIGHT_INDEX);
                }
                else if (column == TableRowData.WEIGHT_INDEX) {
                    setRowSelectionInterval(row + 1, row + 1);
                    setColumnSelectionInterval(TableRowData.PRODUCT_INDEX,
                    		TableRowData.PRODUCT_INDEX);
                }
			}	
		}
      });
      addKeyListener(new KeyAdapter() {
    	  
    	    public void keyPressed(KeyEvent e) {
    	    	if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {
                    int row = getSelectedRow();
                    int column = getSelectedColumn();
                    tableModel.deleteRow(row, column);
                    setColumnSelectionInterval(column, column);
                    setRowSelectionInterval(row, row);
    	    	}
    	    }
      });
      addPropertyChangeListener(new PropertyChangeListener() {

		@Override
		public void propertyChange( PropertyChangeEvent e ) {
			String propertyName = e.getPropertyName();
	        if ("date changed".equals(propertyName)) {
        		tableModel.changeDate((Date)e.getNewValue());
	        } else if ("window closed".equals(propertyName)) {
	        	tableModel.saveDataForCurrentDate();
	        }
		}
    	  
      });
    }
	void fireDateChange( Date newDate ) {
		firePropertyChange("date changed", null, newDate);
	}
 }
