/**
 * card-print-tools - https://github.com/jerome-jouvie/card-print-tools
 * Copyright @ 2018 Jérôme Jouvie (jerome.jouvie@gmail.com)
 */
package jerome.jouvie.card.packer;

import static com.bonzaiengine.io.ParserUtils.parseInt;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.bonzaiengine.io.log.Log;
import com.bonzaiengine.math.Area;
import com.bonzaiengine.math.Size;
import com.bonzaiengine.texture.ITexture;
import com.bonzaiengine.texture.TextureUtils;

import jerome.jouvie.card.Card;
import jerome.jouvie.card.CardSide;
import jerome.jouvie.card.DPI;
import jerome.jouvie.card.tool.ImageWriter;
import jerome.jouvie.card.tool.CardScanner;
import jerome.jouvie.card.tool.Logger;
import jerome.jouvie.card.tool.PdfSupport;

public class CardPacker implements Runnable {
	private static final String INPUT = "-input=";
	private static final String OUTPUT = "-output=";
	private static final String PAPER = "-paper=";
	private static final String PRINT_MARGIN = "-printMarginInPixels=";
	private static final String CARD = "-card=";
	private static final String CARD_CUT = "-cardCut=";
	private static final String CUT_CARD_FRONT = "-cutCardFront=";
	private static final String CUT_CARD_BACK = "-cutCardBack=";
	private static final String CUT_MARKS_FRONT = "-cutMarksFront=";
	private static final String CUT_MARKS_BACK = "-cutMarksBack=";
	private static final String CUT_MARKS_WIDTH = "-cutMarksWidth=";
	private static final String CUT_MARKS_COLOR = "-cutMarksColor=";
	private static final String DPI_RES = "-dpi=";
	private static final String EDGE_BLEED_SIZE = "-edgeBleedSize=";
	private static final String EDGE_BLEED_COLOR_FRONT = "-edgeBleedColorFront=0x";
	private static final String EDGE_BLEED_COLOR_BACK = "-edgeBleedColorBack=0x";
	private static final String BACK = "-back=";
	private static final String COPIES = "-copies=";

	public static void main(String[] args) {
		Log.println(
				"Card Packer - For non commercial use\n" +
				"\n" +
				"Jérôme Jouvie\n" +
				"jerome.jouvie@gmail.com\n" +
				"https://github.com/jerome-jouvie/card-print-tools\n" +
				"\n" +
				"Note: PDF generation requires ImageMagick which can be instaled with:\n" +
				"Linux: sudo apt-get install imagemagick\n" +
				"Other platform: https://www.imagemagick.org/script/download.php\n" +
				"\n" +
				"Usage: " + CardPacker.class.getSimpleName() + " \\\n" +
				INPUT + "<inputDirOfImageToPack> \\\n" +
				OUTPUT + "<outputDirOfPackerImages> \\\n" +
				PAPER + "<paperType[A4|LetterSize]> \\\n" +
				"["+ PRINT_MARGIN + "<printMarginInPixels>] \\\n" +
				DPI_RES + "<DPI[72|200|300|400|600|1200]> \\\n" +
				CARD + "<widthInPixels>x<heightInPixels> \\\n" +
				CARD_CUT + "<widthInPixels>x<heightInPixels> \\\n" +
				"["+ CUT_CARD_FRONT + "[true|false]] \\\n" +
				"["+ CUT_CARD_BACK + "[true|false]] \\\n" +
				"["+ CUT_MARKS_FRONT + "[true|false]] \\\n" +
				"["+ CUT_MARKS_BACK + "[true|false]] \\\n" +
				"["+ CUT_MARKS_WIDTH + "<cutMarksWidthInPixels>] \\\n" +
				"["+ CUT_MARKS_COLOR + "<cutHtmlColor>] \\\n" +
				"["+ EDGE_BLEED_SIZE + "<bleedSizeInPixels>] \\\n" +
				"["+ EDGE_BLEED_COLOR_FRONT + "<htmlColor>] \\\n" +
				"["+ EDGE_BLEED_COLOR_BACK + "<htmlColor>] \\\n" +
				"["+ BACK + "<namePrefixOfBackImage>] \\\n" +
				"["+ COPIES + "<numberOfCopies>] \\\n");
		
		File src = null;
		File dst = null;
		PaperType paperType = null;
		DPI dpi = null;
		int printMarginInPixels = 0;
		Size cardSize = null;
		Size cardCutSize = null;
		boolean cutMarksFront = true;
		boolean cutMarksBack = true;
		int cutMarksWidth = 2;
		boolean cutCardFront = false;
		boolean cutCardBack = false;
		int edgeBleedSize = 0;
		Integer edgeBleedColorFront = null;
		Integer edgeBleedColorBack = null;
		int cutMarksColor = 0x00000000;
		String back = null;
		int copies = 1;
		
		for (String arg : args) {
			if (arg.startsWith(INPUT)) {
				src = new File(arg.substring(INPUT.length()));
			} else if (arg.startsWith(OUTPUT)) {
				dst = new File(arg.substring(OUTPUT.length()));
			} else if (arg.startsWith(PAPER)) {
				paperType = PaperType.valueOf(arg.substring(PAPER.length()));
			} else if (arg.startsWith(DPI_RES)) {
				dpi = DPI.of(parseInt(arg.substring(DPI_RES.length())));
			} else if (arg.startsWith(PRINT_MARGIN)) {
				printMarginInPixels = parseInt(arg.substring(PRINT_MARGIN.length()));
			} else if (arg.startsWith(CARD)) {
				cardSize = argToSize(arg.substring(CARD.length()));
			} else if (arg.startsWith(CARD_CUT)) {
				cardCutSize = argToSize(arg.substring(CARD_CUT.length()));
			} else if (arg.startsWith(CUT_CARD_FRONT)) {
				cutCardFront = Boolean.parseBoolean(arg.substring(CUT_CARD_FRONT.length()));
			} else if (arg.startsWith(CUT_CARD_BACK)) {
				cutCardBack = Boolean.parseBoolean(arg.substring(CUT_CARD_BACK.length()));
			} else if (arg.startsWith(CUT_MARKS_FRONT)) {
				cutMarksFront = Boolean.parseBoolean(arg.substring(CUT_MARKS_FRONT.length()));
			} else if (arg.startsWith(CUT_MARKS_FRONT)) {
				cutMarksFront = Boolean.parseBoolean(arg.substring(CUT_MARKS_FRONT.length()));
			} else if (arg.startsWith(CUT_MARKS_BACK)) {
				cutMarksBack = Boolean.parseBoolean(arg.substring(CUT_MARKS_BACK.length()));
			} else if (arg.startsWith(CUT_MARKS_WIDTH)) {
				cutMarksWidth = parseInt(arg.substring(CUT_MARKS_WIDTH.length()));
			} else if (arg.startsWith(CUT_MARKS_COLOR)) {
				cutMarksColor = argToColor(arg.substring(CUT_MARKS_COLOR.length()));
			} else if (arg.startsWith(EDGE_BLEED_SIZE)) {
				edgeBleedSize = parseInt(arg.substring(EDGE_BLEED_SIZE.length()));
			} else if (arg.startsWith(EDGE_BLEED_COLOR_FRONT)) {
				edgeBleedColorFront = argToColor(arg.substring(EDGE_BLEED_COLOR_FRONT.length()));
			} else if (arg.startsWith(EDGE_BLEED_COLOR_BACK)) {
				edgeBleedColorBack = argToColor(arg.substring(EDGE_BLEED_COLOR_BACK.length()));
			} else if (arg.startsWith(COPIES)) {
				copies = parseInt(arg.substring(COPIES.length()));
			} else if (arg.startsWith(BACK)) {
				back = arg.substring(BACK.length());
			}
		}
		
		if (src == null || dst == null || paperType == null || dpi == null || cardSize == null) {
			System.err.println("Wrong arguments");
			System.exit(1);
		}
		
		new CardPacker(src, dst, paperType, dpi, printMarginInPixels, cardSize,
				cardCutSize, cutMarksFront, cutMarksBack, cutMarksWidth,
				cutMarksColor, cutCardFront, cutCardBack,
				edgeBleedSize, edgeBleedColorFront, edgeBleedColorBack,
				back, copies).run();
	}
	
	private static Size argToSize(String s) {
		int index = s.indexOf("x");
		return new Size(
				parseInt(s.substring(0, index)),
				parseInt(s.substring(index + 1)));
	}
	
	private static int argToColor(String s) {
		int color = (int) (Long.parseLong(s, 16) & 0xFFFFFF);
		return  (color & 0xFF0000) >> 16 |
				(color & 0x00FF00) |
				(color & 0x0000FF) << 16;
	}
	
	private final File src;
	private final File dst;
	private final PaperType paperType;
	private final DPI dpi;
	private final int printMarginInPixels;
	private final Size cardSize;
	private final Size cardCutSize;
	private final boolean cutMarksFront;
	private final boolean cutMarksBack;
	private final int cutMarksWidth;
	private final int cutMarksColor;
	private final boolean cutCardFront;
	private final boolean cutCardBack;
	private final int edgeBleedSize;
	private final Integer edgeBleedColorFront;
	private final Integer edgeBleedColorBack;
	private final String back;
	private final int copies;
	
	private final Logger logger;
	
	public CardPacker(File src, File dst, PaperType paperType, DPI dpi, int printSafeBorderInPixels,
			Size cardSize, Size cutSize, boolean cutMarksFront, boolean cutMarksBack, int cutMarksWidth,
			int cutMarksColor, boolean cutCardFront, boolean cutCardBack,
			int edgeBleedSize, Integer edgeBleedColorFront, Integer edgeBleedColorBack,
			String back, int copies) {
		this.src = src;
		this.dst = dst;
		this.paperType = paperType;
		this.dpi = dpi;
		this.printMarginInPixels = printSafeBorderInPixels;
		this.cardSize = cardSize;
		this.cardCutSize = cutSize;
		this.cutMarksFront = cutMarksFront;
		this.cutMarksBack = cutMarksBack;
		this.cutMarksWidth = cutMarksWidth;
		this.cutMarksColor = cutMarksColor;
		this.cutCardFront = cutCardFront;
		this.cutCardBack = cutCardBack;
		this.edgeBleedSize = edgeBleedSize;
		this.edgeBleedColorFront = edgeBleedColorFront;
		this.edgeBleedColorBack = edgeBleedColorBack;
		this.back = back;
		this.copies = copies;
		
		logger = new Logger(dst.getName());
	}
	
	@Override
	public final void run() {
		try {
			try (ImageWriter imageWriter = new ImageWriter(logger, 2)) {
				List<Card> cards = new CardScanner(logger, src, true, back, copies).scan();
				packCards(cards, imageWriter);
			}
			convertToPdf();
		} catch(Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	private final void packCards(List<Card> cards, ImageWriter imageWriter) {
		// Write cards
		int pageIndex = 0;
		for (Iterator<Card> cardIterator = cards.iterator(); cardIterator.hasNext(); ) {
			logger.log("Packing to page " + pageIndex);
			Paper paper = createPaper(pageIndex);
			// Cut marks
			writeCutMarks(paper);
			// Cut bleeds
			writeBleeds(paper);
			// Cut cards
			paper.iterateOnCardAreas(cardArea -> {
					if (cardIterator.hasNext()) {
						Card card = cardIterator.next();
						
						logger.log(" front = " + card.front);
						writeSide(card.front, paper.frontTex, cardArea.front,
								cutCardFront ? WriteCardMode.CENTER : WriteCardMode.SCALE);
						
						if (card.back != null) {
							logger.log(" back = " + card.back);
							writeSide(card.back, paper.backTex, cardArea.back,
									cutCardBack ? WriteCardMode.CENTER : WriteCardMode.SCALE);
						}
					}
				});

			paper.write(imageWriter, dst);
			
			pageIndex++;
		}
	}
	
	private Paper createPaper(int pageIndex) {
		int width = paperType.getWidth(dpi);
		int height = paperType.getHeight(dpi);
		
		int innerWidth = width - 2 * printMarginInPixels;
		int innerHeight = height - 2 * printMarginInPixels;
		
		// Card grid size
		Size cellSize = cutCardFront || cutCardBack ? cardCutSize : cardSize;
		int numCardWidth = innerWidth / cellSize.width;
		int numCardHeight = innerHeight / cellSize.height;

		// Center on page
		int offsetWidth = (width - numCardWidth * cellSize.width) / 2;
		int offsetHeight = (height - numCardHeight * cellSize.height) / 2;
		
		PaperGrid grid = new PaperGrid(
				cellSize,
				numCardWidth, numCardHeight,
				offsetWidth, offsetHeight);
		return new Paper(width, height, grid, back != null, pageIndex);
	}
	
	private enum WriteCardMode { CENTER, SCALE; }

	private void writeSide(CardSide side, ITexture dstTexture, Area area, WriteCardMode mode) {
		Size size = mode == WriteCardMode.CENTER ? cardSize : area.getSize(new Size());
		
		ITexture sideTexture = side.readTexture();
		if (sideTexture.getWidth() != size.width || sideTexture.getHeight() != size.height) {
			logger.log("  Resizing up");
			sideTexture = TextureUtils.resize(sideTexture, size.width, size.height, 1);
		}

		int srcOffsetWidth = (size.width - area.width) / 2;
		int srcOffsetHeight = (size.height - area.height) / 2;
		
		TextureUtils.copyRegion(
				dstTexture, new Area(area.x, area.y, area.width, area.height), 0,
				sideTexture, new Area(srcOffsetWidth, srcOffsetHeight, area.width, area.height), 0);
	}

	private void writeCutMarks(Paper paper) {
		if (cardCutSize == null || cutMarksWidth <= 0) {
			return;
		}
		logger.log(" cut marks");
		if (cutMarksFront) {
			writeCutMarksOnSide(paper.frontTex, paper.grid,
					cutCardFront ? WriteCardMode.CENTER : WriteCardMode.SCALE);
		}
		if (cutMarksBack && paper.backTex != null) {
			writeCutMarksOnSide(paper.backTex, paper.grid,
					cutCardBack ? WriteCardMode.CENTER : WriteCardMode.SCALE);
		}
	}

	private void writeCutMarksOnSide(ITexture side, PaperGrid grid, WriteCardMode mode) {
		Size size = mode == WriteCardMode.CENTER ? cardSize : grid.cellSize;
		int cutOffsetWidth = (size.width - grid.cellSize.width) / 2;
		int cutOffsetHeight = (size.height - grid.cellSize.height) / 2;
		
		for (int i = grid.offsetWidth; i + grid.cellSize.width < side.getWidth(); i += grid.cellSize.width) {
			writeRectangle(side, new Area(
					cutOffsetWidth + i - cutMarksWidth,
					0,
					cutMarksWidth,
					side.getHeight()), cutMarksColor);
			
			writeRectangle(side, new Area(
					grid.cellSize.width - cutOffsetWidth + i,
					0,
					cutMarksWidth,
					side.getHeight()), cutMarksColor);
		}
		
		for (int j = grid.offsetHeight; j + grid.cellSize.height < side.getHeight(); j += grid.cellSize.height) {
			writeRectangle(side, new Area(
					0,
					cutOffsetHeight + j - cutMarksWidth,
					side.getWidth(),
					cutMarksWidth), cutMarksColor);
			
			writeRectangle(side, new Area(
					0,
					grid.cellSize.height - cutOffsetHeight + j,
					side.getWidth(),
					cutMarksWidth), cutMarksColor);
		}
	}
	
	private void writeBleeds(Paper paper) {
		if (edgeBleedSize <= 0) {
			return;
		}
		paper.iterateOnCardAreas(cardArea -> {
				if (edgeBleedColorFront != null) {
					writeBleed(paper.frontTex, cardArea.front, edgeBleedColorFront);
				}
				if (paper.back && edgeBleedColorBack != null) {
					writeBleed(paper.backTex, cardArea.back, edgeBleedColorBack);
				}
			});
	}
	
	private void writeBleed(ITexture side, Area area, int color) {
		writeRectangle(side,
				new Area(area.x - edgeBleedSize, area.y - edgeBleedSize, edgeBleedSize, area.getHeight() + 2 * edgeBleedSize),
				color);
		writeRectangle(side,
				new Area(area.x + area.width, area.y - edgeBleedSize, edgeBleedSize, area.getHeight() + 2 * edgeBleedSize),
				color);
		writeRectangle(side,
				new Area(area.x, area.y - edgeBleedSize, area.getWidth(), edgeBleedSize),
				color);
		writeRectangle(side,
				new Area(area.x, area.y + area.height, area.getWidth(), edgeBleedSize),
				color);
	}
	
	private static void writeRectangle(ITexture side, Area area, int color) {
		for (int j = 0; j < area.height; j++) {
			for (int i = 0; i < area.width; i++) {
				side.setColor(0, 0,
						area.x + i,
						area.y + j,
						0,
						color);
			}
		}
	}
	
	private void convertToPdf() throws IOException {
		logger.log("Generating pdf");
		PdfSupport.imagesToPdf(dpi, dst, Paper.EXT, dst.getName() + ".pdf");
	}
}
