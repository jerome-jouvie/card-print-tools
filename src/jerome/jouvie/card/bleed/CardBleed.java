/**
 * card-print-tools - https://github.com/jerome-jouvie/card-print-tools
 * Copyright @ 2018 Jérôme Jouvie (jerome.jouvie@gmail.com)
 */
package jerome.jouvie.card.bleed;

import static com.bonzaiengine.io.ParserUtils.parseInt;

import java.io.File;
import java.util.List;

import com.bonzaiengine.io.Stream;
import com.bonzaiengine.io.log.Log;
import com.bonzaiengine.math.Area;
import com.bonzaiengine.texture.ITexture;
import com.bonzaiengine.texture.TextureUtils;
import com.bonzaiengine.texture.format.TextureByte;

import jerome.jouvie.card.Card;
import jerome.jouvie.card.CardSide;
import jerome.jouvie.card.tool.ImageWriter;
import jerome.jouvie.card.tool.CardScanner;
import jerome.jouvie.card.tool.Logger;

public class CardBleed implements Runnable {
	private static final String INPUT = "-input=";
	private static final String OUTPUT = "-output=";
	private static final String BLEED_SIZE = "-bleedSize=";
	private static final String LEFT_BLEED_SIZE = "-leftBleedSize=";
	private static final String RIGHT_BLEED_SIZE = "-rightBleedSize=";
	private static final String BOTTOM_BLEED_SIZE = "-bottomBleedSize=";
	private static final String TOP_BLEED_SIZE = "-topBleedSize=";
	private static final String FILL_BLEED_TO_WIDTH = "-fillBleedToWidth=";
	private static final String FILL_BLEED_TO_HEIGHT = "-fillBleedToHeight=";
	private static final String BLEED_PATTERN_SIZE = "-bleedPatternSize=";
	private static final String LEFT_BLEED_PATTERN_SIZE = "-leftBleedPatternSize=";
	private static final String RIGHT_BLEED_PATTERN_SIZE = "-rightBleedPatternSize=";
	private static final String BOTTOM_BLEED_PATTERN_SIZE = "-bottomBleedPatternSize=";
	private static final String TOP_BLEED_PATTERN_SIZE = "-topBleedPatternSize=";
	
	public static void main(String[] args) {
		Log.println(
				"Card Bleed - For non commercial use\n" +
				"\n" +
				"Jérôme Jouvie\n" +
				"jerome.jouvie@gmail.com\n" +
				"https://github.com/jerome-jouvie/card-print-tools\n" +
				"\n" +
				"Usage: " + CardBleed.class.getSimpleName() + " \\\n" +
				INPUT + "<inputDirOfImageToPack> \\\n" +
				OUTPUT + "<outputDirOfPackerImages> \\\n" +
				BLEED_SIZE + "<bleedSizeInPixels> \\\n" +
				LEFT_BLEED_SIZE + "<leftBleedSizeInPixels> \\\n" +
				RIGHT_BLEED_SIZE + "<rightBleedSizeInPixels> \\\n" +
				BOTTOM_BLEED_SIZE + "<bottomBleedSizeInPixels> \\\n" +
				TOP_BLEED_SIZE + "<topBleedSizeInPixels> \\\n" +
				"[" + BLEED_PATTERN_SIZE + "<bleedPatternSizeInPixels>] \\\n" +
				"[" + LEFT_BLEED_PATTERN_SIZE + "<leftBleedPatternSizeInPixels>] \\\n" +
				"[" + RIGHT_BLEED_PATTERN_SIZE + "<rightBleedPatternSizeInPixels>] \\\n" +
				"[" + BOTTOM_BLEED_PATTERN_SIZE + "<bottomBleedPatternSizeInPixels>] \\\n" +
				"[" + TOP_BLEED_PATTERN_SIZE + "<topBleedPatternSizeInPixels>] \\\n");
		
		File src = null;
		File dst = null;
		int leftBleedSize = 0;
		int rightBleedSize = 0;
		int bottomBleedSize = 0;
		int topBleedSize = 0;
		int fillBleedToWidth = 0;
		int fillBleedToHeight = 0;
		int leftBleedPatternSize = 1;
		int rightBleedPatternSize = 1;
		int bottomBleedPatternSize = 1;
		int topBleedPatternSize = 1;
		
		/* Additional arguments */
		for (String arg : args) {
			if (arg.startsWith(INPUT)) {
				src = new File(arg.substring(INPUT.length()));
			} else if (arg.startsWith(OUTPUT)) {
				dst = new File(arg.substring(OUTPUT.length()));
			} else if (arg.startsWith(BLEED_SIZE)) {
				leftBleedSize = parseInt(arg.substring(BLEED_SIZE.length()));
				rightBleedSize = leftBleedSize;
				bottomBleedSize = leftBleedSize;
				topBleedSize = leftBleedSize;
			} else if (arg.startsWith(LEFT_BLEED_SIZE)) {
				leftBleedSize = parseInt(arg.substring(LEFT_BLEED_SIZE.length()));
			} else if (arg.startsWith(RIGHT_BLEED_SIZE)) {
				rightBleedSize = parseInt(arg.substring(RIGHT_BLEED_SIZE.length()));
			} else if (arg.startsWith(BOTTOM_BLEED_SIZE)) {
				bottomBleedSize = parseInt(arg.substring(BOTTOM_BLEED_SIZE.length()));
			} else if (arg.startsWith(TOP_BLEED_SIZE)) {
				topBleedSize = parseInt(arg.substring(TOP_BLEED_SIZE.length()));
			} else if (arg.startsWith(FILL_BLEED_TO_WIDTH)) {
				fillBleedToWidth = parseInt(arg.substring(FILL_BLEED_TO_WIDTH.length()));
			} else if (arg.startsWith(FILL_BLEED_TO_HEIGHT)) {
				fillBleedToHeight = parseInt(arg.substring(FILL_BLEED_TO_HEIGHT.length()));
			} else if (arg.startsWith(TOP_BLEED_SIZE)) {
				topBleedSize = parseInt(arg.substring(TOP_BLEED_SIZE.length()));
			} else if (arg.startsWith(BLEED_PATTERN_SIZE)) {
				leftBleedPatternSize = parseInt(arg.substring(BLEED_PATTERN_SIZE.length()));
				rightBleedPatternSize = leftBleedPatternSize;
				bottomBleedPatternSize = leftBleedPatternSize;
				topBleedPatternSize = leftBleedPatternSize;
			} else if (arg.startsWith(LEFT_BLEED_PATTERN_SIZE)) {
				leftBleedPatternSize = parseInt(arg.substring(LEFT_BLEED_PATTERN_SIZE.length()));
			} else if (arg.startsWith(RIGHT_BLEED_PATTERN_SIZE)) {
				rightBleedPatternSize = parseInt(arg.substring(RIGHT_BLEED_PATTERN_SIZE.length()));
			} else if (arg.startsWith(BOTTOM_BLEED_PATTERN_SIZE)) {
				bottomBleedPatternSize = parseInt(arg.substring(BOTTOM_BLEED_PATTERN_SIZE.length()));
			} else if (arg.startsWith(TOP_BLEED_PATTERN_SIZE)) {
				topBleedPatternSize = parseInt(arg.substring(TOP_BLEED_PATTERN_SIZE.length()));
			}
		}
		
		if (src == null || dst == null ||
				(leftBleedSize + rightBleedSize + bottomBleedSize + topBleedSize +
						fillBleedToWidth + fillBleedToHeight) == 0) {
			System.err.println("Wrong arguments");
			System.exit(1);
		}
		
		new CardBleed(src, dst, leftBleedSize, rightBleedSize, bottomBleedSize, topBleedSize,
				fillBleedToWidth, fillBleedToHeight,
				leftBleedPatternSize, rightBleedPatternSize, bottomBleedPatternSize, topBleedPatternSize).run();
		
	}

	private final File srcDir;
	private final File dstDir;
	
	private final int leftBleedSize;
	private final int rightBleedSize;
	private final int bottomBleedSize;
	private final int topBleedSize;
	private final int fillBleedToWidthSize;
	private final int fillBleedToHeightSize;
	
	private final int leftBleedPatternSize;
	private final int rightBleedPatternSize;
	private final int bottomBleedPatternSize;
	private final int topBleedPatternSize;
	
	private final Logger logger;
	
	public CardBleed(File srcDir, File dstDir,
			int leftBleedInPixels, int rightBleedInPixels,
			int bottomBleedInPixels, int topBleedInPixels,
			int fillBleedToWidthInPixels, int fillBleedToHeightInPixels,
			int leftBleedPatternSize, int rightBleedPatternSize,
			int bottomBleedPatternSize, int topBleedPatternSize) {
		this.srcDir = srcDir;
		this.dstDir = dstDir;
		this.leftBleedSize = leftBleedInPixels;
		this.rightBleedSize = rightBleedInPixels;
		this.bottomBleedSize = bottomBleedInPixels;
		this.topBleedSize = topBleedInPixels;
		this.fillBleedToWidthSize = fillBleedToWidthInPixels;
		this.fillBleedToHeightSize = fillBleedToHeightInPixels;
		this.leftBleedPatternSize = leftBleedPatternSize;
		this.rightBleedPatternSize = rightBleedPatternSize;
		this.bottomBleedPatternSize = bottomBleedPatternSize;
		this.topBleedPatternSize = topBleedPatternSize;
		
		logger = new Logger(dstDir.getName());
	}
	
	@Override
	public final void run() {
		try {
			try (ImageWriter imageWriter = new ImageWriter(logger, 2)) {
				List<Card> cards = new CardScanner(logger, srcDir, false, null, 1).scan();
				for (Card card : cards) {
					ITexture dst = createBleed(card.front);
					writeCard(imageWriter, card, dst);
				}
			}
		} catch(Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	private ITexture createBleed(CardSide side) {
		ITexture src = side.readTexture();
		int leftBleedSize = this.fillBleedToWidthSize > 0 ?
				(fillBleedToWidthSize - src.getWidth()) / 2 : this.leftBleedSize;
		int rightBleedSize = this.fillBleedToWidthSize > 0 ?
				(fillBleedToWidthSize - src.getWidth()) / 2 : this.rightBleedSize;
		int bottomBleedSize = this.fillBleedToHeightSize > 0 ?
				(fillBleedToHeightSize - src.getHeight()) / 2 : this.bottomBleedSize;
		int topBleedSize = this.fillBleedToHeightSize > 0 ?
				(fillBleedToHeightSize - src.getHeight()) / 2 : this.topBleedSize;
			
		ITexture dst = new TextureByte(
				src.getNumChannels(),
				src.getWidth() + leftBleedSize + rightBleedSize,
				src.getHeight() + bottomBleedSize + topBleedSize,
				src.getDepth());

		TextureUtils.copyRegion(
				dst, new Area(leftBleedSize, bottomBleedSize, src.getWidth(), src.getHeight()), 0,
				src, new Area(0, 0, src.getWidth(), src.getHeight()), 0);
		
		// Bleed left
		if (leftBleedSize > 0) {
			for (int j = 0; j < src.getHeight(); j++) {
				for (int i = 0; i < leftBleedSize; i++) {
					int color = dst.getColor(0, 0,
							leftBleedSize + i % leftBleedPatternSize,
							j + bottomBleedSize,
							0);
					dst.setColor(0, 0,
							i,
							j + bottomBleedSize,
							0, color);
				}
			}
		}
		// Bleed right
		if (rightBleedSize > 0) {
			
			int rightBleedStart = dst.getWidth() - rightBleedSize;
			for (int j = 0; j < src.getHeight(); j++) {
				for (int i = rightBleedStart; i < dst.getWidth(); i++) {
					int color = dst.getColor(0, 0,
							rightBleedStart - 1 - i % rightBleedPatternSize,
							j + bottomBleedSize,
							0);
					dst.setColor(0, 0,
							i,
							j + bottomBleedSize,
							0, color);
				}
			}
		}
		// Bleed bottom
		if (bottomBleedSize > 0) {
			for (int i = 0; i < dst.getWidth(); i++) {
				for (int j = 0; j < bottomBleedSize; j++) {
					int color = dst.getColor(0, 0,
							i,
							bottomBleedSize + j % bottomBleedPatternSize,
							0);
					dst.setColor(0, 0,
							i,
							j,
							0, color);
				}
			}
		}
		// Bleed top
		if (topBleedSize > 0) {
			int topBleedStart = dst.getHeight() - topBleedSize;
			for (int i = 0; i < dst.getWidth(); i++) {
				for (int j = topBleedStart; j < dst.getHeight(); j++) {
					int color = dst.getColor(0, 0,
							i,
							topBleedStart - 1 - j % topBleedPatternSize,
							0);
					dst.setColor(0, 0,
							i,
							j,
							0, color);
				}
			}
		}
		
		return dst;
	}

	private void writeCard(ImageWriter imageWriter, Card card, ITexture dst) {
		Stream dstFile = new Stream(dstDir.getPath(), card.front.image.getName());
		imageWriter.writeAsync(dst, dstFile);
	}
}
