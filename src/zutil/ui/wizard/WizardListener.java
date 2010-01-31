package zutil.ui.wizard;

import java.util.HashMap;

public interface WizardListener {

	/**
	 * Will be called when the cancel button is pressed
	 * 
	 * @param page is the WizardPage where the cancel button was pressed
	 * @param values is the values until now
	 */
	public void onCancel(WizardPage page, HashMap<String, Object> values);
	
	/**
	 * Will be called when the wizard is finished
	 * 
	 * @param values is the values until now
	 */
	public void onFinished(HashMap<String, Object> values);
}
