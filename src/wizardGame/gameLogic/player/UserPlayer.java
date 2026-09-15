package wizardGame.gameLogic.player;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import wizardGame.gameLogic.card.Card;
import wizardGame.gameLogic.card.CardColor;
import wizardGame.gameLogic.table.Table;

public class UserPlayer extends Player{
	
	public UserPlayer(String name, int maxRound) {
		super(name, maxRound);
	}
	
	// to be rendered obsolete //
	public void guessTricks(int cardsNum, int playerNum, Scanner scanner) {
	    	    
	    int tricksGuessed = 0;
	    boolean validInput = false;
	    
	    while (!validInput) {
	        System.out.print("How many tricks do you guess for this round? ");
	        
	        try {
	            tricksGuessed = scanner.nextInt();

	            if (tricksGuessed >= 0 && tricksGuessed <= cardsNum) {
	                validInput = true;
	            } else {
	                System.out.println("Please guess again. Number must be between 0 and " + cardsNum + ".");
	            }
	            
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid input. Please enter a number.");
	            scanner.nextLine();
 
	        }
	    }
	    
	    scanner.nextLine();
	    
	    setTricksGuessed(tricksGuessed);

	}
	
	public CardColor determineTrumpColor(Scanner scanner) {
	    char colorChosen = ' ';
	    boolean validInput = false;

        System.out.print("Trump Card is W. Choose the trump color: (Type R, B, Y or G) ");

	    while (!validInput) {
	        
            colorChosen = scanner.nextLine().charAt(0);

            if ((colorChosen != 'R') && (colorChosen != 'Y') && (colorChosen != 'B') && (colorChosen != 'G')) {
            	System.out.println("Please type a valid color.");
            }
            else {
                validInput = true;
            }

	    }
	    
//	    scanner.nextLine();	
	    
	    switch (colorChosen) {
	    	case 'R' : return CardColor.Red;
	    	case 'Y' : return CardColor.Yellow;
	    	case 'B' : return CardColor.Blue;
	    	case 'G' : return CardColor.Green;
	    	default: return null;
	    }

	}
	
	public Card selectCardToPlay(Table table, Scanner scanner) {
		
	    int cardIndex = 0;
	    boolean validInput = false;
	    ArrayList<Integer> invalidIndices;
	    
	    int cardsNum = handCards.size();
	    
	    while (!validInput) {
	        System.out.print("Select card to play: ");
	        // TODO: Show valid cards if the player wants to //
	        
	        try {
	            cardIndex = scanner.nextInt();

	            if (cardIndex > 0 && cardIndex <= cardsNum) {
	            	invalidIndices = getInvalidIndices(table.getLeadColor());
        			if(isCardIndexInvalid(cardIndex - 1, invalidIndices) == false) {
        				validInput = true;	
    	            }
        			else {
        				System.out.println("This card cannot be played. Check lead color.");
        			}
	                
	            }
	            else {
	                System.out.println("Please select a valid card index.");
	            }
	            
	        } catch (InputMismatchException e) {
	            System.out.println("Please enter a valid number.");
	            scanner.nextLine();
 
	        }
	    }
	    
	    scanner.nextLine();
	    
	    cardIndex-= 1; // because original card Indices started from 1, not from 0 //
	    
		return handCards.get(cardIndex);
	}
	
	public boolean isUser() {
		return true;
	}

}
