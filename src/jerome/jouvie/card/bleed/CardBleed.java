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
				BLEED_SIZE + "<bleedSizeInPixels>] \\\n" +
				LEFT_BLEED_SIZE + "<leftBleedSizeInPixels>] \\\n" +
				RIGHT_BLEED_SIZE + "<rightBleedSizeInPixels>] \\\n" +
				BOTTOM_BLEED_SIZE + "<bottomBleedSizeInPixels>] \\\n" +
				TOP_BLEED_SIZE + "<topBleedSizeInPixels>] \\\n");
		
		File src = null;
		File dst = null;
		int leftBleedSize = 0;
		int rightBleedSize = 0;
		int bottomBleedSize = 0;
		int topBleedSize = 0;
		
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
			}
		}
		
		if (src == null || dst == null || (leftBleedSize + rightBleedSize + bottomBleedSize + topBleedSize) == 0) {
			System.err.println("Wrong arguments");
			System.exit(1);
		}
		
		new CardBleed(src, dst, leftBleedSize, rightBleedSize, bottomBleedSize, topBleedSize).run();
		
	}

	private final File srcDir;
	private final File dstDir;
	
	private final int leftBleedInPixels;
	private final int rightBleedInPixels;
	private final int bottomBleedInPixels;
	private final int topBleedInPixels;
	
	private final Logger logger;
	
	public CardBleed(File srcDir, File dstDir,
			int leftBleedInPixels, int rightBleedInPixels,
			int bottomBleedInPixels, int topBleedInPixels) {
		this.srcDir = srcDir;
		this.dstDir = dstDir;
		this.leftBleedInPixels = leftBleedInPixels;
		this.rightBleedInPixels = rightBleedInPixels;
		this.bottomBleedInPixels = bottomBleedInPixels;
		this.topBleedInPixels = topBleedInPixels;
		
		logger = new Logger(dstDir.getName());
	}
	
	@Override
	public final void run() {
		try {
			try (ImageWriter imageWriter = new ImageWriter(logger, 2)) {
				List<Card> cards = new CardScanner(logger, srcDir, false, null, 1).scan();
				for (Card card : cards) {
					createBleed(imageWriter, card.front);
				}
			}
		} catch(Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	private void createBleed(ImageWriter imageWriter, CardSide side) {
		ITexture src = side.readTexture();
		ITexture dst = new TextureByte(
				src.getNumChannels(),
				src.getWidth() + leftBleedInPixels + rightBleedInPixels,
				src.getHeight() + bottomBleedInPixels + topBleedInPixels,
				src.getDepth());

		TextureUtils.copyRegion(
				dst, new Area(leftBleedInPixels, bottomBleedInPixels, src.getWidth(), src.getHeight()), 0,
				src, new Area(0, 0, src.getWidth(), src.getHeight()), 0);
		
		// Bleed left
		if (leftBleedInPixels > 0) {
			for (int j = 0; j < src.getHeight(); j++) {
				int color = dst.getColor(0, 0,
						leftBleedInPixels,
						j + bottomBleedInPixels,
						0);
				for (int i = 0; i < leftBleedInPixels; i++) {
					dst.setColor(0, 0,
							i,
							j + bottomBleedInPixels,
							0, color);
				}
			}
		}
		// Bleed right
		if (rightBleedInPixels > 0) {
			int rightBleedStart = dst.getWidth() - rightBleedInPixels;
			for (int j = 0; j < src.getHeight(); j++) {
				int color = dst.getColor(0, 0,
						rightBleedStart - 1,
						j + bottomBleedInPixels,
						0);
				for (int i = rightBleedStart; i < dst.getWidth(); i++) {
					dst.setColor(0, 0,
							i,
							j + bottomBleedInPixels,
							0, color);
				}
			}
		}
		// Bleed bottom
		if (bottomBleedInPixels > 0) {
			for (int i = 0; i < dst.getWidth(); i++) {
				int color = dst.getColor(0, 0,
						i,
						bottomBleedInPixels,
						0);
				for (int j = 0; j < bottomBleedInPixels; j++) {
					dst.setColor(0, 0,
							i,
							j,
							0, color);
				}
			}
		}
		// Bleed top
		if (topBleedInPixels > 0) {
			int topBleedStart = dst.getHeight() - topBleedInPixels;
			for (int i = 0; i < dst.getWidth(); i++) {
				int color = dst.getColor(0, 0,
						i,
						topBleedStart - 1,
						0);
				for (int j = topBleedStart; j < dst.getHeight(); j++) {
					dst.setColor(0, 0,
							i,
							j,
							0, color);
				}
			}
		}
		
		Stream dstFile = new Stream(dstDir.getPath(), side.image.getName());
		imageWriter.writeAsync(dst, dstFile);
	}
	
}
