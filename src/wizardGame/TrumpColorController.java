package wizardGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import wizardGame.gameLogic.card.CardColor;

public class TrumpColorController {

  @FXML
  private RadioButton redButton, greenButton, yellowButton, blueButton;

  @FXML
  private Button setTrumpColorButton;

  private CardColor trumpColor = CardColor.Red; // defaulted, so even if user closes, red is chosen //
  private boolean trumpColorChosen = false;

  public void setTrumpColor(ActionEvent event) {

    if (redButton.isSelected()) {
      trumpColor = CardColor.Red;
    }
    else if (yellowButton.isSelected()) {
      trumpColor = CardColor.Yellow;
    }
    else if (blueButton.isSelected()) {
      trumpColor = CardColor.Blue;
    }
    else if (greenButton.isSelected()) {
      trumpColor = CardColor.Green;
    }

    trumpColorChosen = true;

    Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
    stage.close();

  }

  public CardColor getTrumpColor() {
    return trumpColor;
  }

  public boolean isTrumpColorChosen() {
    return trumpColorChosen;
  }
}
