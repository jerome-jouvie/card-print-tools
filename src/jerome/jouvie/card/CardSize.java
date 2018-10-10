/**
 * card-print-tools - https://github.com/jerome-jouvie/card-print-tools
 * Copyright @ 2018 Jérôme Jouvie (jerome.jouvie@gmail.com)
 */
package jerome.jouvie.card;

public enum CardSize {
	MINI(597, 750), // 1.75" x 2.5"
	DOMINO(597, 1050), // 1.75" x 3.5"
	BUSINESS(600, 1050), // 2" x 3.5"
	SQUARE_3_5(1050, 1050), // 3.5" x 3.5"
	POCKER(750, 1050), // 2.5" x 3.5"
	TRUMP(732, 1179), // 2.45" x 3.95"
	TAROT(825, 1425), // 2.75" x 4.75"
	LARGE(1050, 1725), // 3.5" x 5.75"
	
	BRIDGE(675, 1050), // 2.25" x 3.5"
	SQUARE_2(600, 600), // 2" x 2"
	
	SETTLEMENT(1200, 2400);
	
	private final int widthAt300Dpi;
	private final int heightAt300Dpi;
	
	private CardSize(int widthAt300Dpi, int heightAt300Dpi) {
		this.widthAt300Dpi = widthAt300Dpi;
		this.heightAt300Dpi = heightAt300Dpi;
	}
	
	public int getWidthInPixels(DPI dpi) {
		return widthAt300Dpi * dpi.dpi() / 300;
	}
	
	public int getHeightInPixels(DPI dpi) {
		return heightAt300Dpi * dpi.dpi() / 300;
	}
}
