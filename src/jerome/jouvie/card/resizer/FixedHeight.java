/**
 * card-print-tools - https://github.com/jerome-jouvie/card-print-tools
 * Copyright @ 2018 Jérôme Jouvie (jerome.jouvie@gmail.com)
 */
package jerome.jouvie.card.resizer;

import static com.bonzaiengine.io.ParserUtils.parseInt;

import java.io.File;
import java.util.List;

import com.bonzaiengine.io.Stream;
import com.bonzaiengine.io.log.Log;
import com.bonzaiengine.texture.ITexture;
import com.bonzaiengine.texture.TextureUtils;
import jerome.jouvie.card.Card;
import jerome.jouvie.card.CardSide;
import jerome.jouvie.card.tool.ImageWriter;
import jerome.jouvie.card.tool.CardScanner;
import jerome.jouvie.card.tool.Logger;

public class FixedHeight implements Runnable {
	private static final String INPUT = "-input=";
	private static final String OUTPUT = "-output=";
	private static final String HEIGHT = "-height=";
	
	public static void main(String[] args) {
		Log.println(
				"Card height resizer - For non commercial use\n" +
				"\n" +
				"Jérôme Jouvie\n" +
				"jerome.jouvie@gmail.com\n" +
				"https://github.com/jerome-jouvie/card-print-tools\n" +
				"\n" +
				"Usage: " + FixedHeight.class.getSimpleName() + " \\\n" +
				INPUT + "<inputDirOfImageToPack> \\\n" +
				OUTPUT + "<outputDirOfPackerImages> \\\n" +
				HEIGHT + "<heightInPixels> \\\n");
		
		File src = null;
		File dst = null;
		int height = 0;
		
		/* Additional arguments */
		for (String arg : args) {
			if (arg.startsWith(INPUT)) {
				src = new File(arg.substring(INPUT.length()));
			} else if (arg.startsWith(OUTPUT)) {
				dst = new File(arg.substring(OUTPUT.length()));
			} else if (arg.startsWith(HEIGHT)) {
				height = parseInt(arg.substring(HEIGHT.length()));
			}
		}
		
		if (src == null || dst == null || height == 0) {
			System.err.println("Wrong arguments");
			System.exit(1);
		}
		
		new FixedHeight(src, dst, height).run();
		
	}

	private final File srcDir;
	private final File dstDir;
	
	private final int height;
	
	private final Logger logger;
	
	public FixedHeight(File srcDir, File dstDir, int height) {
		this.srcDir = srcDir;
		this.dstDir = dstDir;
		this.height = height;
		
		logger = new Logger(dstDir.getName());
	}
	
	@Override
	public final void run() {
		try {
			try (ImageWriter imageWriter = new ImageWriter(logger, 2)) {
				List<Card> cards = new CardScanner(logger, srcDir, false, null, 1).scan();
				for (Card card : cards) {
					ITexture dst = resize(card.front);
					writeCard(imageWriter, card.front.image.getName(), dst);
				}
			}
		} catch(Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	private ITexture resize(CardSide side) {
		ITexture src = side.readTexture();
		return TextureUtils.resize(src, src.getWidth() * height / src.getHeight(), height, 1);
	}

	private void writeCard(ImageWriter imageWriter, String name, ITexture dst) {
		Stream dstFile = new Stream(dstDir.getPath(), name);
		imageWriter.writeAsync(dst, dstFile);
	}

}
