package org.fusesource.restygwt.server.complex;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import org.fusesource.restygwt.client.Json;
import org.fusesource.restygwt.rebind.JsonEncoderDecoderClassCreator;
import org.fusesource.restygwt.rebind.JsonEncoderDecoderInstanceLocator;

public class OptionalSerializerGenerator extends JsonEncoderDecoderClassCreator {

    private JClassType typeArg;

    public OptionalSerializerGenerator(TreeLogger logger, GeneratorContext context, JClassType source)
            throws UnableToCompleteException {
        super(logger, context, source);
    }

    private JClassType getTypeArg() throws UnableToCompleteException {
        JParameterizedType parameterizedType = source.isParameterized();
        if (parameterizedType == null || parameterizedType.getTypeArgs() == null || parameterizedType.getTypeArgs().length == 0) {
            getLogger().log(ERROR, "Optional types must be parameterized.");
            throw new UnableToCompleteException();
        }
        return parameterizedType.getTypeArgs()[0];
    }

    @Override
    public void generate() throws UnableToCompleteException {
        locator = new JsonEncoderDecoderInstanceLocator(context, getLogger());
        generateSingleton(shortName);
        typeArg = getTypeArg();
        generateEncodeMethod();
        generateDecodeMethod();
    }

    private void generateEncodeMethod() throws UnableToCompleteException {
        p("public " + JSON_VALUE_CLASS + " encode(" + source.getParameterizedQualifiedSourceName() + " value) {").i(1);
            p("if (value == null) {").i(1);
                p("return null;").i(-1);
            p("}");
            p("if (!value.isPresent()) {").i(1);
                p("return null;").i(-1);
            p("}");
            p("return " + locator.encodeExpression(typeArg, "value.get()", Json.Style.DEFAULT) + ";").i(-1);
        p("}");
        p();
    }

    private void generateDecodeMethod() throws UnableToCompleteException {
        p("public " + source.getName() + " decode(" + JSON_VALUE_CLASS + " value) {").i(1);
            p("if (value == null || value.isNull() != null ) {").i(1);
                p("return Optional.absent();").i(-1);
            p("}");
            p("return Optional.of(" + locator.decodeExpression(typeArg, "value", Json.Style.DEFAULT) + ");").i(-1);
        p("}");
        p();
    }

}
