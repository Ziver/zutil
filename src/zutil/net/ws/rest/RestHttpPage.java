/*
 * Copyright (c) 2015 ezivkoc
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

package zutil.net.ws.rest;

import zutil.converters.Converter;
import zutil.net.http.HttpHeaderParser;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;
import zutil.net.http.HttpURL;
import zutil.net.ws.WSInterface;
import zutil.net.ws.WSMethodDef;
import zutil.net.ws.WSParameterDef;
import zutil.net.ws.WebServiceDef;

import java.util.Map;

/**
 * User: Ziver
 */
public class RestHttpPage implements HttpPage {

    /** The object that the functions will be invoked from **/
    private WebServiceDef wsDef;
    /** This instance of the web service class is used if session is disabled **/
    private WSInterface ws;


    public RestHttpPage( WSInterface wsObject ){
        this.ws = wsObject;
        this.wsDef = new WebServiceDef(ws.getClass());
    }


    @Override
    public void respond(HttpPrintStream out,
                        HttpHeaderParser client_info,
                        Map<String, Object> session,
                        Map<String, String> cookie,
                        Map<String, String> request) {
        execute(request);
    }


    private void execute(Map<String, String> input){

    }

    private Object[] prepareInputParams(WSMethodDef method, Map<String, String> input){
        Object[] inputParams = new Object[method.getInputCount()];

        // Get the parameter values
        for(int i=0; i<method.getInputCount() ;i++){
            WSParameterDef param = method.getInput( i );
            if( input.containsKey(param.getName()) ){
                inputParams[i] = Converter.fromString(
                        input.get(param.getName()),
                        param.getParamClass());
            }
        }
        return inputParams;
    }

    private void generateResponse(){

    }
}
