package zutil.log.netlog;

import java.net.URL;
import java.util.ResourceBundle;

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
 
public class NetLoggerClientInstance implements Initializable {
	private enum Status{RUNNING, PAUSED, DISCONNECTED}

	// Logic variables
	private Status status;

	private final ObservableList<NLExceptionData> exceptionData =
	        FXCollections.observableArrayList(
	            new NLExceptionData("java.lang.NullPointerException", "", " at com.example.myproject.Book.getTitle(Book.java:16) \n at com.example.myproject.Author.getBookTitles(Author.java:25) \n at com.example.myproject.Bootstrap.main(Bootstrap.java:14)"),
	            new NLExceptionData("java.lang.NullPointerException", "", " at com.example.myproject.Book.getTitle(Book.java:16) \n at com.example.myproject.Author.getBookTitles(Author.java:25) \n at com.example.myproject.Bootstrap.main(Bootstrap.java:14)"),
	            new NLExceptionData("java.io.FileNotFoundException", "fred.txt", " at java.io.FileInputStream.<init>(FileInputStream.java) \n at java.io.FileInputStream.<init>(FileInputStream.java) \n at ExTest.readMyFile(ExTest.java:19) \n at ExTest.main(ExTest.java:7)")
	        );
	
	// UI elements
	@FXML private ToggleButton pauseButton;
	
    @FXML private TableView<NLLogData> logTable;
    @FXML private TableColumn<NLLogData, Long> logTimestampColumn;
    @FXML private TableColumn<NLLogData, String> logLevelColumn;
    @FXML private TableColumn<NLLogData, String> logColumn;
    
	@FXML private TableView<NLExceptionData> exceptionTable;
    @FXML private TableColumn<NLExceptionData, Long> exCountColumn;
    @FXML private TableColumn<NLExceptionData, String> exNameColumn;
    @FXML private TableColumn<NLExceptionData, String> exMessageColumn;
    @FXML private TableColumn<NLExceptionData, String> exStackTraceColumn;
	
	@FXML private Label logCountLabel;
	@FXML private ProgressBar progressBar;
    

	public void initialize(URL arg0, ResourceBundle arg1) {
		status = Status.RUNNING;
		updateStatus();
		
		logTimestampColumn.setCellValueFactory(new PropertyValueFactory<NLLogData, Long>("timestamp"));
		logLevelColumn.setCellValueFactory(new PropertyValueFactory<NLLogData, String>("level"));
		logColumn.setCellValueFactory(new PropertyValueFactory<NLLogData, String>("log"));
		
		exCountColumn.setCellValueFactory(new PropertyValueFactory<NLExceptionData, Long>("count"));
		exNameColumn.setCellValueFactory(new PropertyValueFactory<NLExceptionData, String>("name"));
		exMessageColumn.setCellValueFactory(new PropertyValueFactory<NLExceptionData, String>("message"));
		exStackTraceColumn.setCellValueFactory(new PropertyValueFactory<NLExceptionData, String>("stackTrace"));
		
		//logTable.setItems(logData);
		exceptionTable.setItems(exceptionData);
	}
	
    @FXML 
	protected void handlePauseAction(ActionEvent event) {
		if(status == Status.RUNNING){
			status = Status.PAUSED;
			System.out.println("Logging Paused");
		}
		else if(status == Status.PAUSED){
			status = Status.RUNNING;
			System.out.println("Logging Unpaused");
		}
		updateStatus();
    }

	@FXML 
	protected void handleDisconnectAction(Event event) {
		System.out.println("Disconnected changed");
		status = Status.DISCONNECTED;
		updateStatus();
    }
	
	@FXML 
	protected void handleLevelChanged(ActionEvent event) {
		System.out.println("Level changed");
    }
	
	@FXML 
	protected void handleIntervalChanged(ActionEvent event) {
		System.out.println("Interval changed");
    }

	private void updateStatus(){
		if(progressBar == null || pauseButton == null){
			System.out.println("progressBar="+progressBar+" pauseButton="+pauseButton);
			return;
		}
		
		System.out.println("Status: "+status);
	
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