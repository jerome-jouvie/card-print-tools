/**
 * card-print-tools - https://github.com/jerome-jouvie/card-print-tools
 * Copyright @ 2018 Jérôme Jouvie (jerome.jouvie@gmail.com)
 */
package jerome.jouvie.card;

public final class Card {
	public final CardSide front;
	public final CardSide back;
	
	public Card(CardSide front, CardSide back) {
		this.front = front;
		this.back = back;
	}
}
