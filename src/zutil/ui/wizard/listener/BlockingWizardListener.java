/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

package zutil.ui.wizard.listener;

import zutil.ui.wizard.WizardListener;
import zutil.ui.wizard.WizardPage;

import java.util.HashMap;

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
