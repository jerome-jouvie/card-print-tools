/**
 * card-print-tools - https://github.com/jerome-jouvie/card-print-tools
 * Copyright @ 2018 Jérôme Jouvie (jerome.jouvie@gmail.com)
 */
package jerome.jouvie.card.tool;

import java.io.File;
import java.io.IOException;

import jerome.jouvie.card.DPI;

/**
 * Uses ImageMagick from https://www.imagemagick.org
 */
public class PdfSupport {
	
	public static void imagesToPdf(DPI dpi, File dir, String srcExt, String pdfFileName) throws IOException {
		Runtime.getRuntime().exec(new String[] {
				"convert", "-density", "" + dpi.dpi(), "*." + srcExt, pdfFileName
			}, new String[]{}, dir);
	}
}
