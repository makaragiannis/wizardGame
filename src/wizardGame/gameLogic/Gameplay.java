package wizardGame.gameLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import wizardGame.Trick.Trick;
import wizardGame.gameLogic.assumption.Assumption;
import wizardGame.gameLogic.card.Card;
import wizardGame.gameLogic.card.CardColor;
import wizardGame.gameLogic.card.Value;
import wizardGame.gameLogic.player.AIPlayer;
import wizardGame.gameLogic.player.Player;
import wizardGame.gameLogic.player.UserPlayer;
import wizardGame.gameLogic.table.Table;

public class Gameplay {

	final int playerNum;
	final int cardNum = 60;
	int round;
	int cardsNum;
	int maxRound;
	int firstPlayer;

	Card[] cards;
	Player[] players;
	Table table;

	Scanner scanner = new Scanner(System.in);

	public Gameplay(int playerNum, String playerName, ArrayList<String> CPUNames) {

		this.playerNum = playerNum;

		cards = new Card[cardNum];
		players = new Player[playerNum];
		table = new Table();

		maxRound = cardNum / playerNum;

//		maxRound = 3;
//		round = maxRound - 1;
		round = 0;

		generateCards();
		initPlayers(playerName, CPUNames);
		firstPlayer = pickFirstPlayer();


		scanner.close();
	}

	public void initPlayers(String playerName, ArrayList<String> CPUNames) {
		// player 1 is always UserPlayer //
		// the rest are AIPlayer //

//		String[] placeholderNames = {"Dimitris", "Giorgos", "Leonidas", "Vaggelis", "Nikos"};

		players[0] = new UserPlayer(playerName, maxRound);

		for (int i = 1; i < playerNum; i++) {

			players[i] = new AIPlayer(CPUNames.get(i-1).toString(), maxRound);
			players[i-1].setNextPlayer(players[i]);
		}

		players[playerNum - 1].setNextPlayer(players[0]);
	}

	public void initNewTrick() {
		table.initNewTrick();
	}

	public void generateCards() {

		// four J, four W, and 4 sets of cards 1-13, one for each color //

		CardColor[] colors = {CardColor.Red, CardColor.Yellow, CardColor.Blue, CardColor.Green};
		Value[] values = {Value.N1, Value.N2, Value.N3, Value.N4, Value.N5, Value.N6, Value.N7, Value.N8, Value.N9, Value.N10, Value.N11, Value.N12, Value.N13};

		for (int i = 0; i < 4; i++) {

			for (int j = 0; j < 13; j++) {

				cards[i*13 + j] = new Card(colors[i], values[j]);
			}
		}

		for (int i = 52; i < 56; i++) {
			cards[i] = new Card(CardColor.Colorless, Value.W);
		}

		for (int i = 56; i < 60; i++) {
			cards[i] = new Card(CardColor.Colorless, Value.J);
		}
	}

	public void printCards() {

		for (int i = 0; i < cardNum; i++) {
			System.out.printf("Card %d: ", i);
			cards[i].printCard();
		}
	}

	public void shuffleCards() {

		Collections.shuffle(Arrays.asList(cards));
	}

	int pickFirstPlayer() {

		// for now, the first player is always player 0 //

//		return 0;
		Random random = new Random();
		return random.nextInt(playerNum);
	}

	public void setRound(int round) {
		this.round = round;
	}

	public void increaseRound() {
		round++;
	}

	public int getNumTricksPlayed() {
		return table.getNumTricksPlayed();
	}

	public void increaseTricksPlayed() {
		table.increaseTricksPlayed();
	}

	public int getNumCardsPlayed() {
		return table.getCurrentTrick().getNumCardsPlayed();
	}

	public void increaseCardsPlayed() {
		table.getCurrentTrick().increaseCardsPlayed();
	}

	 // playable card refers to a card that the player can ALWAYS play based on the current lead color //
  public boolean sameColorCardExists(ArrayList<Card> handCards) {

    CardColor leadColor = getLeadColor();

    // if the lead color is null, or colorless, all cards are playable //
    if (leadColor == null || leadColor == CardColor.Colorless) {
      return true;
    }

    // else, if there exists at least one card in the hand that is either colorless
    // or same color as leadcolor, then it is playable //

    for (Card card : handCards) {
      if (card.getCardColor() == leadColor) {
        return true;
      }
    }

    // if none is found, no playable card exists //
    return false;
  }

	public void playRound(Scanner scanner) {

		CardColor trumpColor;
		Player trumpColorChooser;

		System.out.println("Proceed to next round? ");

		scanner.nextLine();

		round++;
		System.out.printf("Round %d: \n", round);
		shuffleCards();

		dealCards();

		System.out.println("Your hand is: ");
		players[0].printHand();

		if (round != maxRound) {
			table.setTrumpCard(getNextCard());
			System.out.print("Trump Card is: " );
			table.printTrumpCard();

			if (table.getTrumpCard().getCardValue() == "W") {
				trumpColor = getDealer().determineTrumpColor(scanner);
				table.setTrumpColor(trumpColor);

				System.out.printf("Color %s was chosen as trump color by %s\n", trumpColor.getColor(), getDealer().getName());
			}
		}

		int trickWinner;
		int tricksGuessed[] = getTricksGuessed();
		int winner = firstPlayer; // when the round starts, the first player is the round first player //

		for (int i = 0; i < round; i++) {

//			System.out.println("DEBUG: Player 1 hand is");
//			players[1].printHand(round);
//			System.out.println("DEBUG: Player 2 hand is");
//			players[2].printHand(round);
//			System.out.println("DEBUG: Player 3 hand is");
//			players[3].printHand(round);

			System.out.println("Your hand is: ");
			players[0].printHand();

			trickWinner = playCards(firstPlayer);
			firstPlayer = trickWinner;
			players[trickWinner].incrementTricksCompleted();

			System.out.println("Trick winner: " + players[trickWinner].getName());

			table.resetLeadCard();

		}

//		printTotalTricksGuessed();
		updateScores();
		printScores();

		printScoresPerRound();

		resetHands();
		table.resetTable();


		incrementFirstPlayer();

	}

	public void incrementFirstPlayer() {
		firstPlayer++; // the players who play first at each round rotate //
		if (firstPlayer == playerNum) {
      firstPlayer = 0;
    }
	}

	public Player getRoundFirstPlayer() {
		return players[firstPlayer];
	}

	public Player getDealer() {

		return players[(playerNum + firstPlayer - 1) % playerNum];
	}

	public CardColor AIChooseTrumpColor() {
		return getDealer().determineTrumpColor(scanner);
	}

	public Player getUser() {
		return players[0];
	}

	public CardColor getLeadColor() {
		return table.getLeadColor();
	}

	public void resetLeadCard() {
		 table.resetLeadCard();
	}

	public void setTrumpCard() {
		table.setTrumpCard(getNextCard());
//	  table.setTrumpCard(new Card(CardColor.Colorless, Value.W));
	}

	public Card getTrumpCard() {
		return table.getTrumpCard();
	}

	public int getCurrentRound() {
		return this.round;
	}

	public int getMaxRound() {
		return maxRound;
	}

	public void setTrumpColor(CardColor color) {
		table.setTrumpColor(color);
	}

	public void playCard(Player player, Card card) {
		player.playCard(table, card);

		table.addPlayedCard(card);

		checkAssumption(card.getCardColor(), table.getLeadColor() , player);
	}

	public void resetTrick() {
		table.getCurrentTrick().resetTrick();
	}

	public void resetTable() {
		table.resetTable();
	}

	public ArrayList<Card> getPlayedCards() {
		return table.getPlayedCards();
	}

	public ArrayList<Assumption> getAssumptions() {
	  return table.getAssumptions();
	}


	int playCards(int firstPlayer) {

		// winner plays first //

		int currentPlayer = 0;
		int currentWinner = -1; // initialize to an invalid value //
		int trickWinner;
		int currentMaxCardScore = -1; // initialize to an invalid value //
		int cardScore;
		Card lastPlayedCard;
		CardColor trumpColor;
		CardColor leadColor;

		for (int i = 0; i < playerNum; i++) {

			currentPlayer = (firstPlayer + i) % playerNum;

//			players[currentPlayer].playCard(table) // is required, commented out cause of change //
			lastPlayedCard = players[currentPlayer].getLastPlayedCard();

			System.out.printf("%s played card: ", players[currentPlayer].getName());
			lastPlayedCard.printCard();

			trumpColor = table.getTrumpColor();
			leadColor = table.getLeadColor();
			if ((leadColor == null) || (leadColor == CardColor.Colorless)) {
				leadColor = lastPlayedCard.getCardColor();
			}

			cardScore = lastPlayedCard.getCardScore(trumpColor, leadColor);

			if (cardScore > currentMaxCardScore) {
				currentMaxCardScore = cardScore;
				currentWinner = currentPlayer;
			}

		}

		trickWinner = currentWinner;
		return trickWinner;
	}

	public void updateTrickInfo(Player currentPlayer) {

		Trick currentTrick = table.getCurrentTrick();

		Card lastPlayedCard = currentPlayer.getLastPlayedCard();

		CardColor trumpColor = table.getTrumpColor();
		CardColor leadColor = table.getLeadColor();

		if ((leadColor == null) || (leadColor == CardColor.Colorless)) {
			System.out.println("Error! Lead Color is NULL");
			leadColor = lastPlayedCard.getCardColor();
		}

		int cardScore = lastPlayedCard.getCardScore(trumpColor, leadColor);

		System.out.printf("This card scored %d\n", cardScore);

		if (cardScore > currentTrick.getHighestCardScore()) {
			currentTrick.setHighestScoringCard(lastPlayedCard, cardScore);
			currentTrick.setCurrentTrickWinner(currentPlayer);
		}

	}

	public Player getTrickWinner() {
		return table.getCurrentTrick().getCurrentTrickWinner();
	}

	public void updateScores() {
		for (int i = 0; i < playerNum; i++) {
			players[i].calcScore(round-1);
		}
	}

	public void updateLeadColor() {

	}

	public Player getWinner() {

		// scan players until we find the one with the highest score //

		// initialize with player 0 as winner //
		Player currentWinner = players[0];
		int currentHighScore = players[0].getScore();

		for (Player player : players) {
			if (player.getScore() > currentHighScore) {
				currentWinner = player;
				currentHighScore = player.getScore();
			}
		}

		return currentWinner;
	}

	void printScores() {
		System.out.printf("Round %d scores: \n", round);
		for (int i = 0; i < playerNum; i++) {
			System.out.printf("%s: %d\n", players[i].getName(), players[i].getRoundScore(round-1));
		}
	}

	void printScoresPerRound() {
		System.out.printf("\nRound %10s %10s %10s %10s\n", players[0].getName(), players[1].getName(), players[2].getName(), players[3].getName());
		for (int i = 0; i < maxRound; i++) {
			System.out.printf("%2d    %10d %10d %10d %10d\n", i+1, players[0].getRoundScore(i), players[1].getRoundScore(i), players[2].getRoundScore(i), players[3].getRoundScore(i));
		}
		System.out.println();
	}

	public void selectAndPlayCard(Player player) {
		Card playedCard = player.selectCardToPlay(table, null);
		player.playCard(table, playedCard);
	}

	// check if an assumption can be made out of a play, and add it if so //
	public void checkAssumption(CardColor playedCardColor, CardColor leadColor, Player player) {

	  if (playedCardColor == CardColor.Colorless || leadColor == CardColor.Colorless || leadColor == null) {
	    return; // no assumption can be made //
	  }

	  if (playedCardColor != leadColor) {
	    table.addAssumption(new Assumption(player, leadColor));
	  }
	}

	int[] getTricksGuessed() {

		int tricksGuessed;
		int currentPlayer;

		int[] totalTricksGuessed = new int[playerNum];

		// will get as many tricks as playersNum, but will start from firstPlayer //

		for (int i = 0; i < playerNum; i++) {

			currentPlayer = (firstPlayer + i) % playerNum;
			players[currentPlayer].guessTricks(round, playerNum, scanner);
			tricksGuessed = players[currentPlayer].getTricksGuessed();

			System.out.printf("%s: %d tricks\n", players[currentPlayer].getName(), players[currentPlayer].getTricksGuessed());

			totalTricksGuessed[currentPlayer] = tricksGuessed;
		}

		return totalTricksGuessed;
	}

	void printTotalTricksGuessed() {

		for (int i = 0; i < playerNum; i++) {

			System.out.printf("%s: %d tricks\n", players[i].getName(), players[i].getTricksGuessed());
		}
	}

	public void dealCards() {

		for (int i = 0; i < playerNum; i++) {
			for (int j = 0; j < round; j++) {
				players[i].addHandCard(cards[i*round + j]);
			}
		}
	}

	public void resetHands() {

		for (int i = 0; i < playerNum; i++) {
			players[i].resetPlayer();
		}
	}

	Card getNextCard() {
		// in each round, the players have (round*playerNum) cards in their hands //
		// so to get the next card, just get the card with index round*playerNum //

		return cards[round*playerNum];
	}


	public void printTotalScores() {
		for (int i = 0; i < playerNum; i++) {
			System.out.printf("%s: Score %d.\n", players[i].getName(), players[i].getScore());
		}
	}

	public ArrayList<Card> getPlayerHandCards(boolean sort) {

		if (sort == true) {
      players[0].sortHand();
    }

		return players[0].getHandCards();

	}

	// get all player info //
	public Player[] getPlayers() {
		return players;
	}

	public void printPlayerHandCards() {
		players[0].printHand();
	}

	public void printWinner() {

		int maxScore = players[0].getScore();
		Player winner = players[0];

		for (int i = 1; i < playerNum; i++) {
			if (players[i].getScore() > maxScore) {
				winner = players[i];
				maxScore = winner.getScore();
			}
		}

		System.out.printf("\nWinner: %s!!\n", winner.getName());
	}


	public void setTricksGuessed(Player player, int tricksGuessed) {
		player.setTricksGuessed(tricksGuessed);
	}

}
