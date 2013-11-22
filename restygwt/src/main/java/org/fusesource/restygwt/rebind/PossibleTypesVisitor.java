package org.fusesource.restygwt.rebind;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonTypeIdResolver;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.fusesource.restygwt.rebind.JsonEncoderDecoderClassCreator.Subtype;
import org.fusesource.restygwt.rebind.util.JsonTypeInfoIdVisitor;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

public class PossibleTypesVisitor extends JsonTypeInfoIdVisitor<List<Subtype>, UnableToCompleteException>
{
    private GeneratorContext context;
    private JClassType classType;
    private boolean isLeaf;
    private TreeLogger logger;
    private Collection<JsonSubTypes.Type> types;

    public PossibleTypesVisitor(GeneratorContext context, JClassType classType, final boolean isLeaf, TreeLogger logger, final Collection<Type> types)
    {
        this.context = context;
        this.classType = classType;
        this.isLeaf = isLeaf;
        this.logger = logger;
        this.types = types;
    }


    @Override
    public List<Subtype> visitClass() throws UnableToCompleteException
    {
        return getPossibleTypesForClass(context, classType, Id.CLASS, isLeaf, logger, types);
    }

    @Override
    public List<Subtype> visitMinClass() throws UnableToCompleteException
    {
        return getPossibleTypesForClass(context, classType, Id.MINIMAL_CLASS, isLeaf, logger, types);
    }


    @Override
    public List<Subtype> visitCustom() throws UnableToCompleteException
    {
        return getPossibleTypesForOther(context, classType, isLeaf, logger, types);
    }

    @Override
    public List<Subtype> visitName() throws UnableToCompleteException
    {
        return getPossibleTypesForOther(context, classType, isLeaf, logger, types);
    }

    @Override
    public List<Subtype> visitNone() throws UnableToCompleteException
    {
        logger.log(BaseSourceCreator.ERROR, "Id.NONE not supported");
        throw new UnableToCompleteException();
    }

    @Override
    public List<Subtype> visitDefault() throws UnableToCompleteException
    {
        return null;
    }


    protected List<Subtype> getPossibleTypesForOther(GeneratorContext context, JClassType classType, final boolean isLeaf, TreeLogger logger,
            final Collection<JsonSubTypes.Type> types) throws UnableToCompleteException
    {
        final List<Subtype> possibleTypes = Lists.newArrayList();

        final JsonTypeIdResolver typeResolver = JsonEncoderDecoderClassCreator.findAnnotation(classType, JsonTypeIdResolver.class);
        if (typeResolver != null) {
            Class<? extends TypeIdResolver> resolverClass = typeResolver.value();
            RestyJsonTypeIdResolver restyResolver;
            if (RestyJsonTypeIdResolver.class.isAssignableFrom(resolverClass)) {
            try {
                restyResolver = (RestyJsonTypeIdResolver) resolverClass.newInstance();
            } catch (Exception e) {
                logger.log(BaseSourceCreator.ERROR, "Could not acccess: " + resolverClass, e);
                throw new UnableToCompleteException();
            }
            } else {
            restyResolver = JsonEncoderDecoderClassCreator.getRestyResolverClassMap(context, logger).get(resolverClass);
            if (restyResolver == null)
            {
                logger.log(BaseSourceCreator.ERROR, "Could not find RestyJsonTypeIdResolver for " + resolverClass + " did you forget to put <extend-configuration-property name=\"org.fusesource.restygwt.jsontypeidresolver\" value=\"<fully-qualified-class-implementing-RestyJsonTypeIdResolver>\"/> in your *.gwt.xml?");
                throw new UnableToCompleteException();
            }
            }

            for (Map.Entry<String, Class<?>> entry : restyResolver.getIdClassMap().entrySet()) {
            JClassType entryType = BaseSourceCreator.find(entry.getValue(), logger, context);
            if (!isLeaf || classType.equals(entryType))
                possibleTypes.add(new Subtype(entry.getKey(), entryType));
            }
            if (isLeaf && possibleTypes.size() == 0)
            {
                logger.log(BaseSourceCreator.ERROR, "Could not find entry in " + restyResolver.getClass().getName() + " for type: " + classType);
                throw new UnableToCompleteException();
            }
        } else if (types != null) {
            for (JsonSubTypes.Type type : types) {
                if (type.name() != null && !type.name().isEmpty()) {
                    JClassType typeClass = BaseSourceCreator.find(type.value(), logger, context);
                    if (!isLeaf || classType.equals(typeClass))
                    possibleTypes.add(new Subtype(type.name(), typeClass));
                } else {
                    JsonTypeName nameAnnotation = type.value().getAnnotation(JsonTypeName.class);
                    if (nameAnnotation == null || nameAnnotation.value() == null || nameAnnotation.value().isEmpty())
                    {
                        logger.log(BaseSourceCreator.ERROR, "Cannot find @JsonTypeName annotation for type: " + type.value());
                        throw new UnableToCompleteException();
                    }
                    JClassType typeClass = BaseSourceCreator.find(type.value(), logger, context);
                    if (!isLeaf || classType.equals(typeClass))
                        possibleTypes.add(new Subtype(nameAnnotation.value(), typeClass));
                }

            }
            if (isLeaf && possibleTypes.size() == 0)
            {
                logger.log(BaseSourceCreator.ERROR, "Could not find @JsonSubTypes entry for type: " + classType);
                throw new UnableToCompleteException();
            }
        } else {
            logger.log(BaseSourceCreator.ERROR, "Cannot find required subtype resolution for type: " + classType);
            throw new UnableToCompleteException();
        }
        return possibleTypes;
    }

    protected List<Subtype> getPossibleTypesForClass(GeneratorContext context, JClassType classType, final Id id, final boolean isLeaf, TreeLogger logger, final Collection<JsonSubTypes.Type> types)
            throws UnableToCompleteException
    {
        final List<Subtype> possibleTypes = Lists.newArrayList();
        List<JClassType> resolvedSubtypes = Lists.newArrayList();

        if (types != null) {
            for (JsonSubTypes.Type type : types) {
                JClassType typeClass = BaseSourceCreator.find(type.value(), logger, context);
                if (!isLeaf || classType.equals(typeClass))
                    resolvedSubtypes.add(typeClass);
            }
        } else {
            for (JClassType typeClass : context.getTypeOracle().getTypes()) {
                if (!typeClass.isAbstract() && typeClass.isAssignableTo(classType))
                    resolvedSubtypes.add(typeClass);
            }
        }

        for (JClassType typeClass : resolvedSubtypes)
            possibleTypes.add(new Subtype(id == Id.CLASS ? typeClass.getQualifiedSourceName() : "." + typeClass.getSimpleSourceName(), typeClass));

        return possibleTypes;
    }


}
