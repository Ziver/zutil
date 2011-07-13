/*******************************************************************************
 * Copyright (c) 2011 Ziver Koc
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
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
