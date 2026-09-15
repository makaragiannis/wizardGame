package wizardGame;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import wizardGame.gameLogic.card.Card;

public class PlayedCardsController {
	
	@FXML
	private HBox playedCardsBox;
	
	void addCardsNewWindow(ArrayList<Card> playedCards) {
		
		playedCardsBox.getChildren().clear();
		
		for (Card card : playedCards) {
		
			ImageView cardImageView = getImageViewFromCard(card);
				
			cardImageView.setFitHeight(80);
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
