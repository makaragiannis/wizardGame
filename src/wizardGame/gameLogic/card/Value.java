package wizardGame.gameLogic.card;

public enum Value {
	
	W("W", -1),
	J("J", -1),
	N1("1", 1),
	N2("2", 2),
	N3("3", 3),
	N4("4", 4),
	N5("5", 5),
	N6("6", 6),
	N7("7", 7),
	N8("8", 8),
	N9("9", 9),
	N10("10", 10),
	N11("11", 11),
	N12("12", 12),
	N13("13", 13);
	
	private final String value;
	private final int number;
	
	Value(String value, int number) {
		this.value = value;
		this.number = number;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public int getNumber() {
		return this.number;
	}

}
