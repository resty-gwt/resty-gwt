package org.fusesource.restygwt.server.basic;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.fusesource.restygwt.client.basic.AttributeDTO;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * just echos back the request path and the request parameters.
 *
 * @author Thomas Cybulski
 */
public class AttributeTestGwtServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doAttributeRequest(request, response);
    }

    @SuppressWarnings("unchecked")
    private void doAttributeRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AttributeDTO dto = new AttributeDTO();
        dto.setPath(request.getPathInfo());

        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json");
        mapper.writeValue(response.getOutputStream(), dto);
    }

}