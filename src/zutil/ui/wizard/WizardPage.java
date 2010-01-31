package zutil.ui.wizard;

import java.awt.Component;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * This abstract class is one step in the wizard
 * 
 * @author Ziver
 */
public abstract class WizardPage extends JPanel{
	private static final long serialVersionUID = 1L;
	
	/** contains the components whom values will be saved */
	private LinkedList<Component> components;
	/** if this is the last page in the wizard */
	private boolean lastPage = false;
	
	public WizardPage(){
		components = new LinkedList<Component>();
	}

	/**
	 * Register a component whom the value will be saved with 
	 * the key that is what getName returns from the component
	 * and passed on to the other pages.
	 * 
	 * @param c is the component
	 */
	public void registerComponent(Component c){
		components.add( c );
	}
	
	/**
	 * Sets if this is the last page in the wizard, 
	 * Should be called as early as possible.
	 */
	public void setFinalPage(boolean b){
		lastPage = b;
	}
	
	/**
	 * @return is this is the last page in the wizard
	 */
	public boolean isFinalPage(){
		return lastPage;
	}
	
	/**
	 * @return the next page in the wizard, 
	 * 			is called when the next button is pressed, 
	 * 			return null to end the wizard
	 */
	public abstract WizardPage getNextPage(HashMap<String, Object> values);
	
	/**
	 * @return a very short description of this page
	 */
	public abstract String getPageDescription();
	
	
	/**
	 * This method is called when the next button is pressed
	 * and the input values are going to be validated.
	 * 
	 * @param values is the values until now
	 * @return a ValidateFail object or null if the validation passed
	 */
	public ValidationFail validate(HashMap<String, Object> values){
		return null;
	}
	
	/**
	 * Will be called after the validation passes and will
	 * save all the states of the registered components
	 * 
	 * @param listener is the object that handles the save process
	 */
	public void registerValues(WizardActionHandler listener){
		for(Component c : components){
			listener.registerValue( c );
		}
	}
}

/**
 * This class is for failed validations
 * 
 * @author Ziver
 */
class ValidationFail{
	/** The component that failed the validation */
	private JComponent source;
	/** An message to the user about the fault */
	private String msg;
	
	/**
	 * Creates an ValidationFail object
	 * 
	 * @param c is the component that failed the validation
	 * @param msg is a message to the user about the fault
	 */
	public ValidationFail(String msg){
		this(null, msg);
	}
	
	/**
	 * Creates an ValidationFail object
	 * 
	 * @param c is the component that failed the validation
	 * @param msg is a message to the user about the fault
	 */
	public ValidationFail(JComponent c, String msg){
		this.source = c;
		this.msg = msg;
	}
	
	public JComponent getSource(){
		return source;
	}
	
	public String getMessage(){
		return msg;
	}
}
