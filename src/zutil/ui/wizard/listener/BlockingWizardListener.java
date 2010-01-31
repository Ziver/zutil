package zutil.ui.wizard.listener;

import java.util.HashMap;

import zutil.ui.wizard.WizardListener;
import zutil.ui.wizard.WizardPage;

/**
 * This listener class will block until the wizard is finished
 * and than return the values of the wizard
 * 
 * @author Ziver
 */
public class BlockingWizardListener implements WizardListener{
	private HashMap<String, Object> values;

	/**
	 * Will block until the wizard is finished
	 * 
	 * @return the values with a extra parameter "canceled" set 
	 * 			as a boolean if the wizard was canceled and "canceledPage"
	 * 			witch is the page where the cancel button was pressed
	 */
	public HashMap<String, Object> getValues(){
		while(values == null){
			try{
				Thread.sleep(100);
			}catch(Exception e){}
		}
		return values;
	}


	public void onCancel(WizardPage page, HashMap<String, Object> values) {
		values.put("canceled", Boolean.TRUE);
		values.put("canceledPage", page);
		this.values = values;
	}

	public void onFinished(HashMap<String, Object> values) {
		values.put("canceled", Boolean.FALSE);
		this.values = values;
	}

}
