package zutil.ui.wizard;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;

import javax.swing.JList;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

public class WizardActionHandler implements ActionListener, FocusListener, ListSelectionListener{
	private HashMap<String, Object> values;

	public WizardActionHandler(HashMap<String, Object> values){
		this.values = values;
	}

	public void actionPerformed(ActionEvent e) {
		event(e);
	}	
	public void focusGained(FocusEvent e) {
		event(e);
	}
	public void focusLost(FocusEvent e) {
		event(e);
	}	
	public void event(AWTEvent e){
		if(e.getSource() instanceof Component) 
			registerValue( (Component)e.getSource() );
	}
	public void valueChanged(ListSelectionEvent e) {
		if(e.getSource() instanceof Component) 
			registerValue( (Component)e.getSource() );
	}

	public void registerListener(Component c){
		/**
		 * JToggleButton
		 * JCheckBox
		 * JRadioButton
		 */
		if(c instanceof JToggleButton){
			JToggleButton o = (JToggleButton) c;
			o.addActionListener( this );
		}		
		/**
		 * JEditorPane
		 * JTextArea
		 * JTextField
		 */
		else if(c instanceof JTextComponent){
			JTextComponent o = (JTextComponent) c;
			o.addFocusListener( this );
		}
		/**
		 * JList
		 */
		else if(c instanceof JList){
			JList o = (JList) c;
			o.addListSelectionListener( this );
		}
	}

	/**
	 * Registers the state of the event source
	 * @param e is the event
	 */
	public void registerValue(Component c) {
		/**
		 * JToggleButton
		 * JCheckBox
		 * JRadioButton
		 */
		if(c instanceof JToggleButton){
			JToggleButton o = (JToggleButton) c;
			values.put( o.getName() , o.isSelected() );
		}		
		/**
		 * JEditorPane
		 * JTextArea
		 * JTextField
		 */
		else if(c instanceof JTextComponent){
			JTextComponent o = (JTextComponent) c;
			values.put( o.getName() , o.getText() );
		}
		/**
		 * JList
		 */
		else if(c instanceof JList){
			JList o = (JList) c;
			values.put( o.getName() , o.getSelectedValue() );
		}
	}
}
