package org.fusesource.restygwt.rebind;

import java.util.HashMap;
import java.util.Map;

import org.fusesource.restygwt.client.JsonEncoderDecoder;
import org.fusesource.restygwt.client.JsonTypeResolver;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

public abstract class JsonTypeResolverClassCreator extends Generator
{
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException
	{
		try
		{
			JClassType type = context.getTypeOracle().findType(typeName);
			if (type == null)
			{
				logger.log(TreeLogger.ERROR, "TypeOracle could not find " + type);
				throw new UnableToCompleteException();
			}
			BaseSourceCreator generator = new BaseSourceCreator(logger, context, type, "_Generated_JsonTypeResolver")
			{

				@Override
				protected ClassSourceFileComposerFactory createComposerFactory() throws UnableToCompleteException
				{
					ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, shortName);
					composerFactory.addImplementedInterface(JsonTypeResolver.class.getName());
					return composerFactory;
				}

				@Override
				protected void generate() throws UnableToCompleteException
				{
					p();
					p("private static final " + Map.class.getName() + "<" + String.class.getName() + ", " + String.class.getName() +"> cClassnameToId = new " + HashMap.class.getName() + "<" + String.class.getName() + ", " + String.class.getName() +">();");
					p("private static final " + Map.class.getName() + "<" + String.class.getName() + ", " + JsonEncoderDecoder.class.getName() + "<?>> cIdToEncoder = new " + HashMap.class.getName() +"<" + String.class.getName() + ", " + JsonEncoderDecoder.class.getName() + "<?>>();");
					p();
					p("static");
					p("{").i(1);
					{
						for (Map.Entry<String, Class<?>> entry : getIdClassMap().entrySet())
						{
							p("cClassnameToId.put(\"" + entry.getValue().getName() + "\", \"" + entry.getKey() + "\");");
							JClassType classType = findType(entry.getValue().getName());
							JsonEncoderDecoderClassCreator generator = new JsonEncoderDecoderClassCreator(logger, context, classType);
							String encoderClassName = generator.create();
							p("cIdToEncoder.put(\"" + entry.getKey() + "\", " + encoderClassName + ".INSTANCE);");
						}
					}
					p("}").i(-1);

					p();
					p("public " + String.class.getName() + " getType("+ Object.class.getName() + "  object)");
					p("{").i(1);
					{
						p("return cClassnameToId.get(object.getClass().getName());");
					}
					p("}").i(-1);
					p();
					p("public <T> " + JsonEncoderDecoder.class.getName() + "<T> getEncoderDecoder(" + String.class.getName() + " type)");
					p("{").i(1);
					{
						p("return (" + JsonEncoderDecoder.class.getName() + "<T>)cIdToEncoder.get(type);");
					}
					p("}").i(-1);
					p();
				}

				private JClassType findType(String name) throws UnableToCompleteException
				{
					try
					{
						return context.getTypeOracle().getType(name.replace('$', '.'));
					}
					catch (NotFoundException e)
					{
				        logger.log(ERROR, "Unable to find type: " + name);
						throw new UnableToCompleteException();
					}
				}
			};

			return generator.create();
		}
		finally
		{
			BaseSourceCreator.clearGeneratedClasses();
		}
	}

	protected abstract Map<String, Class<?>> getIdClassMap();
}
