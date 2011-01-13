/**
 * 
 */
package org.fusesource.restygwt.examples.client.rails;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

public interface RailsController {
	
	String getPath();
	
	JsonEncoderDecoder<?> getCodec();
}