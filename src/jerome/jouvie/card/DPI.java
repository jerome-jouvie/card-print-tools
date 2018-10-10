/**
 * card-print-tools - https://github.com/jerome-jouvie/card-print-tools
 * Copyright @ 2018 Jérôme Jouvie (jerome.jouvie@gmail.com)
 */
package jerome.jouvie.card;

public enum DPI {
	DPI_72(72),
	DPI_96(96),
	DPI_150(150),
	DPI_300(300),
	DPI_600(600);
	
	private static final float MILLI_METER_TO_INCHES = 0.0393701f;
	
	private int dpi;
	
	private DPI(int dpi) {
		this.dpi = dpi;
	}
	
	public int dpi() {
		return dpi;
	}
	
	public int convertMilliMeterToPixels(float centimeter) {
		float inches = centimeter * MILLI_METER_TO_INCHES;
		return convertInchToPixels(inches);
	}
	
	public int convertInchToPixels(float inches) {
		return (int) (inches * dpi);
	}
	
	public static DPI of(int dpi) {
		for (DPI e : values()) {
			if (e.dpi == dpi) {
				return e;
			}
		}
		throw new IllegalArgumentException("Unknown DPI: " + dpi);
	}
}
