/**
 * card-print-tools - https://github.com/jerome-jouvie/card-print-tools
 * Copyright @ 2018 Jérôme Jouvie (jerome.jouvie@gmail.com)
 */
package jerome.jouvie.card.packer;

import jerome.jouvie.card.DPI;

enum PaperType {
	A4,
	A3,
	LetterSize;
	
	public int getWidth(DPI dpi) {
		if (this == A4) {
			switch (dpi) {
				case DPI_72: return 595;
				case DPI_96: return 794;
				case DPI_150: return 1240;
				case DPI_300: return 2480;
				case DPI_600: return 4961;
			}
		} else if (this == A3) {
			switch (dpi) {
				case DPI_72: return 842;
				case DPI_96: return 1123;
				case DPI_150: return 1754;
				case DPI_300: return 3508;
				case DPI_600: return 7016;
			}
		} else if (this == LetterSize) {
			switch (dpi) {
				case DPI_72: return 612;
				case DPI_96: return 816;
				case DPI_150: return 1275;
				case DPI_300: return 2550;
				case DPI_600: return 5100;
			}
		}
		throw new IllegalStateException();
	}
	
	public int getHeight(DPI dpi) {
		if (this == A4) {
			switch (dpi) {
				case DPI_72: return 842;
				case DPI_96: return 1123;
				case DPI_150: return 1754;
				case DPI_300: return 3508;
				case DPI_600: return 7016;
			}
		} else if (this == A3) {
			switch (dpi) {
				case DPI_72: return 1191;
				case DPI_96: return 1587;
				case DPI_150: return 2480;
				case DPI_300: return 4960;
				case DPI_600: return 9933;
			}
		} else if (this == LetterSize) {
			switch (dpi) {
				case DPI_72: return 792;
				case DPI_96: return 1056;
				case DPI_150: return 1650;
				case DPI_300: return 3300;
				case DPI_600: return 6600;
			}
		}
		throw new IllegalStateException();
	}
}
