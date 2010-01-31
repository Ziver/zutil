package zutil.ui.wizard.pages;

import java.util.HashMap;

import javax.swing.JTextArea;

import zutil.ui.wizard.WizardPage;

/**
 * This class will show a summary of all the values
 * in the wizard
 * 
 * @author Ziver
 *
 */
public class SummaryPage extends WizardPage{
	private static final long serialVersionUID = 1L;

	public SummaryPage(HashMap<String, Object> values){
		this.setFinalPage( true );
		
		JTextArea summary = new JTextArea();
		summary.setEditable(false);
		summary.setEnabled(false);
		this.add( summary );
		
		StringBuffer tmp = new StringBuffer();
		for(String key : values.keySet()){
			tmp.append(key);
			tmp.append(": ");
			tmp.append(values.get( key ));
			tmp.append("\n");
		}
		summary.setText( tmp.toString() );
	}
	
	@Override
	public WizardPage getNextPage(HashMap<String, Object> values) {
		return null;
	}

	@Override
	public String getPageDescription() {
		return "Summary";
	}

}
