package wizardGame.gameLogic.card;

public enum CardColor {
	
	Red("Red"),
	Yellow("Yellow"),
	Blue("Blue"),
	Green("Green"),
	Colorless("Colorless");
	
	private final String color;
	
	CardColor(String color) {
		
		this.color = color;
	}
	
	public String getColor() {
		return this.color;
	}
}
