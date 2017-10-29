package org.fusesource.restygwt.rebind;

import junit.framework.TestCase;

import org.junit.Test;

public class IsLeafTestCase extends TestCase {

    private static interface IParent {
    }

    ;

    private static interface ISon extends IParent {
    }

    private static interface IGranSon extends ISon {
    }

    private static abstract class Parent implements IParent {
    }

    private static class Son extends Parent implements ISon {
    }

    @SuppressWarnings("unused")
    private static class GranSon extends Son implements IGranSon {
    }

    @Test
    public void test() throws Exception {
        //TOTO @rqu to be completed
    }
}
