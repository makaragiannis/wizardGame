package wizardGame;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import wizardGame.gameLogic.Gameplay;
import wizardGame.gameLogic.card.Card;
import wizardGame.gameLogic.card.CardColor;
import wizardGame.gameLogic.player.Player;

public class GameController implements Initializable {

	Gameplay gameplay;
	int playersNum;

	@FXML
	private Label roundLabel, trumpColorLabel, leadColorLabel, dealerLabel, nowPlayingLabel, currentTrickWinnerLabel;

	@FXML
	private Button orderCardsButton, nextActionButton, showPlayedCardsButton, showAssumptionsButton;

	@FXML
	private ChoiceBox<Integer> tricksChoiceBox;

	@FXML
	private HBox player1Box, topPlayerInfoBox, player4Box, player5Box, player6Box, centerCards,
				trumpCardBox;

	private Card draggedCard;
	private int actionIndex;

	private Player currentPlayer;

	private Boolean nullValueChosen = false;
	private Boolean userPlayedCard = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

//		tricksChoiceBox.getItems().addAll(getAvailableTrickGuesses(1));

	}

	void initGame(int playersNum, String playerName, ArrayList<String> CPUNames) {

		// 3. Remove inline styles so they don't override the CSS file
		centerCards.setStyle("");

		this.playersNum = playersNum;
		this.actionIndex = 0;

		gameplay = new Gameplay(playersNum, playerName, CPUNames);
		nextActionButton.setText("Start Round 1");

//		startRound(10);
	}

	public void nextAction(ActionEvent e) throws IOException {

//		System.out.println("Current State: " + actionIndex);

		switch(actionIndex) {

			case 0: {
				actionIndex = 1;
				increaseRound();

				resetVisualTrumpCardInfo();
				resetVisualTrickInfo();

				gameplay.shuffleCards();
				dealerLabel.setText("Dealer: " + gameplay.getDealer().getName());
				nowPlayingLabel.setText("First Player: " + gameplay.getRoundFirstPlayer().getName());
				nextActionButton.setText("Deal Cards");
				break;
			}

			case 1: {
				actionIndex = 2;
				gameplay.dealCards();
				ArrayList<Card> playerHandCards = gameplay.getPlayerHandCards(false); // do not order //
				showHand(playerHandCards);
				showPlayerInfoTop();
				nextActionButton.setText("Set Trump Card");
				break;
			}

			case 2: {

				actionIndex = 3;
				if (gameplay.getCurrentRound() != gameplay.getMaxRound()) {
          setTrumpCard();
          if (gameplay.getDealer().isUser() && gameplay.getTrumpCard().getCardValue() == "W") {
            // next state is now to choose trump color //
            nextActionButton.setText("Set Trump Color");
            actionIndex = 9;
            break;
          }
        }

				currentPlayer = gameplay.getRoundFirstPlayer();
				nextActionButton.setText("Guess Tricks: " + currentPlayer.getName());
				if (currentPlayer.isUser()) {
					tricksChoiceBox.setVisible(true);
					tricksChoiceBox.getItems().addAll(getAvailableTrickGuesses(gameplay.getCurrentRound()));
				}

				break;
			}

			case 3: {

				// all players guess tricks, starting from the firstPLayer //

				guessTricks(currentPlayer);

				// if a null value was chosen, guess again //
				if (nullValueChosen) {
          break;
        }

				// update current player //
				currentPlayer = currentPlayer.getNextPlayer();
				nextActionButton.setText("Guess Tricks: " + currentPlayer.getName());

				if (currentPlayer == gameplay.getRoundFirstPlayer()) {
					actionIndex = 4;
					gameplay.initNewTrick();
					nextActionButton.setText("Play Cards: " + currentPlayer.getName());
					refreshHand(); // do this to change drag n drop functionality //
				}
				else if (currentPlayer.isUser()) {
					tricksChoiceBox.setVisible(true);
					tricksChoiceBox.getItems().addAll(getAvailableTrickGuesses(gameplay.getCurrentRound()));
				}

				break;

			}

			case 4: {

				currentTrickWinnerLabel.setText("Current Trick Winner: ");

				playCard(currentPlayer);

				if (currentPlayer.isUser() && !userPlayedCard) {
          break;
        }

				gameplay.increaseCardsPlayed();
				System.out.println(currentPlayer.getName() + " played Card ");
				gameplay.updateTrickInfo(currentPlayer);
				currentPlayer = currentPlayer.getNextPlayer();

				// will play as many times as round; keep a counter //
				nextActionButton.setText("Play Cards: " + currentPlayer.getName());

				System.out.println("Cards Played: " + gameplay.getNumCardsPlayed());
				if (gameplay.getNumCardsPlayed() == playersNum) {
					actionIndex = 5;
					nextActionButton.setText("Evaluate Trick");
				}

				refreshHand(); // do this to change drag n drop functionality //


				break;
			}

			case 5: {
				// here we get the trick winner, then we go back to case 4 for the new round //

				currentPlayer = gameplay.getTrickWinner();
				System.out.println("Current Trick Winner is " + currentPlayer.getName());
				currentTrickWinnerLabel.setText("Current Trick Winner: " + currentPlayer.getName());
				gameplay.increaseTricksPlayed();
				currentPlayer.incrementTricksCompleted();
				gameplay.initNewTrick();
				userPlayedCard = false;
				showPlayerInfoTop();

				actionIndex = 6;
				nextActionButton.setText("Go to Next Trick");

				System.out.println("Tricks Played: " + gameplay.getNumTricksPlayed());
				if (gameplay.getNumTricksPlayed() == gameplay.getCurrentRound()) {
					actionIndex = 7;
					nextActionButton.setText("Evaluate Round");
				}

				break;

			}

			case 6: {
//				gameplay.updateScores();
//				gameplay.resetHands();
//				gameplay.resetTable();

				gameplay.resetLeadCard();

				currentTrickWinnerLabel.setText("Current Trick Winner: ");

				showPlayerInfoTop();

				actionIndex = 4;
				refreshHand(); // do this to change drag n drop functionality //

				resetVisualTrickInfo();
				nextActionButton.setText("Play Cards: " + currentPlayer.getName());

				break;
			}

			// evaluate round //
			case 7: {
				gameplay.updateScores();
				gameplay.resetHands();
				gameplay.resetTable();

				currentTrickWinnerLabel.setText("Current Trick Winner: ");

				showPlayerInfoTop();

				if (gameplay.getCurrentRound() == gameplay.getMaxRound()) {
					nextActionButton.setText("Congratulate Winner");
					actionIndex = 8;
				}
				else {
					actionIndex = 0;
					nextActionButton.setText("Start Round " + (gameplay.getCurrentRound() + 1));
				}

				break;
			}

			case 8: {
				congratulateWinner(e);
				break;
			}

			case 9: {
			  showTrumpColorPanel();

			  currentPlayer = gameplay.getRoundFirstPlayer();
        nextActionButton.setText("Guess Tricks: " + currentPlayer.getName());
        if (currentPlayer.isUser()) {
          tricksChoiceBox.setVisible(true);
          tricksChoiceBox.getItems().addAll(getAvailableTrickGuesses(gameplay.getCurrentRound()));
        }

        actionIndex = 3;

        break;
			}

		}

	}

	void increaseRound() {
		gameplay.increaseRound();
		gameplay.incrementFirstPlayer();
		roundLabel.setText("Round " + gameplay.getCurrentRound());
	}

	void dealCards(int numCards) {

	}

	void resetVisualTrickInfo() {
		centerCards.getChildren().clear();

		leadColorLabel.setTextFill(Color.BLACK);
		leadColorLabel.setText("Lead Color");

		showPlayerInfoTop();
	}

	void resetVisualTrumpCardInfo() {
		trumpCardBox.getChildren().clear();

		trumpColorLabel.setTextFill(Color.BLACK);
		trumpColorLabel.setText("Trump Color");

		showPlayerInfoTop();
	}

	void guessTricks(Player player) {
		Integer trickNumber;

		if (player.isUser() == true) {
			trickNumber = guessTricksUser();
			if (trickNumber != null) {
        gameplay.setTricksGuessed(player, trickNumber);
      }
		}
		else {
			trickNumber = guessTricksAI(player);
			// sets tricks guessed automatically //
		}


		showPlayerInfoTop();
	}

	public void showPlayedCards(ActionEvent e) throws IOException {

		ArrayList<Card> playedCards = gameplay.getPlayedCards();
		playedCards.sort(
		        Comparator.comparing(Card::getCardColor)
                .thenComparing(Card::getCardNumber)
				);

		System.out.println("Num of played card is " + playedCards.size());

		FXMLLoader loader = new FXMLLoader(getClass().getResource("playedCards.fxml"));
		Parent root = loader.load();

		PlayedCardsController playedCardsController = loader.getController();
		playedCardsController.addCardsNewWindow(playedCards);

		Stage stage = new Stage(); // opens a completely new window

		Scene scene = new Scene(root);

		stage.setScene(scene);

		stage.show();

	}

	 public void showAssumptions(ActionEvent e) throws IOException {

	    ArrayList<Card> playedCards = gameplay.getPlayedCards();
	    playedCards.sort(
	            Comparator.comparing(Card::getCardColor)
	                .thenComparing(Card::getCardNumber)
	        );

	    System.out.println("Num of played card is " + playedCards.size());

	    FXMLLoader loader = new FXMLLoader(getClass().getResource("assumptions.fxml"));
	    Parent root = loader.load();

	    AssumptionsController assumptionsController = loader.getController();
	    assumptionsController.addAssumptions(gameplay.getAssumptions());

	    Stage stage = new Stage(); // opens a completely new window

	    Scene scene = new Scene(root);

	    stage.setScene(scene);

	    stage.show();

	  }

	public void congratulateWinner(ActionEvent e) throws IOException {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("winner.fxml"));
		Parent root = loader.load();

		WinnerController winnerController = loader.getController();
		winnerController.updateWinner(gameplay.getWinner().getName());

		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();

		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.show();

	}

	void playCard(Player player) {

		if (player.isUser()) {
			// wait until card is dragged in the center //
		}
		else {
			gameplay.selectAndPlayCard(player);
			moveCardToCenter(player.getLastPlayedCard(), player);
		}

		if (player.isUser() && !userPlayedCard) {
      return;
    }

		Color leadColor = cardColorToColor(gameplay.getLeadColor());
		if (leadColor != null) {
			leadColorLabel.setTextFill(leadColor);
		}
	}

	public Integer guessTricksUser() {

		Integer tricksNumber;

		// if there are no choices, add //
		if (tricksChoiceBox.getItems().isEmpty()) {
			tricksChoiceBox.getItems().addAll(getAvailableTrickGuesses(gameplay.getCurrentRound()));
		}

		// if the box is not visible, make it visible //
		if (tricksChoiceBox.isVisible() == false) {
			tricksChoiceBox.setVisible(true);
		}

		tricksNumber = tricksChoiceBox.getValue();

		if (tricksNumber != null) {
			System.out.println("Tricks Number is " + tricksNumber);
			tricksChoiceBox.getItems().clear();
			tricksChoiceBox.setVisible(false);
			nullValueChosen = false;
		}
		else {
			System.out.println("Please select a valid number.");
			nullValueChosen = true;
		}

//		tricksChoiceBox.getItems().clear();

		return tricksNumber;

	}

	public int guessTricksAI(Player player) {
		player.guessTricks(gameplay.getCurrentRound(), playersNum, null);

		System.out.printf("%s guessed %d tricks.\n", player.getName(), player.getTricksGuessed());
		return player.getTricksGuessed();
	}

	public void orderHand() {
		ArrayList<Card> playerHandCards = gameplay.getPlayerHandCards(true); // order

		showHand(playerHandCards);
	}

	public void refreshHand() {
		ArrayList<Card> playerHandCards = gameplay.getPlayerHandCards(false); // do not order

		showHand(playerHandCards);
	}

    public void dragCardOverCenter(DragEvent e) {

    	// make it available to move //

        if (e.getDragboard().hasString()) {
            e.acceptTransferModes(TransferMode.MOVE);
        }
    }

    public void dropCardAtCenter(DragEvent e) {

		moveCardToCenter(draggedCard, gameplay.getUser());
		e.setDropCompleted(true);

		draggedCard = null;

		userPlayedCard = true;

		refreshHand();

    }

	public ImageView getImageViewFromCard(Card card) {

		String cardImagePath;
		InputStream stream;
		Image cardImage;

		cardImagePath = card.getImagePath();
		stream = getClass().getResourceAsStream(cardImagePath);

//		System.out.println("Card Path is " + cardImagePath);
//
//		System.out.println("Showing Card " + card.getCardColor() + " " + card.getCardValue());

		cardImage = new Image(stream);
		ImageView cardImageView = new ImageView(cardImage);

		return cardImageView;
	}

	public void showHand(ArrayList<Card> playerHandCards) {

		String cardImagePath;
		InputStream stream;
		Image cardImage;

		player1Box.getChildren().clear(); // empty the hbox //

		boolean playableCardExists = gameplay.sameColorCardExists(playerHandCards);

		for (Card card : playerHandCards) {

			ImageView cardImageView = getImageViewFromCard(card);

			cardImageView.setFitHeight(50);
      cardImageView.setPreserveRatio(true);

//      // if card is not playable, grey it out //
//      // keep the code to insert a button for it //
//      if (invalidCardsShown && card.isCardPlayable(gameplay.getLeadColor(), playableCardExists)) {
//        ColorAdjust colorAdjustGrayscale = new ColorAdjust();
//        colorAdjustGrayscale.setSaturation(-1);
//        cardImageView.setEffect(colorAdjustGrayscale);
//      }

      // lambda function to enlarge card when hovering over it //
      cardImageView.setOnMouseEntered(event -> {
//	        	cardImageView.setFitHeight(70); // do not use, it changes the whole hbox //
      	cardImageView.setScaleX(1.5);
      	cardImageView.setScaleY(1.5);
      });

      cardImageView.setOnMouseExited(event -> {
//	        	cardImageView.setFitHeight(50); // do not use, it changes the whole hbox //
//	        	cardImageView.setFitHeight(70); // do not use, it changes the whole hbox //
      	cardImageView.setScaleX(1);
      	cardImageView.setScaleY(1);
      });

      // can add the following statement to the below condition //
      if (card.isCardPlayable(gameplay.getLeadColor(), playableCardExists) == false) {
        player1Box.getChildren().add(cardImageView);
        continue;
      }

      if (currentPlayer == gameplay.getUser() && (actionIndex == 4) && (userPlayedCard == false)) {
        cardImageView.setOnDragDetected(event -> {

        	Dragboard db = cardImageView.startDragAndDrop(TransferMode.MOVE); // or none? //
        	ClipboardContent content = new ClipboardContent();
        	content.putString("Moving Card");
        	db.setContent(content);

        	db.setDragView(cardImageView.snapshot(null, null));

        	setDraggedCard(card);

        	event.consume();

        });
      }



			player1Box.getChildren().add(cardImageView);

		}
	}

	public void setDraggedCard(Card card) {
		draggedCard = card;
	}

	public Card getDraggedCard(Card card) {
		return draggedCard;
	}

	public void moveCardToCenter(Card card, Player player) {

		VBox cardWrapper = new VBox();
		cardWrapper.setAlignment(Pos.CENTER);
		cardWrapper.setSpacing(5);

		gameplay.playCard(player, card);
		ImageView cardImageView = getImageViewFromCard(card);

		System.out.println("Card " + card.getCardValue() + " moved to Center");

		cardImageView.setFitHeight(80);
        cardImageView.setPreserveRatio(true);

        Label playerNameLabel = new Label(player.getName());
        playerNameLabel.setTextFill(Color.WHITE);

        cardWrapper.getChildren().addAll(cardImageView, playerNameLabel);

		centerCards.getChildren().add(cardWrapper);
		if (player.isUser()) {
      refreshHand();
    }
	}

	public void setTrumpCard() {

		gameplay.setTrumpCard();

		Card trumpCard = gameplay.getTrumpCard();
		ImageView cardImageView = getImageViewFromCard(trumpCard);

		cardImageView.setFitHeight(80);
        cardImageView.setPreserveRatio(true);

		trumpCardBox.getChildren().add(cardImageView);

		// set the color //
		setTrumpColor(trumpCard);

	}

	public void setTrumpColor(Card trumpCard) {

		CardColor cardColor = trumpCard.getCardColor();

		// convert enum cardColor to JavaFX Color //
		Color color = cardColorToColor(cardColor);

		// if color is not colorless, set it and return //
		if (color != null) {
			trumpColorLabel.setTextFill(color);
			return;
		}

		if (trumpCard.getCardValue() == "W") {
			trumpColorLabel.setText("Trump Color\n(Chosen by " + gameplay.getDealer().getName() + ")");
			trumpColorLabel.setTextAlignment(TextAlignment.CENTER);

			color = chooseTrumpColor();
			cardColor = colorToCardColor(color);

	     if (color != null) {
        trumpColorLabel.setTextFill(color);
       }
		}
		else if (trumpCard.getCardValue() == "J") {
			// if card is J, there is no trump color //
//      trumpColorLabel.setText("Trump Color: None");
//      trumpColorLabel.setTextAlignment(TextAlignment.CENTER);
			trumpColorLabel.setText("No Trump Color");
		}

		if (cardColor != null) {
		  System.out.println("Trump Color chosen: " + cardColor.getColor());
		}
		else {
		  System.out.println("Trump Color not yet chosen!");
		}

		gameplay.setTrumpColor(cardColor);

	}

	public Color chooseTrumpColor() {

		if (gameplay.getDealer().isUser()) {
			// for now just return red //
//			return Color.RED;
//			return cardColorToColor()

			return null; // will choose at the next step //
		}
		else {
			return cardColorToColor(gameplay.AIChooseTrumpColor());
		}

	}

	public void showTrumpColorPanel() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("chooseTrumpColor.fxml"));
    Parent root = loader.load();

    TrumpColorController trumpColorController = loader.getController();
//    playedCardsController.addCardsNewWindow(playedCards);

    Stage stage = new Stage(); // opens a completely new window

    Scene scene = new Scene(root);

    stage.setScene(scene);

    stage.showAndWait();

    CardColor trumpColor = trumpColorController.getTrumpColor();

    System.out.println("Trump Color set to " + trumpColor.getColor());

    trumpColorLabel.setText("Trump Color\n(Chosen by " + gameplay.getDealer().getName() + ")");
    trumpColorLabel.setTextAlignment(TextAlignment.CENTER);
    trumpColorLabel.setTextFill(cardColorToColor(trumpColor));

    gameplay.setTrumpColor(trumpColor);
	}

	public Color cardColorToColor(CardColor color) {
		switch(color) {
			case CardColor.Red: return Color.RED;
			case CardColor.Yellow: return Color.YELLOW;
			case CardColor.Blue: return Color.BLUE;
			case CardColor.Green: return Color.GREEN;
			default: return null;
		}
	}

	 public CardColor colorToCardColor(Color color) {

	   if (color == Color.RED) {
      return CardColor.Red;
     } else if (color == Color.YELLOW) {
      return CardColor.Yellow;
     } else if (color == Color.BLUE) {
      return CardColor.Blue;
     } else if (color == Color.GREEN) {
      return CardColor.Green;
     }
     else {
       return null;
     }

	  }


	public ArrayList<Integer> getAvailableTrickGuesses(int cardNum) {

		ArrayList<Integer> availableTrickGuesses = new ArrayList<>(cardNum+1);

		for (int i = 0; i <= cardNum; i++) {
			availableTrickGuesses.add(i);
		}

		return availableTrickGuesses;
	}

	public void showPlayerInfoTop() {

		topPlayerInfoBox.getChildren().clear();

//		ArrayList<Label> playerInfoTop = new ArrayList<>;
		Player[] players = gameplay.getPlayers();

//		VBox[] playerInfoVBoxes = new VBox[playersNum];
		// we want to show: Name, Current Guesses, Current Score //

		for (int i = 0; i < playersNum; i++) {
			VBox playerInfoVBox = new VBox();
			playerInfoVBox.getStyleClass().add("playerInfoStyle");
			playerInfoVBox.setMaxWidth(200); // arbitrary //

//			centerCards.setHgrow(playerInfoVBox, Priority.ALWAYS);

			Label nameLabel = new Label(players[i].getName());
			Label scoreLabel = new Label("Score: " + players[i].getScore() + " | " + players[i].getRoundScore(gameplay.getCurrentRound() - 1));
			Label tricksGuessedLabel = new Label("Guessed:");
			Label tricksGuessedLabel2 = new Label(players[i].getTricksGuessed() + " Tricks");
			Label tricksCompletedLabel = new Label("Completed:");
			Label tricksCompletedLabel2 = new Label(players[i].getTricksCompleted() + " Tricks");
			playerInfoVBox.getChildren().addAll(nameLabel, scoreLabel, tricksGuessedLabel, tricksGuessedLabel2, tricksCompletedLabel, tricksCompletedLabel2);
			topPlayerInfoBox.getChildren().add(playerInfoVBox);
		}


	}


}
