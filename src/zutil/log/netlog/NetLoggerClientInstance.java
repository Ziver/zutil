package zutil.log.netlog;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
 
public class NetLoggerClientInstance extends Tab{
	private enum Status{RUNNING, PAUSED, DISCONNECTED}

	// Logic variables
	private Status status;

	// UI elements
	@FXML 
	private ToggleButton statusBt;
	
    @FXML 
	private TableView logTb;
	@FXML 
	private TableView exceptionTb;
	
	@FXML 
	private Label logCountLb;
	@FXML 
	private ProgressBar progressBar;
    
	public NetLoggerClientInstance(){}
	
	public NetLoggerClientInstance(String host, int port) throws IOException{
		this.setText( host+":"+port );
		
		Parent tabRoot = FXMLLoader.load(getClass().getResource("NetLoggerClientInstance.fxml"));
		this.setContent(tabRoot);
		AnchorPane.setRightAnchor(tabRoot, 0.0);
		this.setOnClosed(new EventHandler<Event>() {
			public void handle(Event e) {
				handleDisconnectAction(e);
			}
		});
		updateStatus();
	}
	
    @FXML 
	protected void handlePauseAction(ActionEvent event) {
		System.out.println("Pause changed");
		if(status == Status.RUNNING){
			status = Status.PAUSED;
		}
		else if(status == Status.PAUSED){
			status = Status.RUNNING;
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
		if(progressBar == null || statusBt == null)
			return;
	
		if(status == Status.RUNNING){
			progressBar.setProgress(1);
			statusBt.setText("Pause");
		}
		else if(status == Status.PAUSED){
			progressBar.setProgress(-1);
			statusBt.setText("Unpause");
		}
		else if(status == Status.DISCONNECTED){
			progressBar.setProgress(0);
			statusBt.disableProperty();
		}
	}
}