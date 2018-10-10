/**
 * card-print-tools - https://github.com/jerome-jouvie/card-print-tools
 * Copyright @ 2018 Jérôme Jouvie (jerome.jouvie@gmail.com)
 */
package jerome.jouvie.card.packer;

import com.bonzaiengine.math.Area;

class CardArea {
	public final Area front;
	public final Area back;
	
	public CardArea(Area front, Area back) {
		this.front = front;
		this.back = back;
	}
}
