// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018, 2019 IBM Corporation and others.
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

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);
    }

    @AfterEach
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
    @Test
    public void testFallbackForGet() throws InterruptedException {
        response = TestUtils.getResponse(client,
                                         TestUtils.INVENTORY_LOCALHOST_URL);
        assertResponse(TestUtils.baseUrl, response);
        JsonObject obj = response.readEntity(JsonObject.class);
        int propertiesSize = obj.size();
        TestUtils.changeSystemProperty(TestUtils.SYSTEM_MAINTENANCE_FALSE,
                                       TestUtils.SYSTEM_MAINTENANCE_TRUE);
        Thread.sleep(3000);
        response = TestUtils.getResponse(client,
                                         TestUtils.INVENTORY_LOCALHOST_URL);
        assertResponse(TestUtils.baseUrl, response);
        obj = response.readEntity(JsonObject.class);
        int propertiesSizeFallBack = obj.size();
        assertTrue(propertiesSize > propertiesSizeFallBack,
                   "The total number of properties from the @Fallback method "
                 + "should be smaller than the number from the system service.");
        TestUtils.changeSystemProperty(TestUtils.SYSTEM_MAINTENANCE_TRUE,
                                       TestUtils.SYSTEM_MAINTENANCE_FALSE);
        Thread.sleep(3000);
    }

    // tag::javadoc[]
    /**
     * Asserts that the given URL has the correct response code of 200.
     */
    // end::javadoc[]
    private void assertResponse(String url, Response response) {
        assertEquals(200, response.getStatus(), "Incorrect response code from " + url);
    }
}
// end::ft_testing[]
