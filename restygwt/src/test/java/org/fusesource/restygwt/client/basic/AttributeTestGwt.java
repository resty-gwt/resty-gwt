package org.fusesource.restygwt.client.basic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import org.fusesource.restygwt.client.Attribute;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.RestServiceProxy;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @author Thomas Cybulski
 */
public class AttributeTestGwt extends GWTTestCase {

    private AttributeTestRestService service;

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.AttributeTestGwt";
    }

    interface AttributeTestRestService extends RestService {

        @POST
        @Path("/save")
        void savePublicAttribute(@Attribute("publicId") AttributeDTO instance, MethodCallback<AttributeDTO> callback);


        @POST
        @Path("/save")
        void savePrivateAttribute(@Attribute("privateId") AttributeDTO instance, MethodCallback<AttributeDTO> callback);
    }

    class AttributeDTOMethodCallback implements MethodCallback<AttributeDTO> {

        private final String path;

        AttributeDTOMethodCallback(String path) {
            this.path = path;
        }

        @Override
        public void onSuccess(Method method, AttributeDTO response) {

            assertEquals(response.getPath(), path);

            finishTest();

        }

        @Override
        public void onFailure(Method method, Throwable exception) {

            fail();

        }
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        service = GWT.create(AttributeTestRestService.class);
        Resource resource = new Resource(GWT.getModuleBaseURL() + "attribute");
        ((RestServiceProxy) service).setResource(resource);
    }


    public void testSaveWithInstance() {

        AttributeDTO dto = new AttributeDTO();
        dto.setPath("/save");

        service.savePublicAttribute(dto, new AttributeDTOMethodCallback("/save"));

    }

    public void testSaveWithNull() {

        service.savePublicAttribute(null, new MethodCallback<AttributeDTO>() {

            @Override
            public void onFailure(Method method, Throwable exception) {

                fail();

            }

            @Override
            public void onSuccess(Method method, AttributeDTO response) {

                assertFalse(response.getPath().contains("path"));

            }
        });

    }

    public void gwtTearDown() {

        // wait... we are in async testing...
        delayTestFinish(10000);

    }
}