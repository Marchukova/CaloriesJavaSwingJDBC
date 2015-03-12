import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

@SuppressWarnings("serial")
public class AddNewProductDialog extends JDialog implements ActionListener {
    private JPanel myPanel = null;
    private JButton cancelButton = null, addButton;
	private String[] columnNames = {"Product",
	        "Proteins",
	        "Fats",
	        "Carbohydrate", 
	        "Calories"};
	Object[] rowData = {null, null, null, null, null};
	private final static int PRODUCT_INDEX = 0;
	private final static int CALORIES_INDEX = 4;
    private TableModel tableModel = new TableModel();
    private CaloriesTableModel mainTableModel;
    private JTable table  = new JTable();
    private final String productWasAdded = " was successfully added";
    private final String emptyString = " ";
    private	JLabel additionStatusLabel = new JLabel(emptyString);
    
    private Component addTable() {
		table.setModel(tableModel);
		table.setSurrendersFocusOnKeystroke(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
        /* Set selection to first cell */
		table.setRowSelectionInterval(0, 0);
    	table.setColumnSelectionInterval(0, 0);
		table.getModel().addTableModelListener(new TableModelListener(){
			@Override
			public void tableChanged( TableModelEvent e ) {
				if (e.getType() == TableModelEvent.UPDATE) {
	                int column = e.getColumn();
	                if (column != CALORIES_INDEX) { //not the last column
	                	/* Select next column */
	                	table.setRowSelectionInterval(0, 0);
	                	table.setColumnSelectionInterval(column + 1, column + 1);
	                } else { //the last column
	            		/* Set focus to "Add product" button */
	                	if (table.isEditing())
	            			table.getCellEditor().cancelCellEditing();
	                	addButton.requestFocusInWindow();
	                }
				}	
			}
		});
		/* Set columns's width */
    	TableColumn column = null;
    	for (int i = 0; i < tableModel.getColumnCount(); i++) {
    		column = table.getColumnModel().getColumn(i);
    		if (i == 0) {
    			column.setPreferredWidth(220); 
    		} else {
    			column.setPreferredWidth(90);
    		}
    	}
    	/* Set cell editors to check valid float values */
    	for (int i = 1; i < tableModel.getColumnCount(); i++)
    		table.getColumnModel().getColumn(i).setCellEditor(new WeightEditor());
		/* Set scroll pane fit to table size */ 
    	table.setPreferredScrollableViewportSize(table.getPreferredSize());
		JScrollPane scrollPane = new JScrollPane(table);
		return scrollPane;
    }
    /* Add dialog's buttons */
    private Component addButtons() {
		addButton = new JButton("Add product");
		addButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		JPanel buttonsPane = new JPanel(new FlowLayout());
    	buttonsPane.add(addButton);
    	buttonsPane.add(cancelButton);
    	return buttonsPane;
    }
    
    /* Constructor */
    AddNewProductDialog( Frame frame, CaloriesTableModel mainTableModel ) {
		super(frame, "Add new product", true);
		//setResizable(false);
		this.mainTableModel = mainTableModel;
		setLocationRelativeTo(frame);
		myPanel = new JPanel(new GridBagLayout());
		getContentPane().add(myPanel);
    	GridBagConstraints c = new GridBagConstraints();
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.gridx = 0;
    	c.gridy = 0;
    	c.weighty = 1;
    	c.weightx = 1;
    	c.insets = new Insets(10, 10, 0, 10);
		myPanel.add(addTable(), c);
    	c.fill = GridBagConstraints.NONE;
    	c.gridx = 0;
    	c.gridy = 1;
    	c.insets = new Insets(0, 0, 0, 0);
        additionStatusLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        myPanel.add(additionStatusLabel, c);
    	c.gridx = 0;
    	c.gridy = 2;
        myPanel.add(addButtons(), c);
		pack();
	}

    public void actionPerformed(ActionEvent e) {
    	if(addButton == e.getSource()) {
    		for (int i = 0; i < tableModel.getColumnCount(); i++) {
    			if (tableModel.getValueAt(0, i) == null) {
    		    	Toolkit.getDefaultToolkit().beep();
    		        JOptionPane.showMessageDialog(
    		            this,
    		            "Cell for \"" + tableModel.getColumnName(i) + "\" should be filled",
    		            "Error",
    		            JOptionPane.ERROR_MESSAGE);
                	table.setRowSelectionInterval(0, 0);
                	table.setColumnSelectionInterval(i, i);
                	table.requestFocusInWindow();
                	return;
    			}
    		}
    		if (mainTableModel.getDataSource().isProductAlreadyIn((String)tableModel.getValueAt(0, PRODUCT_INDEX))) {
		    	Toolkit.getDefaultToolkit().beep();
		        Object[] options = {"Replace",
                "Cancel"};
				int answer = JOptionPane.showOptionDialog(
					this,
					"This product name already exist\n"
					+ "Replace it?",
					"Product name already exist",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.ERROR_MESSAGE,
					null,
					options,
					options[1]);
				if (answer == 1) //cancel
					return;
    		}
    		mainTableModel.getDataSource().addProduct(rowData);
    		mainTableModel.changeNutrients(rowData);
    		additionStatusLabel.setText("\"" + (String)rowData[0] + "\"" + productWasAdded);
    		tableModel.clear();
		}
		else if(cancelButton == e.getSource()) {
			/* Prepare dialog for future show */
			if (table.isEditing())
    			table.getCellEditor().cancelCellEditing();
        	/* Set selection to first cell */
        	table.setRowSelectionInterval(0, 0);
        	table.setColumnSelectionInterval(0, 0);
        	/* Set focus to table */
        	table.requestFocusInWindow();
        	/* Clear status for product addition */
        	additionStatusLabel.setText(emptyString);
		    setVisible(false);
    		//tableModel.clear();
		}
	}
    private class TableModel extends AbstractTableModel {
    	@Override
    	public String getColumnName(int col) { 
        	return columnNames[col];
        }
        public int getColumnCount() { return columnNames.length; }
        public int getRowCount() { return 1; }
		public Class getColumnClass( int column ) {
    		if (column == 0) 
    			return String.class;
   			return Float.class;
        }
        public Object getValueAt(int row, int col) { return rowData[col]; }
        public boolean isCellEditable(int row, int col) { return true; }
        public void setValueAt(Object value, int row, int col) {
            rowData[col] = value;
            fireTableCellUpdated(row, col);
        	}
        public void clear() {
        	for (int i = 0; i < rowData.length; i++)
        		rowData[i] = null;
        	fireTableRowsUpdated(0, 0);
        }
    };
}
