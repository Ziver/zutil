package zutil.log.netlog;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
 
public class NetLoggerClient extends Application{
	public static final String VERSION = "0.1";
		
	// UI elements
    @FXML 
	private TabPane tabPane;

	
	public static void main(String[] args) {
        Application.launch(NetLoggerClient.class, args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("NetLoggerClient.fxml"));
        
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
			
			Parent tabRoot = FXMLLoader.load(getClass().getResource("NetLoggerClientInstance.fxml"));
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