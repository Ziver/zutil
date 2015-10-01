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

package zutil.log.net;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
 
public class NetLogGuiClient extends Application{
	public static final String VERSION = "0.1";
		
	// UI elements
    @FXML 
	private TabPane tabPane;

	
	public static void main(String[] args) {
        Application.launch(NetLogGuiClient.class, args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("NetLogClient.fxml"));
        
        stage.setTitle("NetLoggerClient ("+VERSION+")");
        stage.setScene(new Scene(root));
        stage.show();
    }
	
	// Menu Actions
    @FXML 
	protected void handleConnectAction(ActionEvent event) {
		try{
			tabPane.getTabs().add(new NetLoggerClientTab("koc.se", 8080));
		}catch(Exception e){
			e.printStackTrace();
		}
    }
	
	@FXML 
	protected void handleExitAction(ActionEvent event) {
		System.exit(0);
    }
	
	@FXML 
	protected void handleAboutAction(ActionEvent event) {
		
    }
	
	private class NetLoggerClientTab extends Tab{
		public NetLoggerClientTab(String host, int port) throws IOException{
			this.setText( host+":"+port );

            FXMLLoader loader = new FXMLLoader();
			Parent tabRoot = loader.load(getClass().getResource("NetLogClientInstance.fxml"));
			this.setContent(tabRoot);
			AnchorPane.setRightAnchor(tabRoot, 0.0);
			//this.setOnClosed(new EventHandler<Event>() {
			//	public void handle(Event e) {
			//		handleDisconnectAction(e);
			//	}
			//});
		}
	}
}