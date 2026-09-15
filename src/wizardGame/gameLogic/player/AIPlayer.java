package wizardGame.gameLogic.player;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import wizardGame.gameLogic.card.Card;
import wizardGame.gameLogic.card.CardColor;
import wizardGame.gameLogic.table.Table;

public class AIPlayer extends Player{
	
	public AIPlayer(String name, int maxRound) {
		super(name, maxRound);
	}
	
	int guessRandomTrickNumber(int cardsNum, int playerNum) {
		
		// 40% chance to guess roundNum / playerNum, rounded //
		// 30% to guess one higher //
		// 30% to guess one lower //
		
		Random random = new Random();
		
		int averageGuess = Math.round(((float) cardsNum / playerNum));
		int randInt = random.nextInt(0, 10);
		
		if (randInt < 4) { // 0, 1, 2, 3 //
			return Math.max(0,  averageGuess);
		}
		if (randInt < 7) { // 4, 5, 6 //
			return Math.max(0,  averageGuess - 1);
		}
		// 7, 8, 9 //
		return Math.min(cardsNum,  averageGuess + 1);
	}
	
	public void guessTricks(int round, int playerNum, Scanner scanner) {
		
		setTricksGuessed(guessRandomTrickNumber(round, playerNum));
	}
	
	public Card selectCardToPlay(Table table, Scanner scanner) {
		
	    ArrayList<Integer> invalidIndices;
	    Random random = new Random();
	    int cardIndex;

	    
    	invalidIndices = getInvalidIndices(table.getLeadColor());

		do {
			cardIndex = random.nextInt(0, handCards.size());
		}
		while (isCardIndexInvalid(cardIndex, invalidIndices) == true);
		
		return handCards.get(cardIndex);
	}
	
	public CardColor determineTrumpColor(Scanner scanner) {
		
		// for now pick a color at random //
		
		Random random = new Random();
		CardColor[] colors = {CardColor.Red, CardColor.Blue, CardColor.Yellow, CardColor.Blue};
		
		return colors[random.nextInt(0, 4)];
		
	}

	
	public boolean isUser() {
		return false;
	}

}
