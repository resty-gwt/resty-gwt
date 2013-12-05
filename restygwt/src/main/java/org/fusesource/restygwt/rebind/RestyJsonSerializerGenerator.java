package org.fusesource.restygwt.rebind;

import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

/**
 * @author Kirill Ponomaryov, 12/2/13
 */
public interface RestyJsonSerializerGenerator {

    Class<? extends JsonEncoderDecoderClassCreator> getGeneratorClass();

    JType getType(TypeOracle typeOracle);

}
