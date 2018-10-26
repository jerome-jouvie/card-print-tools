/**
 * card-print-tools - https://github.com/jerome-jouvie/card-print-tools
 * Copyright @ 2018 Jérôme Jouvie (jerome.jouvie@gmail.com)
 */
package jerome.jouvie.card.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.bonzaiengine.io.Stream;
import com.bonzaiengine.texture.TextureIO;

import jerome.jouvie.card.Card;
import jerome.jouvie.card.CardSide;

public class CardScanner {

	private final Logger logger;
	private final File dir;
	private final boolean recursive;
	private final String back;
	private final int copies;
	
	public CardScanner(Logger logger, File dir, boolean recursive, String back, int copies) {
		this.logger = logger;
		this.dir = dir;
		this.recursive = recursive;
		this.back = back;
		this.copies = copies;
	}
	
	public List<Card> scan() {
		List<Card> cards = new ArrayList<>();
		scanCards(dir, cards);
		return cards;
	}
	
	private final void scanCards(File dir, List<Card> cards) {
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		Arrays.sort(files, Comparator.comparing(f -> f.getName().toLowerCase()));
		
		for (File file : files) {
			if (recursive && file.isDirectory()) {
				scanCards(file, cards);
			}
		}
		
		List<Stream> images = new ArrayList<>();
		TextureIO textureIO = TextureIO.get();
		for (File file : files) {
			Stream stream = new Stream(file.getAbsolutePath());
			if (file.isFile() && textureIO.isReadSupported(stream)) {
				images.add(stream);
			}
		}
		processFolder(images, cards);
	}

	private final void processFolder(List<Stream> images, List<Card> cards) {
		CardSide imageBack = null;
		for (Stream image : images) {
			if (isBack(image)) {
				imageBack = new CardSide(image);
			}
		}

		for (Stream image : images) {
			if (!isBack(image)) {
				CardSide imageFront = new CardSide(image);
				Card card = new Card(imageFront, imageBack);
				for (int i = 0; i < copies; i++) {
					cards.add(card);
				}
				logger.log("Card: " + card.front + (card.back != null ? " <-> " + card.back : ""));
			}
		}
	}
	
	private boolean isBack(Stream image) {
		return back != null && image.getName().startsWith(back);
	}
}
