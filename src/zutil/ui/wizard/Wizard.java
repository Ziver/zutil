package zutil.ui.wizard;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import zutil.FileFinder;
import zutil.MultiPrintStream;
import zutil.struct.HistoryList;
import zutil.ui.JImagePanel;
import zutil.ui.wizard.listener.BlockingWizardListener;

/**
 * This class manages the whole wizard
 * 
 * @author Ziver
 */
public class Wizard implements ActionListener{
	public static final boolean DEBUG = true;
	private static final long serialVersionUID = 1L;

	/** Some defoult backgrounds for the sidebar */
	public static final String BACKGROUND_1 = "zutil/data/wizard1.jpg";
	public static final String BACKGROUND_2 = "zutil/data/wizard2.jpg";
	public static final String BACKGROUND_3 = "zutil/data/wizard3.png";

	/** An list with all the previous pages and the current at the beginning */
	private HistoryList<WizardPage> pages;
	/** HashMap containing all the selected values */
	private HashMap<String, Object> values;
	/** The general component listener */
	private WizardActionHandler handler;
	/** This is the user listener that handles all the values after the wizard */
	private WizardListener listener;
	
	/** This is the old validation fail, this is needed for reseting purposes */
	private ValidationFail oldFail;
	

	private Wizard(WizardListener listener){
		this(listener, null);
	}

	/**
	 * Creates a new Wizard
	 */
	public Wizard(WizardListener listener, WizardPage start){
		this(listener, start, BACKGROUND_1);
	}

	/**
	 * Creates a new Wizard
	 * 
	 * @param start is the first page in the wizard
	 * @param bg is the background image to use
	 */
	public Wizard(WizardListener listener, final WizardPage start, final String bg){
		try {
			this.listener = listener;
			pages = new HistoryList<WizardPage>();
			values = new HashMap<String, Object>();
			handler = new WizardActionHandler( values );

			// GUI
			frame = new JFrame();
			initComponents();
			sidebar.scale( false );
			
			// add action listener to the buttons
			back.addActionListener( this );
			next.addActionListener( this );
			cancel.addActionListener( this );
			finish.addActionListener( this );
			
			// Set the image in the sidebar
			sidebar.setImage(ImageIO.read( FileFinder.getInputStream( bg ) ));
			
			// add the first page
			pages.add( start );
			displayWizardPage( start );

		} catch (Exception e) {
			e.printStackTrace(MultiPrintStream.out);
		}
	}

	/**
	 * Sets the title of the Wizard
	 */
	public void setTitle(String s){
		frame.setTitle(s);
	}
	
	/**
	 * Sets the size of the Wizard frame
	 * 
	 * @param w is the width
	 * @param h is the height
	 */
	public void setSize(int w, int h){
		frame.setSize(w, h);
	}

	/**
	 * Displays the wizard
	 */
	public void start(){
		frame.setVisible(true);
	}
	
	/**
	 * @return the JFrame used for the wizard
	 */
	public JFrame getFrame(){
		return frame;
	}
	
	/**
	 * Set the current WizardPage
	 * 
	 * @param page is the page to be displayed
	 */
	protected void displayWizardPage(WizardPage page){
		pageContainer.getViewport().setView(page);
		pageTitle.setText( page.getPageDescription() );
	}

	public void actionPerformed(ActionEvent e) {
		// Back Button
		if(e.getSource() == back){
			WizardPage page = pages.getPrevious();
			displayWizardPage( page );
			if(pages.get(0) == page){
				back.setEnabled( false );
			}
		}
		// Next Button and Finish Button
		else if(e.getSource() == next || e.getSource() == finish){
			WizardPage page = pages.getCurrent();
			page.registerValues( handler );
			if(DEBUG) MultiPrintStream.out.println(values);
			
			ValidationFail fail = page.validate( values );
			if(fail != null){
				// reset old fail
				if(oldFail != null) oldFail.getSource().setBorder( BorderFactory.createEmptyBorder() );
				if(fail.getSource() != null) 
					fail.getSource().setBorder( BorderFactory.createLineBorder(Color.RED) );
				//pageStatus.setText( fail.getMessage() );
			}
			else if(e.getSource() == finish){
				frame.dispose();
				listener.onFinished( values );
			}
			else if(e.getSource() == next){
				WizardPage nextPage = page.getNextPage( values );
				if(nextPage == null){
					frame.dispose();
					listener.onCancel(page, values );
					return;
				}
				pages.add( nextPage );
				displayWizardPage( nextPage );
				back.setEnabled( true );
				if( nextPage.isFinalPage() ){
					next.setEnabled( false );
					finish.setEnabled( true );
				}
			}
		}
		// Cancel Button
		else if(e.getSource() == cancel){
			frame.dispose();
			listener.onCancel(pages.getCurrent(), values );
		}		
	}	
	

    private void initComponents() {
        cancel = new JButton();
        finish = new JButton();
        next = new JButton();
        back = new JButton();

        JSeparator separator = new JSeparator(); // NOI18N
        sidebar = new JImagePanel();
        JLabel steps = new JLabel();
        JSeparator separator2 = new JSeparator();
        pageTitle = new JLabel();
        JSeparator separator3 = new JSeparator();
        error = new JLabel();
        pageContainer = new JScrollPane();

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ResourceBundle bundle = ResourceBundle.getBundle("zutil/ui/wizard/Bundle");
        cancel.setText(bundle.getString("WizardManager.cancel.text")); // NOI18N

        finish.setText(bundle.getString("WizardManager.finish.text")); // NOI18N
        finish.setEnabled(false);

        next.setText(bundle.getString("WizardManager.next.text")); // NOI18N

        back.setText(bundle.getString("WizardManager.back.text")); // NOI18N
        back.setEnabled(false);

        sidebar.setBorder(BorderFactory.createEtchedBorder());

        steps.setText(bundle.getString("WizardManager.steps.text")); // NOI18N

        GroupLayout sidebarLayout = new GroupLayout(sidebar);
        sidebar.setLayout(sidebarLayout);
        sidebarLayout.setHorizontalGroup(
            sidebarLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(sidebarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sidebarLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(separator2, GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                    .addComponent(steps, GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                .addContainerGap())
        );
        sidebarLayout.setVerticalGroup(
            sidebarLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(sidebarLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(steps)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(separator2, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(347, Short.MAX_VALUE))
        );

        pageTitle.setFont(new Font("Tahoma", 1, 18));
        pageTitle.setText(bundle.getString("WizardManager.pageTitle.text")); // NOI18N

        error.setFont(new Font("Times New Roman", 1, 12));
        error.setForeground(new Color(255, 0, 0));
        error.setText(bundle.getString("WizardManager.error.text")); // NOI18N

        pageContainer.setBorder(null);

        GroupLayout layout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(error, GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(back)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(next)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(finish)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(cancel)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(sidebar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                            .addComponent(separator, GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(pageTitle, GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)))
                        .addGap(2, 2, 2))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(separator3, GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                            .addComponent(pageContainer, GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(sidebar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pageTitle, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(separator3, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(pageContainer, GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(cancel)
                    .addComponent(finish)
                    .addComponent(next)
                    .addComponent(back)
                    .addComponent(error))
                .addContainerGap())
        );

        frame.pack();
    }
    
    
	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		final BlockingWizardListener listener = new BlockingWizardListener();
		EventQueue.invokeLater(new Runnable() {
			public void run() {				
				Wizard wizard = new Wizard(listener);
				wizard.start();				
			}
		});
		MultiPrintStream.out.dump( listener.getValues() );

	}

	// Variables declaration - do not modify
	private JLabel error;
	private JButton back;
	private JButton cancel;
	private JButton finish;
	private JButton next;
	private JScrollPane pageContainer;
	private JLabel pageTitle;
	private JImagePanel sidebar;
	private JFrame frame;
	// End of variables declaration

}
