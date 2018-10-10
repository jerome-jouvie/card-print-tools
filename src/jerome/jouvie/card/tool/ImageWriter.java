/**
 * card-print-tools - https://github.com/jerome-jouvie/card-print-tools
 * Copyright @ 2018 Jérôme Jouvie (jerome.jouvie@gmail.com)
 */
package jerome.jouvie.card.tool;

import java.io.Closeable;

import com.bonzaiengine.io.Stream;
import com.bonzaiengine.texture.ITexture;
import com.bonzaiengine.texture.TextureIO;
import com.bonzaiengine.texture.TextureWriterParam;
import com.bonzaiengine.threading.IWorker;
import com.bonzaiengine.threading.Task;
import com.bonzaiengine.threading.WorkerDispatcher;

public class ImageWriter implements Closeable {
	
	private final Logger logger;
	private final IWorker worker;
	
	public ImageWriter(Logger logger, int numWorkers) {
		this.logger = logger;
		this.worker = new WorkerDispatcher(numWorkers);
	}

	public void writeAsync(ITexture texture, Stream dst) {
		worker.queue(new Task<Boolean>() {
			@Override public Boolean process() throws Throwable {
				logger.log("Writing image " + dst.getName());
				
				TextureWriterParam params = new TextureWriterParam(dst);
				TextureIO.get().writeTexture(params, texture);
				
				return Boolean.TRUE;
			}
		});
	}
	
	@Override
	public void close() {
		worker.waitQueueEmpty();
		worker.stop();
	}
}
