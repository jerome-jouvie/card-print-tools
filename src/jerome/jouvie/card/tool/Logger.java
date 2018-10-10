/**
 * card-print-tools - https://github.com/jerome-jouvie/card-print-tools
 * Copyright @ 2018 Jérôme Jouvie (jerome.jouvie@gmail.com)
 */
package jerome.jouvie.card.tool;

import com.bonzaiengine.io.log.Log;

public class Logger {
	
	private final String name;
	
	public Logger(String name) {
		this.name = name;
	}
	
	public void log(String s) {
		Log.println("[" + name + "] " + s);
	}

}
