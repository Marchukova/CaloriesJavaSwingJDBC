
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import com.toedter.calendar.JDateChooser;

/****
 * THIS CLASS CONTAINS MAIN PROGRAM FUNCTION 
 ****/

/* Class for creation of window */
public class CaloriesContentPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private  final JLabel[] nutrientsNamesLabels = {new JLabel("Proteins: "), new JLabel("Fats: "),
			new JLabel("Carbohydrate: "), new JLabel("Calories: ")};
	/* Labels for output amount of proteins, fats and etc */
	private  JLabel[] outNutrientsNumbersLabels = {new JLabel(), new JLabel(), new JLabel(), new JLabel()};
	private static Table table;
    private AddNewProductDialog dialog = null;
 
	/* Calendar for date changing */
    public class Calendar extends JDateChooser {
		private static final long serialVersionUID = 1L;
		Calendar( Date date ) {
		    getDateEditor().addPropertyChangeListener(
		    	    new PropertyChangeListener() {
		    	        @SuppressWarnings("deprecation")
						@Override
		    	        public void propertyChange(PropertyChangeEvent e) {
		    	        	if ("date".equals(e.getPropertyName())) {
			    	        	Date newDate = (Date)e.getNewValue();
			    	        	Date oldDate = (Date)e.getOldValue();
		    	        		/* After date changing inform about it table */
			    	        	if (oldDate == null || oldDate.getDate() != newDate.getDate() ||
		    	        				oldDate.getMonth() != newDate.getMonth() ||
	    	        						oldDate.getYear() != newDate.getYear())
		    	        			table.fireDateChange(new java.sql.Date(getDate().getTime()));
		    	        	}
		    	        }
		    	    });
		    getDateEditor().setDate(date);
	    }
	}
	
	/* */
    JPanel addCalendar( Date date ) {
    	JPanel calendarPane = new JPanel(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
    	c.fill = GridBagConstraints.NONE;
    	c.gridx = 1;
    	c.gridy = 0;
    	c.weighty = 0.5;
    	c.weightx = 0.5;
    	c.gridwidth = 1;
    	c.insets = new Insets(15, 0, 15, 0);
     	Calendar calendar = new Calendar(date);
     	Dimension dimension = calendar.getPreferredSize();
     	dimension.width = 150;
     	calendar.setPreferredSize(dimension);
    	calendarPane.add(calendar, c);
    	return calendarPane;
	}
	
	JPanel addLabels() {
    	JPanel labelsPane = new JPanel(new GridLayout(0,2));
    	labelsPane.setAlignmentX(Container.RIGHT_ALIGNMENT);
    	for (int i = 0; i < nutrientsNamesLabels.length; i++) {
        	nutrientsNamesLabels[i].setHorizontalAlignment(JLabel.RIGHT);
        	//nutrientsNamesLabels[i].setAlignmentX((float)0.5);
        	labelsPane.add(nutrientsNamesLabels[i]);
        	outNutrientsNumbersLabels[i].setHorizontalAlignment(JLabel.LEFT);
        	labelsPane.add(outNutrientsNumbersLabels[i]);
    	}
    	return labelsPane;
	}
	
	//@SuppressWarnings("deprecation")
	public CaloriesContentPane() {
    	super(new BorderLayout());
    	Date date = new Date();
    	date = new Date(date.getTime() - date.getTime() % 1000000);
    	table = new Table(outNutrientsNumbersLabels, new java.sql.Date(date.getTime() - date.getTime() % 1000000/*- date.getHours() 
    													- date.getMinutes() - date.getSeconds()*/));
    	JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		splitPane.setDividerSize(4);
		splitPane.setDividerLocation(400);
    	splitPane.setTopComponent((new JScrollPane(table)));
    	splitPane.setResizeWeight(1);
		add(addCalendar(date), BorderLayout.NORTH);
		JPanel controls = new JPanel();
        controls.setLayout(new GridLayout(0,2));
        controls.add(addLabels());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        final JButton button = new JButton("Add new product");
        button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				if (dialog == null)
					dialog = new AddNewProductDialog(
			        		(JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, button), 
			        		((CaloriesTableModel)table.getModel()));
				SwingUtilities.getWindowAncestor(button);
				dialog.setVisible(true);
				
			}
        	
        });
        buttonPanel.add(new JPanel());
        buttonPanel.add(button);
        buttonPanel.add(new JPanel());
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        controls.add(buttonPanel);
    	splitPane.setBottomComponent(controls);
    	add(splitPane, BorderLayout.CENTER);
    }
	
    /***
     * MAIN PROGRAM FUNCTION
     ***/
    public static void main(String[] args) {
    	JFrame frame = new JFrame("Calories");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setJMenuBar(createMainMenu());
    	
    	frame.setContentPane(new CaloriesContentPane());
    	frame.addWindowListener(new WindowAdapter() {
    	    @Override
    	    public void windowClosing(WindowEvent e) {
    	    	/* Table should save changes before window is closed */
    	    	table.firePropertyChange("window closed", 0, -1);
    	    }
    	});
    	frame.pack();
    	frame.setVisible(true);
    }
    
	/* Menu functions */
    public static JMenuBar createMainMenu() {
        JMenuBar mainMenu = new JMenuBar();
        mainMenu.add(createFileMenu());
        
        return mainMenu;
    }
    public static JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);

        fileMenu.add(new JMenuItem("Open"));
        fileMenu.add(new JMenuItem("Save"));
        fileMenu.add(new JSeparator());
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
        	public void actionPerformed( ActionEvent e ) {
        	  System.exit(0);	
        	}
        });
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }
}
