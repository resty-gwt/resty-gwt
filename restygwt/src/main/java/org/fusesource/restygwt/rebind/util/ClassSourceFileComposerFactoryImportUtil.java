package org.fusesource.restygwt.rebind.util;

import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
/**
* @author Eric Le
*/
public class ClassSourceFileComposerFactoryImportUtil {
	/**
	 * Static class which adds all the <i>org.fusesource.restygwt.client.AbstractJsonEncoderDecoder</i>
	 * static imports to the <i>ClassSourceFileComposerFactory</i>
	 * @param composerFactory the ClassSourceFileComposerFactory to which the static imports are added
	 */
	public static void addFuseSourceStaticImports(ClassSourceFileComposerFactory composerFactory) {
		composerFactory.addImport("static org.fusesource.restygwt.client.AbstractJsonEncoderDecoder.*");
	}
}
