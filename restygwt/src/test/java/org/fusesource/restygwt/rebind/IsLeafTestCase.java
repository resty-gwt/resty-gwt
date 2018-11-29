package org.fusesource.restygwt.rebind;

import junit.framework.TestCase;

import org.junit.Test;

public class IsLeafTestCase extends TestCase {

    private interface IParent {
    }

    private interface ISon extends IParent {
    }

    private interface IGranSon extends ISon {
    }

    private abstract static class Parent implements IParent {
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
