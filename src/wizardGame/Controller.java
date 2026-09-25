package wizardGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Controller {

	@FXML
	private Button newGameButton, selectionsButton, randomizeCPUNamesButton;

	@FXML
	private TextField player1Name, player2Name, player3Name, player4Name, player5Name, player6Name;

	@FXML
	private Label player4NameLabel, player5NameLabel, player6NameLabel;
	@FXML
	private RadioButton threePlayers, fourPlayers, fivePlayers, sixPlayers;
	@FXML
	private Stage stage;

	@FXML
  private StackPane startingStackPane;

	@FXML
	private AnchorPane startingAnchorPane;

	@FXML
	private ImageView menuImage;

	private Scene scene;
	private Parent root;

	int playersNum;
	ArrayList<String> CPUNames;

	public void startNewGame(ActionEvent event) throws IOException {

		playersNum = getPlayersNum();

		// to start new game, no text field must be name //
		if (player1Name.getText().length() == 0 ||
			player2Name.getText().length() == 0 ||
			player3Name.getText().length() == 0 ||
			(playersNum > 3 && player4Name.getText().length() == 0) ||
			(playersNum > 4 && player5Name.getText().length() == 0) ||
			(playersNum == 6 && player6Name.getText().length() == 0)) {

			// this must be a pop up error //
				System.out.println("Error! No Name field must be empty!");
				return;
		}
		CPUNames = new ArrayList<>();

		String player1NameString = player1Name.getText();

		setCPUNames();

		System.out.printf("Starting New Game with User Name: %s and CPU Players: ", player1NameString);
		printCPUPlayers();

		FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
	    Parent root = loader.load(); // THIS is when GameController is created in memory

	    // 2. Grab the controller object from the loader
	    GameController gameController = loader.getController();

	    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	    stage.setScene(new Scene(root));
	    stage.setResizable(true);
	    stage.show();

	    gameController.initGame(playersNum, player1NameString, CPUNames);
	}

	void setCPUNames() {
		CPUNames.add(player2Name.getText());
		CPUNames.add(player3Name.getText());
		if (playersNum > 3) {
      CPUNames.add(player4Name.getText());
    }
		if (playersNum > 4) {
      CPUNames.add(player5Name.getText());
    }
		if (playersNum == 6) {
      CPUNames.add(player6Name.getText());
    }
	}

	void printCPUPlayers() {

		System.out.printf("%s", CPUNames.get(0));

		for (int i = 1; i < playersNum-1; i++) {
			System.out.printf(", %s", CPUNames.get(i));
		}

		System.out.printf("\n");

	}

	public void goToSelections(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("selections.fxml"));
		stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();

	}

	public void goToGame(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("game.fxml"));
		stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();

	}

	public int getPlayersNum() {

		if (threePlayers.isSelected()) {
      playersNum = 3;
    } else if (fourPlayers.isSelected()) {
      playersNum = 4;
    } else if (fivePlayers.isSelected()) {
      playersNum = 5;
    } else {
      playersNum = 6;
    }

		return playersNum;
	}

	public void randomizeCPUNames(ActionEvent e) {
		 String[] CPUNamesRandom = getRandomNames(getPlayersNum());

		 player2Name.setText(CPUNamesRandom[0]);
		 player3Name.setText(CPUNamesRandom[1]);
		 player4Name.setText(CPUNamesRandom[2]);
		 player5Name.setText(CPUNamesRandom[3]);
		 player6Name.setText(CPUNamesRandom[4]);
	}

	public void showThreePlayers(ActionEvent e) {
		player4Name.setVisible(false);
		player4NameLabel.setVisible(false);
		player5Name.setVisible(false);
		player5NameLabel.setVisible(false);
		player6Name.setVisible(false);
		player6NameLabel.setVisible(false);
	}

	public void showFourPlayers(ActionEvent e) {
		player4Name.setVisible(true);
		player4NameLabel.setVisible(true);
		player5Name.setVisible(false);
		player5NameLabel.setVisible(false);
		player6Name.setVisible(false);
		player6NameLabel.setVisible(false);
	}

	public void showFivePlayers(ActionEvent e) {
		player4Name.setVisible(true);
		player4NameLabel.setVisible(true);
		player5Name.setVisible(true);
		player5NameLabel.setVisible(true);
		player6Name.setVisible(false);
		player6NameLabel.setVisible(false);
	}

	public void showSixPlayers(ActionEvent e) {
		player4Name.setVisible(true);
		player4NameLabel.setVisible(true);
		player5Name.setVisible(true);
		player5NameLabel.setVisible(true);
		player6Name.setVisible(true);
		player6NameLabel.setVisible(true);
	}

	// below function gets names for all players, even if they are not visible //
	public String[] getRandomNames(int numPlayers) {
        List<String> allNames = new ArrayList<>();

        try {
            InputStream stream = getClass().getResourceAsStream("randomNames.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            while ((line = reader.readLine()) != null) {
                allNames.add(line.trim());
            }
            reader.close();

            Collections.shuffle(allNames);

            String[] CPUNamesRandom = new String[5];
            CPUNamesRandom[0] = allNames.get(0);
            CPUNamesRandom[1] = allNames.get(1);
            CPUNamesRandom[2] = allNames.get(2);
            CPUNamesRandom[3] = allNames.get(3);
            CPUNamesRandom[4] = allNames.get(4);

            return CPUNamesRandom;

        } catch (Exception e) {
            System.out.println("Could not find or read the file!");
            e.printStackTrace();
            return new String[] {"Player 2", "Player 3", "Player 4", "Player 5", "Player 6"}; // Fallback names
        }
	}

}
