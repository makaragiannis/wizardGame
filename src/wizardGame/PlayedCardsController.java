package wizardGame;

import java.io.InputStream;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import wizardGame.gameLogic.card.Card;

public class PlayedCardsController {

	@FXML
	private HBox playedCardsBox;

	void addCardsNewWindow(ArrayList<Card> playedCards) {

		playedCardsBox.getChildren().clear();

		for (Card card : playedCards) {

			ImageView cardImageView = getImageViewFromCard(card);

			cardImageView.setFitHeight(120);
	        cardImageView.setPreserveRatio(true);

	        playedCardsBox.getChildren().add(cardImageView);

		}

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
}
