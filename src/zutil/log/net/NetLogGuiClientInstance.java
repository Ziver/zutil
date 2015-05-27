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

package zutil.log.net;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.control.*;
import zutil.log.LogUtil;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class NetLogGuiClientInstance implements Initializable, NetLogListener {
	private static final Logger logger = LogUtil.getLogger();
	private static enum Status{RUNNING, PAUSED, DISCONNECTED}

	// Logic variables
	private NetLogClient net;
	private Status status;

	// UI elements
	@FXML private ToggleButton pauseButton;
    @FXML private Label levelLabel;
    @FXML private ComboBox levelComboBox;
    @FXML private Label intervalLabel;
    @FXML private ComboBox intervalComboBox;
    @FXML private ProgressBar progressBar;
    @FXML private Label errorLabel;
    @FXML private Label logCountLabel;

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
			net = new NetLogClient("127.0.0.1", 5050);
			net.addListener( this );
			status = Status.RUNNING;		
		}catch(Exception e){
			logger.log(Level.SEVERE, null, e);
			status = Status.DISCONNECTED;
			errorLabel.setText(e.getMessage());
		}
		updateStatus();

		// Setup Gui
		logTimestampColumn.setCellValueFactory(new PropertyValueFactory<NetLogMessage, Long>("timestamp"));
		logLevelColumn.setCellValueFactory(new PropertyValueFactory<NetLogMessage, String>("level"));
		logLevelColumn.setCellFactory(new RowCssCellFactory<NetLogMessage,String>(){
			public String getStyleName(String item){
				return item;
			}
		});
		logColumn.setCellValueFactory(new PropertyValueFactory<NetLogMessage, String>("log"));

		exCountColumn.setCellValueFactory(new PropertyValueFactory<NetLogExceptionMessage, Long>("count"));
		exNameColumn.setCellValueFactory(new PropertyValueFactory<NetLogExceptionMessage, String>("name"));
		exMessageColumn.setCellValueFactory(new PropertyValueFactory<NetLogExceptionMessage, String>("message"));
		exStackTraceColumn.setCellValueFactory(new PropertyValueFactory<NetLogExceptionMessage, String>("stackTrace"));
	}

	/************* NETWORK *****************/
	public void handleLogMessage(NetLogMessage msg) {
		if(status == Status.RUNNING){
			logTable.getItems().add(msg);

            Platform.runLater(new Runnable() {
                public void run() {
                    logCountLabel.setText("" + (Long.parseLong(logCountLabel.getText()) + 1));
                }
            });
		}
	}

	public void handleExceptionMessage(NetLogExceptionMessage msg) {
		if(status == Status.RUNNING){
			exceptionTable.getItems().remove(msg);
			exceptionTable.getItems().add(msg);
		}	
	}

	public void handleStatusMessage(NetLogStatusMessage msg) {
		if(status == Status.RUNNING){
			
		}	
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
			pauseButton.setDisable(true);
            levelLabel.setDisable(true);
            levelComboBox.setDisable(true);
            intervalLabel.setDisable(true);
            intervalComboBox.setDisable(true);

            logTable.setDisable(true);
            exceptionTable.setDisable(true);

            progressBar.setProgress(0);
            logCountLabel.setDisable(true);
		}
	}

	/**
	 * http://stackoverflow.com/questions/13697115/javafx-tableview-colors
	 */
	public abstract class RowCssCellFactory<S,T> implements Callback<TableColumn<S,T>, TableCell<S,T>> {

		public TableCell<S,T> call(TableColumn<S,T> p) {
			TableCell<S, T> cell = new TableCell<S, T>() {
				@Override
				public void updateItem(T item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty ? null : getString());
					setGraphic(null);

					String style = getStyleName(item);
					if(style != null){
						TableRow<?> row = getTableRow();
						row.getStyleClass().add(style);
					}
				}

				@Override
				public void updateSelected(boolean upd){
					super.updateSelected(upd);
				}


				private String getString() {
					return getItem() == null ? "NULL" : getItem().toString();
				}
			};
			return cell;
		}

		public abstract String getStyleName(T item);
	}
}