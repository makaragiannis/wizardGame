package wizardGame;

import java.util.ArrayList;
import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import wizardGame.gameLogic.assumption.Assumption;



public class AssumptionsController {

  @FXML
  private VBox assumptionsVBox;

  public void addAssumptions(ArrayList<Assumption> assumptions) {

    ArrayList<Assumption> newAssumptions = new ArrayList<>(assumptions);

    newAssumptions.sort(Comparator.comparing(Assumption::getAssumptionText));

    assumptionsVBox.getChildren().clear();

    String previousString = null;

    for (Assumption assumption : newAssumptions) {

      // if an assumption has the same string as the previous one, remove it //
      if (assumption.getAssumptionText().equals(previousString)) {
        continue; // do not display it //
      }

      previousString = assumption.getAssumptionText();

      Label newAssumptionsLabel = new Label(assumption.getAssumptionText());
      assumptionsVBox.getChildren().add(newAssumptionsLabel);
    }
  }
}
