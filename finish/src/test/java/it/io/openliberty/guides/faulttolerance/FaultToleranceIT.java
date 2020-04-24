// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
// tag::ft_testing[]
package it.io.openliberty.guides.faulttolerance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.io.openliberty.guides.utils.TestUtils;

public class FaultToleranceIT {

    private Response response;
    private Client client;

    // tag::Before[]
    @BeforeEach
    // end::Before[]
    public void setup() {
        client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);
    }

    // tag::After[]
    @AfterEach
    // end::After[]
    public void teardown() {
        client.close();
        response.close();
    }
    // tag::javadoc[]
    /**
     * testFallbackForGet - test for checking if the fallback is being called
     * correctly 1. Return system properties for a hostname when inventory
     * service is available. 2. Make System service down and get the system
     * properties from inventory service when it is down. 3. Check if system
     * properties for the specific host was returned when the inventory service
     * was down by: Asserting if the total number of the system properties, when
     * service is up, is greater than the total number of the system properties
     * when service is down.
     */
    // end::javadoc[]

    // tag::Test[]
    @Test
    // end::Test[]
    // tag::testFallbackForGet[]
    public void testFallbackForGet() throws InterruptedException {
        response = TestUtils.getResponse(client,
                                         TestUtils.INVENTORY_LOCALHOST_URL);
        assertResponse(TestUtils.baseUrl, response);
        JsonObject obj = response.readEntity(JsonObject.class);
        int propertiesSize = obj.size();
        // tag::changeSystemProperty1[]
        TestUtils.changeSystemProperty(TestUtils.SYSTEM_MAINTENANCE_FALSE,
                                       TestUtils.SYSTEM_MAINTENANCE_TRUE);
        // end::changeSystemProperty1[]
        Thread.sleep(3000);
        response = TestUtils.getResponse(client,
                                         TestUtils.INVENTORY_LOCALHOST_URL);
        assertResponse(TestUtils.baseUrl, response);
        obj = response.readEntity(JsonObject.class);
        int propertiesSizeFallBack = obj.size();
        assertTrue(propertiesSize > propertiesSizeFallBack, 
                   "The total number of properties from the @Fallback method "
                 + "is not smaller than the number from the system service" 
                 +  "as expected.");
        // tag::changeSystemProperty2[]
        TestUtils.changeSystemProperty(TestUtils.SYSTEM_MAINTENANCE_TRUE,
                                       TestUtils.SYSTEM_MAINTENANCE_FALSE);
        // end::changeSystemProperty2[]
        Thread.sleep(3000);
    }
    // end::testFallbackForGet[]

    // tag::javadoc[]
    /**
     * testFallbackForGet - test for checking if the fallback skip mechanism is working as intended:
     * 1. Access system properties for the wrong hostname (localhot)
     * 2. Verify that the response code is 404
     * 3. Verify that the response text is "ERROR: Unknown host"
     */
    // end::javadoc[]
    // tag::Test[]
    @Test
    // end::Test[]
    // tag::testFallbackSkipForGet[]
    public void testFallbackSkipForGet() {
        response = TestUtils.getResponse(client,
                TestUtils.INVENTORY_UNKNOWN_HOST_URL);
        assertResponse(TestUtils.baseUrl, response, 404);
        assertEquals("ERROR: Unknown host", response.readEntity(String.class),
                "Incorrect response body from " + TestUtils.INVENTORY_UNKNOWN_HOST_URL);
    }

    // tag::javadoc[]
    /**
     * Asserts that the given URL's response code matches the given status code.
     */
    // end::javadoc[]
    private void assertResponse(String url, Response response, int status_code) {
        assertEquals(status_code, response.getStatus(), "Incorrect response code from " + url);
    }

    // tag::javadoc[]
    /**
     * Asserts that the given URL has the correct response code of 200.
     */
    // end::javadoc[]
    private void assertResponse(String url, Response response) {
        assertResponse(url, response, 200);
    }
}
// end::ft_testing[]
