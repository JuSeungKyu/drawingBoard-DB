package application;

import java.net.URL;

import db.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			new JDBC("localhost", "", "root", "");
			
			AnchorPane ap = (AnchorPane)FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
			Scene scene = new Scene(ap);
			
			primaryStage.setTitle("Drawing Board App");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
