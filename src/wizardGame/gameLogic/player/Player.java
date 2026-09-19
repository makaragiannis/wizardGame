package wizardGame.gameLogic.player;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

import wizardGame.gameLogic.card.Card;
import wizardGame.gameLogic.card.CardColor;
import wizardGame.gameLogic.table.Table;

public abstract class Player {

	ArrayList<Card> handCards;
	String name;
	int tricksGuessed;
	int tricksCompleted;
	int score;
	int playedCardScore;
	int[] scoresPerRound;
	Player nextPlayer;

	Card lastPlayedCard;

	public Player(String name, int maxRound) {

		this.name = name;

		tricksGuessed = 0;
		tricksCompleted = 0;
		score = 0;
		lastPlayedCard = null;
		playedCardScore = -1;

		nextPlayer = null;

		scoresPerRound = new int[maxRound];

		handCards = new ArrayList<Card>();
	}


	public void resetPlayer() {

		tricksGuessed = 0;
		tricksCompleted = 0;
		lastPlayedCard = null;

		// cards in hand should be 0 //
	}

	public void setNextPlayer(Player nextPlayer) {
		this.nextPlayer = nextPlayer;
	}

	public Player getNextPlayer() {
		return this.nextPlayer;
	}

	public void resetPlayedCardScore() {
		playedCardScore = -1;
	}

	public String getName() {
		return name;
	}

	public void addHandCard(Card card) {

		handCards.add(card);
	}


	public void playCard(Table table, Card card) {

		Card playedCard = card;
		handCards.remove(playedCard);

		lastPlayedCard = playedCard;

		System.out.printf("Played Crd: ");

		if (table.getLeadCard() == null) {
			System.out.println("Set Lead Card");
			table.setLeadCard(playedCard);
		}

		else if ((table.getLeadCard().getCardValue() == "J") && (card.getCardValue()!= "J")
				&& (table.getLeadColor() == CardColor.Colorless)) {

			// bug: here we must also check that the lead color is not colorless //
			table.setLeadColor(card.getCardColor());
			System.out.println("Set Lead Color");
		}

	}

	public void incrementTricksCompleted() {
		tricksCompleted++;
	}

	public Card getLastPlayedCard() {
		return lastPlayedCard;
	}

	public int getTricksGuessed() {

		return tricksGuessed;
	}

	public void setTricksGuessed(int tricksGuessed) {

		this.tricksGuessed = tricksGuessed;
	}

	public int getTricksCompleted() {

		return tricksCompleted;
	}

	public void calcScore(int currentRound) {

		int roundScore;

		if (tricksGuessed == tricksCompleted) {
			roundScore = 20 + 10*tricksGuessed;
		}
		else {
			roundScore = -10*Math.abs(tricksGuessed - tricksCompleted);
		}

		scoresPerRound[currentRound] = roundScore;
		score += roundScore;
	}

	public int getScore() {
		return this.score;
	}

	public int getRoundScore(int round) {
		return scoresPerRound[round];
	}

	public void printHand() {
		for (int i = 0; i < handCards.size(); i++) {
			System.out.print((i+1) + ": ");
			handCards.get(i).printCard();
		}
	}

	public void sortHand() {

		handCards.sort(
		        Comparator.comparing(Card::getCardColor)
		                  .thenComparing(Card::getCardNumber)
		    );

	}

	public ArrayList<Card> getHandCards() {
		return handCards;
	}

	public abstract void guessTricks(int cardsNum, int playerNum, Scanner scanner);

	public abstract CardColor determineTrumpColor(Scanner scanner);

	public abstract Card selectCardToPlay(Table table, Scanner scanner);

	public ArrayList<Integer> getInvalidIndices(CardColor leadColor) {

		ArrayList<Integer> invalidIndices = new ArrayList<>();
		CardColor cardColor;
		boolean sameColorCardExists = false;

		// if no lead color exists, this means that the card played now sets the lead color //
		// so no restrictions //

		// if the lead color is colorless, there also no restrictions (for now) //

		if (leadColor == null || leadColor == CardColor.Colorless) {
			return invalidIndices;
		}

		// if the lead card has a color (is not W or J) //
		// then all players MUST play a card of that color (if they have it) //
		// they can also (optionally) play W or J //

		// first check if they have at least one card of that color //
		for (int i = 0; i < handCards.size(); i++) {

			cardColor = handCards.get(i).getCardColor();
			if (cardColor == leadColor) {
				sameColorCardExists = true;
				break;
			}
		}

		if (sameColorCardExists == false) {
			return invalidIndices; // return empty arrayList //
		}

		// if not, no indices are invalid //
		// if they do, they can only play cards of that color, as well as colorless cards //

		for (int i = 0; i < handCards.size(); i++) {

			cardColor = handCards.get(i).getCardColor();
			if ((cardColor != leadColor) && (cardColor != CardColor.Colorless)) {
				invalidIndices.add(i);
			}
		}

		return invalidIndices;
	}

	boolean isCardIndexInvalid(int cardIndex, ArrayList<Integer> invalidIndices) {

		for (int i = 0; i < invalidIndices.size(); i++) {
			if (invalidIndices.get(i) == cardIndex) {
				return true;
			}
		}

		return false;
	}

	public abstract boolean isUser();

}
