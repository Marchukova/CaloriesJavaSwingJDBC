import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.ParseException;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatterFactory;


@SuppressWarnings("serial")
public class ProductNameEditor extends DefaultCellEditor {
	private JFormattedTextField ftf;
	private int row = -1;
	private CaloriesTableModel tableModel;
	
	public ProductNameEditor( CaloriesTableModel model ) {
		super(new JFormattedTextField());
		this.tableModel = model;
		ftf = (JFormattedTextField)getComponent();
		ftf.setValue(new String(""));
		ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);
        ftf.getInputMap().put(KeyStroke.getKeyStroke(
                KeyEvent.VK_ENTER, 0),
                "check");
		ftf.getActionMap().put("check", new AbstractAction() {		
			public void actionPerformed(ActionEvent e) {
		        JFormattedTextField ftf = (JFormattedTextField)getComponent();
				ftf.postActionEvent();
			}
		});
	}

    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected,
            int row, int column) {
    	this.row = row;
        JFormattedTextField ftf = (JFormattedTextField)getComponent();
        ftf.setValue(value);
    	return ftf;
    	
    }
    
    public boolean stopCellEditing() {
        JFormattedTextField ftf = (JFormattedTextField)getComponent();
    	if ("".equals(ftf.getText()))
            try {
                ftf.commitEdit();
            } catch (java.text.ParseException exc) { }
		else if (!tableModel.setNewProductName(row, ftf.getText())) {
			userSaysRevert();			
            try {
                ftf.commitEdit();
            } catch (java.text.ParseException exc) { }
		}
        return super.stopCellEditing();
    }

    private void userSaysRevert() {
    	Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(
            SwingUtilities.getWindowAncestor(ftf),
            "Can't find such product name.\n",
            "Warning",
            JOptionPane.WARNING_MESSAGE);
        SwingUtilities.getAncestorOfClass(JTable.class, ftf).requestFocusInWindow();
    }
}
