package org.fusesource.restygwt.rebind;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JEnumConstant;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;

class DummyJField implements JField {

    private final JType type;
    private final String name;
    private final JMethod getterMethod;
    private Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<Class<? extends Annotation>, Annotation>();
    
    DummyJField( String name, JType type, JMethod getterMethod){
        this.name = name;
        this.type = type;
        this.getterMethod = getterMethod;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return (T) annotations.get(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return annotations.values().toArray(new Annotation[0]);
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return null;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return annotations.containsKey(annotationClass);
    }

    @Override
    public String[][] getMetaData(String tagName) {
        return null;
    }

    @Override
    public String[] getMetaDataTags() {
        return null;
    }

    @Override
    public JClassType getEnclosingType() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JType getType() {
        return type;
    }

    @Override
    public boolean isDefaultAccess() {
        return true;
    }

    @Override
    public JEnumConstant isEnumConstant() {
        return null;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isPublic() {
        return false;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public boolean isVolatile() {
        return false;
    }

    public <T extends Annotation> void setAnnotation(T annotation) {
        annotations.put( annotation.annotationType(), annotation ); 
    }

    public JMethod getGetterMethod()
    {
        return getterMethod;
    }
    
}