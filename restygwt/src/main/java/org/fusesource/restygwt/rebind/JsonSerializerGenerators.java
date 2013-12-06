package org.fusesource.restygwt.rebind;

import com.google.gwt.core.ext.typeinfo.*;
import com.google.gwt.thirdparty.guava.common.collect.Maps;

import java.util.Map;

/**
 * @author Kirill Ponomaryov, 12/3/13
 */
public class JsonSerializerGenerators {

    private Map<JPrimitiveType, RestyJsonSerializerGenerator> primitiveMappings = null;

    private Map<JClassType, RestyJsonSerializerGenerator> classMappings = null;

    public void addGenerator(RestyJsonSerializerGenerator generator, TypeOracle typeOracle) {
        JType type = generator.getType(typeOracle);
        if (type.isPrimitive() != null) {
            if (primitiveMappings == null) {
                primitiveMappings = Maps.newHashMap();
            }
            primitiveMappings.put(type.isPrimitive(), generator);
        } else if (type.isClass() != null) {
            if (classMappings == null) {
                classMappings = Maps.newHashMap();
            }
            classMappings.put(type.isClass(), generator);
        }
    }

    private JClassType getBaseType(JClassType classType) {
        if (classType == null) {
            return null;
        }
        JParameterizedType parameterizedType = classType.isParameterized();
        return parameterizedType == null ? classType : parameterizedType.getBaseType();
    }

    public RestyJsonSerializerGenerator findGenerator(JType type) {
        if (primitiveMappings != null && type.isPrimitive() != null) {
            return primitiveMappings.get(type.isPrimitive());
        } else if (classMappings != null && type.isClass() != null) {
            JClassType classType = getBaseType(type.isClass());
            RestyJsonSerializerGenerator generator = classMappings.get(classType);
            if (generator != null) {
                return generator;
            }
            for (JClassType curr = classType; (curr != null); curr = getBaseType(curr.getSuperclass())) {
                generator = classMappings.get(curr);
                if (generator != null) {
                    return generator;
                }
            }
        }
        return null;
    }

}
