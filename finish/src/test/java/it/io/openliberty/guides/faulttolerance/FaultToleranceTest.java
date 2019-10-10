// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.io.openliberty.guides.utils.TestUtils;

public class FaultToleranceTest {

    private Response response;
    private Client client;
    // tag::Before[]
    @Before
    // end::Before[]
    public void setup() {
        client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);
    }

    // tag::After[]
    @After
    // end::After[]
    public void teardown() {
        client.close();
        response.close();
    }

    // tag::javadoc1[]
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
    // end::javadoc1[]

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
        assertTrue("The total number of properties from the @Fallback method "
                + "is not smaller than the number from the system service, as expected.",
                   propertiesSize > propertiesSizeFallBack);
        // tag::changeSystemProperty2[]
        TestUtils.changeSystemProperty(TestUtils.SYSTEM_MAINTENANCE_TRUE,
                                       TestUtils.SYSTEM_MAINTENANCE_FALSE);
        // end::changeSystemProperty2[]
    }
    // end::testFallbackForGet[]

    // tag::javadoc2[]
    /**
     * Asserts that the given URL has the correct response code of 200.
     */
    // end::javadoc2[]
    private void assertResponse(String url, Response response) {
        assertEquals("Incorrect response code from " + url, 200,
                     response.getStatus());
    }
}
// end::ft_testing[]
