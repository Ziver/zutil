/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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
 */

package zutil.ui.wizard.pages;

import zutil.ui.wizard.WizardPage;

import javax.swing.*;
import java.util.HashMap;

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
