package org.fusesource.restygwt.rebind;

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import org.fusesource.restygwt.rebind.JsonEncoderDecoderClassCreator.Subtype;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;

@RunWith(JUnit4.class)
public class JsonEncoderDecoderClassCreatorTestCase extends TestCase
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static class MyPossibleTypesVisitor extends PossibleTypesVisitor {

        boolean hasEnteredGetPossibleTypesForClass;
        boolean hasEnteredGetPossibleTypesForOther;

        public MyPossibleTypesVisitor(GeneratorContext context, JClassType classType, boolean isLeaf,
                TreeLogger logger, Collection<JsonSubTypes.Type> types)
        {
            super(context, classType, isLeaf, logger, types);
        }

        @Override
        protected List<Subtype> getPossibleTypesForClass(GeneratorContext context, JClassType classType, Id id,
                boolean isLeaf, TreeLogger logger, Collection<JsonSubTypes.Type> types) throws UnableToCompleteException
        {
            hasEnteredGetPossibleTypesForClass = true;
            return null;
        }

        @Override
        protected List<Subtype> getPossibleTypesForOther(GeneratorContext context, JClassType classType,
                boolean isLeaf, TreeLogger logger, Collection<JsonSubTypes.Type> types) throws UnableToCompleteException
        {
            hasEnteredGetPossibleTypesForOther = true;
            return null;
        }

    }

    @Test
    public void testGetPossibleTypesForClass() throws Throwable {
        check(Id.CLASS, true, false);
        check(Id.MINIMAL_CLASS, true, false);
        check(Id.CUSTOM, false, true);
        check(Id.NAME, false, true);
    }

    @Test(expected=UnableToCompleteException.class)
    public void testGetPossibleTypesForClass2() throws Throwable {
        check(Id.NONE, false, true);
    }

    private void check(final Id id, final boolean classMethodVisited, final boolean otherMethodVisited)
            throws Throwable
    {
        MyPossibleTypesVisitor v = new MyPossibleTypesVisitor(null, null, true, new TreeLogger(){
            @Override
            public void log(Type type, String msg, Throwable caught, HelpInfo helpInfo){
            }

            @Override
            public boolean isLoggable(Type type){
                return false;
            }

            @Override
            public TreeLogger branch(Type type, String msg, Throwable caught, HelpInfo helpInfo){
                return null;
            }
        }, null);
        v.visit(id);
        assertEquals(v.hasEnteredGetPossibleTypesForClass, classMethodVisited);
        assertEquals(v.hasEnteredGetPossibleTypesForOther, otherMethodVisited);
    }
}
