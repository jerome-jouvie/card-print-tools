/**
 * card-print-tools - https://github.com/jerome-jouvie/card-print-tools
 * Copyright @ 2018 Jérôme Jouvie (jerome.jouvie@gmail.com)
 */
package jerome.jouvie.card;

import com.bonzaiengine.io.Stream;
import com.bonzaiengine.texture.ITexture;
import com.bonzaiengine.texture.TextureIO;

public final class CardSide {
	public final Stream image;
	
	public CardSide(Stream image) {
		this.image = image;
	}
	
	@Override
	public String toString() {
		return image.getName();
	}
	
	public ITexture readTexture() {
		return TextureIO.get().readTexture(image);
	}
}
