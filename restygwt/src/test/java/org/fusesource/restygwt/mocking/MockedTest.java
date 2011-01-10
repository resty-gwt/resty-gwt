package org.fusesource.restygwt.mocking;

import junit.framework.TestCase;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.basic.ExampleDto;
import org.fusesource.restygwt.client.basic.ExampleService;
import org.junit.Before;

import com.google.gwt.junit.GWTMockUtilities;

/**
 * Just a real world usability example how services can be mocked in a simple manner.
 * WITHOUT using GwtTestCases that tend to be damn slow.
 *
 * In principle - you just have to implement the interface - right?
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</<a>
 *
 */
public class MockedTest extends TestCase {

    @Before
    public void setUp() throws Exception {
        GWTMockUtilities.disarm();
    }

    public void testMockedDispatcher() {

        /** Mock/Stub mocked example service: */
        ExampleService exampleService = new ExampleService() {
            @Override
            public void getExampleDto(MethodCallback<ExampleDto> callback) {
                ExampleDto exampleDto = new ExampleDto();
                exampleDto.name = "name";
               callback.onSuccess(null, exampleDto);
            }
        };

        /** test*/
        exampleService.getExampleDto(new MethodCallback<ExampleDto>() {
            @Override
            public void onSuccess(Method method, ExampleDto response) {
                assertEquals(response.name, "name");

            }

            @Override
            public void onFailure(Method method, Throwable exception) {
            }
        });

    }


}
