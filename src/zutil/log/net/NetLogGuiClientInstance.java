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
package zutil.log.net;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import zutil.log.LogUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
 
public class NetLogGuiClientInstance implements Initializable, NetLogListener {
	private static final Logger logger = LogUtil.getLogger();
	private static enum Status{RUNNING, PAUSED, DISCONNECTED}

	// Logic variables
	private NetLogClient net;
	private Status status;

	private final ObservableList<NetLogExceptionMessage> exceptionData =
	        FXCollections.observableArrayList(
	            new NetLogExceptionMessage("java.lang.NullPointerException", "", " at com.example.myproject.Book.getTitle(Book.java:16) \n at com.example.myproject.Author.getBookTitles(Author.java:25) \n at com.example.myproject.Bootstrap.main(Bootstrap.java:14)"),
	            new NetLogExceptionMessage("java.lang.NullPointerException", "", " at com.example.myproject.Book.getTitle(Book.java:16) \n at com.example.myproject.Author.getBookTitles(Author.java:25) \n at com.example.myproject.Bootstrap.main(Bootstrap.java:14)"),
	            new NetLogExceptionMessage("java.io.FileNotFoundException", "fred.txt", " at java.io.FileInputStream.<init>(FileInputStream.java) \n at java.io.FileInputStream.<init>(FileInputStream.java) \n at ExTest.readMyFile(ExTest.java:19) \n at ExTest.main(ExTest.java:7)")
	        );
	
	// UI elements
	@FXML private ToggleButton pauseButton;
	@FXML private Label logCountLabel;
	@FXML private ProgressBar progressBar;
	
    @FXML private TableView<NetLogMessage> logTable;
    @FXML private TableColumn<NetLogMessage, Long> logTimestampColumn;
    @FXML private TableColumn<NetLogMessage, String> logLevelColumn;
    @FXML private TableColumn<NetLogMessage, String> logColumn;
    
	@FXML private TableView<NetLogExceptionMessage> exceptionTable;
    @FXML private TableColumn<NetLogExceptionMessage, Long> exCountColumn;
    @FXML private TableColumn<NetLogExceptionMessage, String> exNameColumn;
    @FXML private TableColumn<NetLogExceptionMessage, String> exMessageColumn;
    @FXML private TableColumn<NetLogExceptionMessage, String> exStackTraceColumn;
    

	public void initialize(URL arg0, ResourceBundle arg1) {
		// Connect to Server
		try{
			net = new NetLogClient("localhost", 5050);
			net.addListener( this );
			status = Status.RUNNING;		
		}catch(Exception e){
			logger.log(Level.SEVERE, null, e);
			status = Status.DISCONNECTED;
		}
		updateStatus();
		
		// Setup Gui
		logTimestampColumn.setCellValueFactory(new PropertyValueFactory<NetLogMessage, Long>("timestamp"));
		logLevelColumn.setCellValueFactory(new PropertyValueFactory<NetLogMessage, String>("level"));
		logColumn.setCellValueFactory(new PropertyValueFactory<NetLogMessage, String>("log"));
		
		exCountColumn.setCellValueFactory(new PropertyValueFactory<NetLogExceptionMessage, Long>("count"));
		exNameColumn.setCellValueFactory(new PropertyValueFactory<NetLogExceptionMessage, String>("name"));
		exMessageColumn.setCellValueFactory(new PropertyValueFactory<NetLogExceptionMessage, String>("message"));
		exStackTraceColumn.setCellValueFactory(new PropertyValueFactory<NetLogExceptionMessage, String>("stackTrace"));
		
		//logTable.setItems(logData);
		exceptionTable.setItems(exceptionData);
	}
	
	/************* NETWORK *****************/
	public void handleLogMessage(NetLogMessage log) {
		logTable.getItems().add( log );		
	}

	public void handleExceptionMessage(NetLogExceptionMessage exception) {
		exceptionTable.getItems().add( exception );		
	}

	public void handleStatusMessage(NetLogStatusMessage status) {
		// TODO Auto-generated method stub
		
	}
	
	/*************** GUI *******************/
    @FXML 
	protected void handlePauseAction(ActionEvent event) {
		if(status == Status.RUNNING){
			status = Status.PAUSED;
			logger.info("Logging paused");
		}
		else if(status == Status.PAUSED){
			status = Status.RUNNING;
			logger.info("Logging Unpaused");
		}
		updateStatus();
    }

	@FXML 
	protected void handleDisconnectAction(Event event) {
		logger.info("Disconnecting from Log Server");
		net.close();
		status = Status.DISCONNECTED;
		updateStatus();
    }
	
	@FXML 
	protected void handleLevelChanged(ActionEvent event) {
		logger.info("Updating Log Level");
    }
	
	@FXML 
	protected void handleIntervalChanged(ActionEvent event) {
		logger.info("Updating Log Interval");
    }

	private void updateStatus(){
		if(progressBar == null || pauseButton == null){
			return;
		}
	
		if(status == Status.RUNNING){
			progressBar.setProgress(-1.0);			
			pauseButton.setText("Pause");
		}
		else if(status == Status.PAUSED){
			progressBar.setProgress(1.0);
			pauseButton.setText("Unpause");
		}
		else if(status == Status.DISCONNECTED){
			progressBar.setProgress(0);
			pauseButton.disableProperty();
		}
	}

}