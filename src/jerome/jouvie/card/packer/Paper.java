/**
 * card-print-tools - https://github.com/jerome-jouvie/card-print-tools
 * Copyright @ 2018 Jérôme Jouvie (jerome.jouvie@gmail.com)
 */
package jerome.jouvie.card.packer;

import java.io.File;
import java.util.function.Consumer;

import com.bonzaiengine.io.Stream;
import com.bonzaiengine.math.Area;
import com.bonzaiengine.texture.TextureUtils;
import com.bonzaiengine.texture.format.TextureByte;

import jerome.jouvie.card.tool.ImageWriter;

class Paper {

	public static final byte PAPER_COLOR = (byte) 0xFF;
	public static final String EXT = "png";

	public final int width;
	public final int height;
	public final PaperGrid grid;
	public final boolean back;
	public final int pageIndex;

	public final TextureByte frontTex;
	public final TextureByte backTex;
	
	public Paper(int width, int height, PaperGrid grid, boolean back, int pageIndex) {
		this.width = width;
		this.height = height;
		this.grid = grid;
		this.back = back;
		this.pageIndex = pageIndex;
		
		int numChannels = 3;
		frontTex = new TextureByte(numChannels, width, height, 1);
		backTex = back ? new TextureByte(numChannels, width, height, 1) : null;
		for (int i = 0; i < numChannels; i++) {
			TextureUtils.fill(frontTex, i, PAPER_COLOR);
			if (back) {
				TextureUtils.fill(backTex, i, PAPER_COLOR);
			}
		}
	}
	
	public void iterateOnCardAreas(Consumer<CardArea> consumer) {
		int x = grid.offsetWidth;
		int xBack = width - grid.offsetWidth - grid.cellSize.width;
		int y = grid.offsetHeight;

		for (int ch = 0; ch < grid.numCardHeight; ch++) {
			x = grid.offsetWidth;
			xBack = width - grid.offsetWidth - grid.cellSize.width;
			for (int cw = 0; cw < grid.numCardWidth; cw++) {
				consumer.accept(new CardArea(
						new Area(x, y, grid.cellSize.width, grid.cellSize.height),
						new Area(xBack, y, grid.cellSize.width, grid.cellSize.height)));
				
				x += grid.cellSize.width;
				xBack -= grid.cellSize.width;
			}
			y += grid.cellSize.height;
		}
	}
	
	public void write(ImageWriter imageWriter, File dst) {
		imageWriter.writeAsync(frontTex, new Stream(dst.getPath(), pageIndex + "-front." + EXT));
		if (backTex != null) {
			imageWriter.writeAsync(backTex, new Stream(dst.getPath(), pageIndex + "-back." + EXT));
		}
	}

}
