package wizardGame;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class WinnerController {
	
	@FXML
	private Button newGameButton;
	
	@FXML
	private Label winnerLabel;
	
	public void updateWinner(String winnerName) {
		
		winnerLabel.setText(winnerName);
	}
	
	public void startNewGame(ActionEvent event) throws IOException {
		
		System.out.println("Start New Game");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("selections.fxml"));
		Parent root = loader.load();

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		
		Scene scene = new Scene(root);
		
		stage.setScene(scene);
		stage.show();
	}

}
