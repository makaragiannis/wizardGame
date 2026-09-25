package wizardGame.gameLogic.assumption;

import wizardGame.gameLogic.card.CardColor;
import wizardGame.gameLogic.player.Player;

// assumption is when we know that a Player has no cards of a Color //
public class Assumption {

  String assumptionText;
  Player player;
  CardColor color;

  public Assumption(Player player, CardColor color) {

    this.player = player;
    this.color = color;
    this.assumptionText = player.getName() + " has no " + color.getColor() + " cards.";
  }

  public String getAssumptionText() {

    return assumptionText;
  }

}
