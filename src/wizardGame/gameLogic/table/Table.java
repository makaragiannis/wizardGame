package wizardGame.gameLogic.table;

import java.util.ArrayList;

import wizardGame.Trick.Trick;
import wizardGame.gameLogic.card.Card;
import wizardGame.gameLogic.card.CardColor;

public class Table {
	
	Card trumpCard;
	Card leadCard;
	ArrayList<Card> playedCards;
	CardColor trumpColor;
	CardColor leadColor;
	Trick currentTrick;
	
	int tricksPlayed;

	public Table() {
		
		trumpCard = null;
		leadCard = null;
		leadColor = null;
		currentTrick = null;
		playedCards = new ArrayList<>();
		
		tricksPlayed = 0;
		
	}
	
	public void setTrumpCard(Card card) {
		
		trumpCard = card;
		trumpColor = card.getCardColor();

	}
	
	public void setTrumpColor(CardColor color) {
		
		trumpColor = color;
	}
	
	public Card getTrumpCard() {
		
		return trumpCard;
	}
	
	public CardColor getTrumpColor() {
		
		return trumpColor;
	}
	
	public CardColor getLeadColor() {
		
		return leadColor;
	}
	
	public Trick getCurrentTrick() {
		return currentTrick;
	}
	
	public void printTrumpCard() {
		trumpCard.printCard();
	}
	
	public void setLeadCard(Card card) {
		
		leadCard = card;
		leadColor = card.getCardColor();
		
		System.out.println("DEBUG: Lead Color set to " + leadColor.getColor());
	}
	
	public void setLeadColor(CardColor color) {
		leadColor = color;
	}
	
	public Card getLeadCard() {
		
		return leadCard;
	}
	
	public void addPlayedCard(Card card) {
		
		playedCards.add(card);
	}
	
	public void resetTable() {
		trumpCard = null;
		leadCard = null;
		leadColor = null;
		playedCards.clear();
		
//		currentTrick.resetTrick();
		
		tricksPlayed = 0;
		
		System.out.println("DEBUG: Table is reset.");
	}
	
	public ArrayList<Card> getPlayedCards() {
		return playedCards;
	}
	
	public void resetLeadCard() {
		leadCard = null;
		leadColor = null;
	}
	
	public void initNewTrick() {
		currentTrick = new Trick();
	}
	
	public void increaseTricksPlayed() {
		tricksPlayed++;
	}
	
	public int getNumTricksPlayed() {
		return tricksPlayed;
	}

}
