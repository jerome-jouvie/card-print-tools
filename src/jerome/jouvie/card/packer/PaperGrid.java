/**
 * card-print-tools - https://github.com/jerome-jouvie/card-print-tools
 * Copyright @ 2018 Jérôme Jouvie (jerome.jouvie@gmail.com)
 */
package jerome.jouvie.card.packer;

import com.bonzaiengine.math.Size;

class PaperGrid {

	public final Size cellSize;
	
	public final int numCardWidth;
	public final int numCardHeight;

	public final int offsetWidth;
	public final int offsetHeight;
	
	public PaperGrid(Size cellSize, int numCardWidth, int numCardHeight, int offsetWidth, int offsetHeight) {
		this.cellSize = cellSize;
		this.numCardWidth = numCardWidth;
		this.numCardHeight = numCardHeight;
		this.offsetWidth = offsetWidth;
		this.offsetHeight = offsetHeight;
	}

}
