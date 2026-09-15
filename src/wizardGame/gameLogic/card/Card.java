package wizardGame.gameLogic.card;

public class Card {

	CardColor color;
	Value value;
	
	public Card(CardColor color, Value value) {
		this.color = color;
		this.value = value;
	}
	
	public boolean isCardW() {
		return (value.getValue() == "W");
	}
	
	public boolean isCardJ() {
		return (value.getValue() == "J");
	}

	
	public int getCardNumber() {
		
		return value.getNumber();
	}
	
	public String getColorString() {
		
		return color.getColor();
	}
	
	public CardColor getCardColor() {
		
		return color;
	}
	
	public String getCardValue() {
		
		return value.getValue();
	}
	
	public void printCard() {
		if (getCardNumber() == -1) {
			System.out.println(getCardValue());
		}
		else {
			System.out.println(getCardColor() + " " + getCardValue());
		}
	}
	
	public String getImagePath() {
		
		if (value == Value.W) return "Cards/w.png";
		if (value == Value.J) return "Cards/z.png";
		return "Cards/" + color.getColor() + "/" + color.getColor().toLowerCase() + value.getValue() + ".png";
	}
	
	public int getCardScore(CardColor trumpColor, CardColor leadColor) {
		
		// Scores start from 0 and end at 27 //
		// 0 corresponds to J //
		// 1 to 13 correspond to the value of cards of the lead color //
		// 14 to 26 corresponds to the value of cards of the trump color (13 + their value) //
		// 27 corresponds to W //
		// anything else corresponds to -1 //
		// the winning card is the card with the highest score //
		
		if (value == Value.J) {
			return 0;
		}
		
		if (value == Value.W) {
			return 27;
		}
		
		if (color == leadColor) {
			return value.getNumber(); // (1 - 13) //
		}
		
		if (color == trumpColor) {
			return 13 + value.getNumber(); // (14 - 26) //
		}
		
		return -1;
			
			
	}
	
}
