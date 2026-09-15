package wizardGame.Trick;

import wizardGame.gameLogic.card.Card;
import wizardGame.gameLogic.player.Player;

public class Trick {
	
	Card trumpCard;
	Card leadCard;
	Player currentTrickWinner;
	Card highestScoringCard;
	int highestCardScore;
	
	int cardsPlayed;
	
	public Trick() {
		
		resetTrick();
	}
	
	public Player getCurrentTrickWinner() {
		return currentTrickWinner;
	}
	
	public void setCurrentTrickWinner(Player player) {
		currentTrickWinner = player;
	}
	
	public void setHighestScoringCard(Card card) {
		this.highestScoringCard = card;
	}
	
	public void setHighestScoringCard(Card card, int score) {
		this.highestScoringCard = card;
		this.highestCardScore = score;
	}
	
	public Card getHighestScoringCard() {
		return highestScoringCard;
	}
	
	public int getHighestCardScore() {
		return highestCardScore;
	}
	
	public void setHighestCardScore(int score) {
		this.highestCardScore = score;
	}
	
	public void increaseCardsPlayed() {
		cardsPlayed++;
	}
	
	public int getNumCardsPlayed() {
		return cardsPlayed;
	}
	
	public void resetTrick() {
		this.currentTrickWinner = null;
		this.highestScoringCard = null;
		this.highestCardScore = 0;
		this.cardsPlayed = 0;
	}
}
