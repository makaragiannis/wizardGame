package wizardGame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int playerNum = 4;


//		gameplay.generateCards();
//		gameplay.printCards();
//		gameplay.shuffleCards();
//		gameplay.printCards();

		launch(args);

	}

	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub

		Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
		Scene scene = new Scene(root);

		String css = this.getClass().getResource("style.css").toExternalForm(); // method 2 //
		scene.getStylesheets().add(css);

		stage.setScene(scene);

		stage.setResizable(false);
		stage.setTitle("Wizard");
		stage.show();


	}


}
