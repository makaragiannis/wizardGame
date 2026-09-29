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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
	private BorderPane myBorderPane;

	@FXML
	private Label lastActionLabel, lastTrickWinnerLabel, roundLabel, trumpColorLabel, leadColorLabel, dealerLabel, firstPlayerLabel, currentTrickWinnerLabel;

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

	  // calculate spacing when window is resized //
	  player1Box.widthProperty().addListener((obs, oldVal, newVal) -> {

	    calculateCardSpacing();
	  });

	   myBorderPane.heightProperty().addListener((obs, oldVal, newVal) -> {

	      System.out.println("Old - New:" + oldVal.doubleValue() + " " + newVal.doubleValue() );
	    });

   topPlayerInfoBox.setHgrow(topPlayerInfoBox, Priority.ALWAYS);

   currentTrickWinnerLabel.setText(null);
   lastTrickWinnerLabel.setText(null);

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

        lastActionLabel.setText("Round " + gameplay.getCurrentRound() + " starts!");

				resetVisualTrumpCardInfo();
				resetVisualTrickInfo();

        showPlayerInfoTop(null, 0);

				gameplay.shuffleCards();
				dealerLabel.setText("Dealer: " + gameplay.getDealer().getName());
				firstPlayerLabel.setText("First Player: " + gameplay.getRoundFirstPlayer().getName());
				nextActionButton.setText("Deal Cards");
				break;
			}

			case 1: {
				actionIndex = 2;
				gameplay.dealCards();

				lastActionLabel.setText("Everyone was dealt " + gameplay.getCurrentRound() + " cards!");

				refreshHand();

				nextActionButton.setText("Set Trump Card");
				break;
			}

			case 2: {

				actionIndex = 4;
				if (gameplay.getCurrentRound() != gameplay.getMaxRound()) {
          setTrumpCard();
          lastActionLabel.setText("Trump Card is set!");
          if (gameplay.getDealer().isUser() && gameplay.getTrumpCard().getCardValue() == "W") {
            // next state is now to choose trump color //
            nextActionButton.setText("Set Trump Color");
            actionIndex = 3;
            break;
          }
        }
				else {

				  lastActionLabel.setText("This is the last round, so no Trump Card is set!");
				  // set trump color to be NULL if this is the last round //
				  setTrumpColor(CardColor.Colorless);
				}

				currentPlayer = gameplay.getRoundFirstPlayer();
				nextActionButton.setText("Guess Tricks: " + currentPlayer.getName());
				currentTrickWinnerLabel.setText(null);
				if (currentPlayer.isUser()) {
					tricksChoiceBox.setVisible(true);
					tricksChoiceBox.getItems().addAll(getAvailableTrickGuesses(gameplay.getCurrentRound()));
				}

				break;
			}

	     case 3: {
	        showTrumpColorPanel();

	        lastActionLabel.setText("Trump Color was chosen: " + gameplay.getTrumpColor().getColor());

	        currentPlayer = gameplay.getRoundFirstPlayer();
	        nextActionButton.setText("Guess Tricks: " + currentPlayer.getName());
	        if (currentPlayer.isUser()) {
	          tricksChoiceBox.setVisible(true);
	          tricksChoiceBox.getItems().addAll(getAvailableTrickGuesses(gameplay.getCurrentRound()));
	        }

	        actionIndex = 4;

	        break;
	      }

			case 4: {

				// all players guess tricks, starting from the firstPLayer //

				guessTricks(currentPlayer);
        showPlayerInfoTop(null, 0);

				// if a null value was chosen, guess again //
				if (nullValueChosen) {
	        lastActionLabel.setText("Choose a valid guess!");
          break;
        }

        lastActionLabel.setText("Player " + currentPlayer.getName() + " guessed " + currentPlayer.getTricksGuessed() + " tricks!");

        showPlayerInfoTop(currentPlayer, 2);

				// update current player //
				currentPlayer = currentPlayer.getNextPlayer();
				nextActionButton.setText("Guess Tricks: " + currentPlayer.getName());

				if (currentPlayer == gameplay.getRoundFirstPlayer()) {
					actionIndex = 5;
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

			case 5: {

			  showPlayerInfoTop(null, 0);

				playCard(currentPlayer);


				if (currentPlayer.isUser() && !userPlayedCard) {
	        lastActionLabel.setText("Play a valid card by dragging it to the center!");

          break;
        }

				lastActionLabel.setText("Player " + currentPlayer.getName() + " played a card!");

				gameplay.increaseCardsPlayed();
				gameplay.updateTrickInfo(currentPlayer);
        currentTrickWinnerLabel.setText("Trick Winner (Currently): " + gameplay.getTrickWinner().getName());

				currentPlayer = currentPlayer.getNextPlayer();

				// will play as many times as round; keep a counter //
				nextActionButton.setText("Play Cards: " + currentPlayer.getName());

				System.out.println("Cards Played: " + gameplay.getNumCardsPlayed());
				if (gameplay.getNumCardsPlayed() == playersNum) {
					actionIndex = 6;
					nextActionButton.setText("Evaluate Trick");
				}

				refreshHand(); // do this to change drag n drop functionality //


				break;
			}

			case 6: {
				// here we get the trick winner, then we go back to case 4 for the new round //

				currentPlayer = gameplay.getTrickWinner();
				lastActionLabel.setText("Current Trick Winner is " + currentPlayer.getName() + "!");
				currentTrickWinnerLabel.setText("Trick Winner: " + currentPlayer.getName());
				gameplay.increaseTricksPlayed();
				currentPlayer.incrementTricksCompleted();
				gameplay.initNewTrick();
				userPlayedCard = false;
				showPlayerInfoTop(currentPlayer, 3);

				actionIndex = 7;
				nextActionButton.setText("Go to Next Trick");

				System.out.println("Tricks Played: " + gameplay.getNumTricksPlayed());
				if (gameplay.getNumTricksPlayed() == gameplay.getCurrentRound()) {
					actionIndex = 8;
					nextActionButton.setText("Evaluate Round");
				}

				break;

			}

			case 7: {
//				gameplay.updateScores();
//				gameplay.resetHands();
//				gameplay.resetTable();

        lastActionLabel.setText("Starting new trick!");
        System.out.println("Currenrt Player: " + currentPlayer.getName());
        lastTrickWinnerLabel.setText("Last Trick Winner: " + currentPlayer.getName());

				gameplay.resetLeadCard();

//				currentTrickWinnerLabel.setText("Current Trick Winner: ");

				showPlayerInfoTop(null, 0);

				actionIndex = 5;
				refreshHand(); // do this to change drag n drop functionality //

				resetVisualTrickInfo();
				nextActionButton.setText("Play Cards: " + currentPlayer.getName());

				break;
			}

			// evaluate round //
			case 8: {
			  lastActionLabel.setText("Scores are updated!");
        lastTrickWinnerLabel.setText(null);
        currentTrickWinnerLabel.setText(null);

				gameplay.updateScores();
				gameplay.resetHands();
				gameplay.resetTable();

				// show player info with bolded scores //
				showPlayerInfoTop(null, 1);

				if (gameplay.getCurrentRound() == gameplay.getMaxRound()) {
					nextActionButton.setText("Congratulate Winner");
					actionIndex = 9;
					lastActionLabel.setText("Congratulare the Winner!");
				}
				else {
					actionIndex = 0;
					nextActionButton.setText("Start Round " + (gameplay.getCurrentRound() + 1));
				}

				break;
			}

			case 9: {

				congratulateWinner(e);
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
		centerCards.setStyle("-fx-border-color: #000000;");

		leadColorLabel.setTextFill(Color.BLACK);
		leadColorLabel.setText("Lead Color");

//		showPlayerInfoTop(null, 0);
	}

	void resetVisualTrumpCardInfo() {
		trumpCardBox.getChildren().clear();
		trumpCardBox.setStyle("-fx-border-color: #000000;");

		trumpColorLabel.setTextFill(Color.BLACK);
		trumpColorLabel.setText("Trump Color");

//		showPlayerInfoTop(null, 0);
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


//		showPlayerInfoTop();
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
//		if (leadColor != null) {
			applyLeadColor(leadColor);
//		}
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
		calculateCardSpacing();
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

			cardImageView.setFitHeight(80);
      cardImageView.setPreserveRatio(true);
      cardImageView.setViewOrder(1);

      // lambda function to enlarge card when hovering over it //
      cardImageView.setOnMouseEntered(event -> {
//	        	cardImageView.setFitHeight(70); // do not use, it changes the whole hbox //
      	cardImageView.setScaleX(1.5);
      	cardImageView.setScaleY(1.5);
      	cardImageView.setViewOrder(0);
      });

      cardImageView.setOnMouseExited(event -> {
//	        	cardImageView.setFitHeight(50); // do not use, it changes the whole hbox //
//	        	cardImageView.setFitHeight(70); // do not use, it changes the whole hbox //
      	cardImageView.setScaleX(1);
      	cardImageView.setScaleY(1);
      	cardImageView.setViewOrder(1);
      });

      // can add the following statement to the below condition //
      if (card.isCardPlayable(gameplay.getLeadColor(), playableCardExists) == false) {
        player1Box.getChildren().add(cardImageView);
        continue;
      }

      if (currentPlayer == gameplay.getUser() && (actionIndex == 5) && (userPlayedCard == false)) {
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

	void calculateCardSpacing() {

	  int cardCount = player1Box.getChildren().size();

	  if (cardCount < 2) {
	    player1Box.setSpacing(10);
	    return;
	  }

	  double availableWidth = myBorderPane.getWidth() - 40; // subtract a little from the sides //

	  Node firstCard = player1Box.getChildren().get(0);

	  double cardWidth = firstCard.getLayoutBounds().getWidth();

	  double totalWidthOfCards = cardWidth * cardCount;

	  double requiredSpacing = (availableWidth - totalWidthOfCards) / (cardCount - 1);

	  double finalSpacing = Math.min(requiredSpacing, 10);

	  player1Box.setSpacing(finalSpacing);
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

        cardWrapper.setMaxWidth(cardImageView.getLayoutBounds().getWidth() + 10);
        cardWrapper.setMinWidth(cardImageView.getLayoutBounds().getWidth() + 10);


		centerCards.getChildren().add(cardWrapper);
		if (player.isUser()) {
      refreshHand();
    }
	}

	public void setTrumpCard() {

		gameplay.setTrumpCard();

		Card trumpCard = gameplay.getTrumpCard();
		ImageView cardImageView = getImageViewFromCard(trumpCard);

		cardImageView.setFitHeight(100);
        cardImageView.setPreserveRatio(true);

		trumpCardBox.getChildren().add(cardImageView);

		// set the color //
		setTrumpColor(trumpCard);

	}

	public void setTrumpColor(CardColor color) {
	  gameplay.setTrumpColor(color);
	}

	public void setTrumpColor(Card trumpCard) {

		CardColor cardColor = trumpCard.getCardColor();

		// convert enum cardColor to JavaFX Color //
		Color color = cardColorToColor(cardColor);

		// if color is not colorless, set it and return //
		if (color != null) {
			applyTrumpColor(color, null);
			return;
		}

		if (trumpCard.getCardValue() == "W") {
			trumpColorLabel.setText("Trump Color\n(Chosen by " + gameplay.getDealer().getName() + ")");
			trumpColorLabel.setTextAlignment(TextAlignment.CENTER);

			color = chooseTrumpColor();
			cardColor = colorToCardColor(color);

	     if (color != null) {
        applyTrumpColor(color, gameplay.getDealer());

       }
		}
		else if (trumpCard.getCardValue() == "J") {
			// if card is J, there is no trump color //
//      trumpColorLabel.setText("Trump Color: None");
//      trumpColorLabel.setTextAlignment(TextAlignment.CENTER);
//			trumpColorLabel.setText("No Trump Color");
		  applyTrumpColor(null, null);
		}

		if (cardColor != null) {
		  System.out.println("Trump Color chosen: " + cardColor.getColor());
		}
		else {
		  System.out.println("Trump Color not yet chosen!");
		}

		gameplay.setTrumpColor(cardColor);

	}

	public void applyTrumpColor(Color trumpColor, Player dealer) {
//	  trumpColorLabel.setTextFill(trumpColor);


	  // paint the box //
    if (trumpColor == Color.RED) {
      trumpCardBox.setStyle("-fx-border-color: #FF0000;");
      trumpColorLabel.setText("Trump Color: Red");
     }
    else if (trumpColor == Color.YELLOW) {
       trumpCardBox.setStyle("-fx-border-color: #FFFF00;");
       trumpColorLabel.setText("Trump Color: Yellow");
     }
    else if (trumpColor == Color.BLUE) {
       trumpCardBox.setStyle("-fx-border-color: #0000FF;");
       trumpColorLabel.setText("Trump Color: Blue");
     }
    else if (trumpColor == Color.GREEN) {
       trumpCardBox.setStyle("-fx-border-color: #00FF00;");
       trumpColorLabel.setText("Trump Color: Green");
     }
   else { // color is null //
     trumpCardBox.setStyle("-fx-border-color: #000000;");
     trumpColorLabel.setText("No Trump Color");
   }

    if (dealer != null) {
      trumpColorLabel.setText(trumpColorLabel.getText() + " (Chosen by Dealer)");
    }


	}

	 public void applyLeadColor(Color leadColor) {
//	    leadColorLabel.setTextFill(leadColor);

	    // paint the box //
	    if (leadColor == Color.RED) {
	      centerCards.setStyle("-fx-border-color: #FF0000;");
	      leadColorLabel.setText("Lead Color: Red");
	     }
	    else if (leadColor == Color.YELLOW) {
	       centerCards.setStyle("-fx-border-color: #FFFF00;");
	       leadColorLabel.setText("Lead Color: Yellow");
	     }
	    else if (leadColor == Color.BLUE) {
	       centerCards.setStyle("-fx-border-color: #0000FF;");
	       leadColorLabel.setText("Lead Color: Blue");
	     }
	    else if (leadColor == Color.GREEN) {
	       centerCards.setStyle("-fx-border-color: #00FF00;");
	       leadColorLabel.setText("Lead Color: Green");
	     }
	   else {
	     centerCards.setStyle("-fx-border-color: #000000;");
	     leadColorLabel.setText("No Lead Color");
	   }


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

//    trumpColorLabel.setText("Trump Color\n(Chosen by " + gameplay.getDealer().getName() + ")");
    trumpColorLabel.setTextAlignment(TextAlignment.CENTER);
    applyTrumpColor(cardColorToColor(trumpColor), gameplay.getDealer());

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

	public void showPlayerInfoTop(Player boldPlayer, int boldMode) {

		topPlayerInfoBox.getChildren().clear();

//		ArrayList<Label> playerInfoTop = new ArrayList<>;
		Player[] players = gameplay.getPlayers();

//		VBox[] playerInfoVBoxes = new VBox[playersNum];
		// we want to show: Name, Current Guesses, Current Score //

		for (int i = 0; i < playersNum; i++) {
			VBox playerInfoVBox = new VBox();
			playerInfoVBox.getStyleClass().add("playerInfoStyle");
//			playerInfoVBox.setMinWidth(myBorderPane.getWidth() / playersNum); // arbitrary //
			playerInfoVBox.setMaxWidth(250);

			topPlayerInfoBox.setHgrow(playerInfoVBox, Priority.ALWAYS);

			playerInfoVBox.setSpacing(5);

			Label nameLabel = new Label(players[i].getName());
			Label roundScoreLabel = new Label("Round Score: " + players[i].getRoundScore(gameplay.getCurrentRound() - 1));
			Label totalScoreLabel = new Label("Total Score: " + players[i].getScore());
			Label tricksGuessedLabel = new Label("Tricks Guessed: " + players[i].getTricksGuessed());
      Label tricksCompletedLabel = new Label("Tricks Completed: " + players[i].getTricksCompleted());

      if (boldMode == 1) {
        roundScoreLabel.setFont(Font.font(roundScoreLabel.getFont().getFamily(), FontWeight.BOLD, roundScoreLabel.getFont().getSize()));
        totalScoreLabel.setFont(Font.font(totalScoreLabel.getFont().getFamily(), FontWeight.BOLD, totalScoreLabel.getFont().getSize()));
      }

      if (players[i] == boldPlayer) {
        if (boldMode == 2) {
          tricksGuessedLabel.setFont(Font.font(tricksGuessedLabel.getFont().getFamily(), FontWeight.BOLD, tricksGuessedLabel.getFont().getSize()));
        }
        else if (boldMode == 3) {
          tricksCompletedLabel.setFont(Font.font(tricksCompletedLabel.getFont().getFamily(), FontWeight.BOLD, tricksCompletedLabel.getFont().getSize()));
        }
      }

			playerInfoVBox.getChildren().addAll(nameLabel, roundScoreLabel, totalScoreLabel, tricksGuessedLabel, tricksCompletedLabel);
			topPlayerInfoBox.getChildren().add(playerInfoVBox);
		}


	}


}
