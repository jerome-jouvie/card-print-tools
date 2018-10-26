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

public class FixedWidth implements Runnable {
	private static final String INPUT = "-input=";
	private static final String OUTPUT = "-output=";
	private static final String WIDTH = "-width=";
	
	public static void main(String[] args) {
		Log.println(
				"Card width resizer - For non commercial use\n" +
				"\n" +
				"Jérôme Jouvie\n" +
				"jerome.jouvie@gmail.com\n" +
				"https://github.com/jerome-jouvie/card-print-tools\n" +
				"\n" +
				"Usage: " + FixedWidth.class.getSimpleName() + " \\\n" +
				INPUT + "<inputDirOfImageToPack> \\\n" +
				OUTPUT + "<outputDirOfPackerImages> \\\n" +
				WIDTH + "<widthInPixels> \\\n");
		
		File src = null;
		File dst = null;
		int width = 0;
		
		/* Additional arguments */
		for (String arg : args) {
			if (arg.startsWith(INPUT)) {
				src = new File(arg.substring(INPUT.length()));
			} else if (arg.startsWith(OUTPUT)) {
				dst = new File(arg.substring(OUTPUT.length()));
			} else if (arg.startsWith(WIDTH)) {
				width = parseInt(arg.substring(WIDTH.length()));
			}
		}
		
		if (src == null || dst == null || width == 0) {
			System.err.println("Wrong arguments");
			System.exit(1);
		}
		
		new FixedWidth(src, dst, width).run();
		
	}

	private final File srcDir;
	private final File dstDir;
	
	private final int width;
	
	private final Logger logger;
	
	public FixedWidth(File srcDir, File dstDir, int width) {
		this.srcDir = srcDir;
		this.dstDir = dstDir;
		this.width = width;
		
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
		return TextureUtils.resize(src, width, src.getHeight() * width / src.getWidth(), 1);
	}

	private void writeCard(ImageWriter imageWriter, String name, ITexture dst) {
		Stream dstFile = new Stream(dstDir.getPath(), name);
		imageWriter.writeAsync(dst, dstFile);
	}

}
