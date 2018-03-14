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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FaultToleranceTest {

    private static String port;
    private static String baseUrl;
    private Response response;
    private Client client;

    private final String INVENTORY_LOCALHOST = "inventory/systems/localhost/";
    private final String SYSTEM_MAINTENANCE_FALSE = "io_openliberty_guides_system_inMaintenance\":false";
    private final String SYSTEM_MAINTENANCE_TRUE = "io_openliberty_guides_system_inMaintenance\":true";

    @BeforeClass
    public static void oneTimeSetup() {
        port = System.getProperty("liberty.test.port");
        baseUrl = "http://localhost:" + port + "/";
    }

    @Before
    public void setup() {
        client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);
    }

    @After
    public void teardown() {
        client.close();
        response.close();
        //changeSystemProperty(SYSTEM_MAINTENANCE_TRUE, SYSTEM_MAINTENANCE_FALSE);
    }

    @Test
    public void testSuite() throws InterruptedException {
        testFallbackForGet();
    }

    //tag::javadoc[]
    /**
     * testFallbackForGet - test for checking if the fallback is being called correctly
     * 1. Return system properties for a hostname when inventory service is available.
     * 2. Make System service down and get the system properties from inventory service
     *    when it is down.
     * 3. Check if system properties for the specific host was returned when the inventory
     *    service was down by:
     *    Asserting if the total number of the system properties, when service is up, is
     *    greater than the total number of the system properties when service is down.
     * @return {void}
     * @throws InterruptedException 
     */
    //end::javadoc[]
    public void testFallbackForGet() throws InterruptedException {
        response = this.getResponse(baseUrl + INVENTORY_LOCALHOST);
        assertResponse(baseUrl, response);
        JsonObject obj = response.readEntity(JsonObject.class);
        int propertiesSize = obj.size();
        changeSystemProperty(SYSTEM_MAINTENANCE_FALSE, SYSTEM_MAINTENANCE_TRUE);
        Thread.sleep(3000);
        response = this.getResponse(baseUrl + INVENTORY_LOCALHOST);
        assertResponse(baseUrl, response);
        obj = response.readEntity(JsonObject.class);
        int propertiesSizeFallBack = obj.size();
        assertTrue("The total number of properties from the @Fallback method is not smaller than the number from the system service, as expected.", propertiesSize > propertiesSizeFallBack);
        changeSystemProperty(SYSTEM_MAINTENANCE_TRUE, SYSTEM_MAINTENANCE_FALSE);
    }
    
    // tag::javadoc[]
    /**
     * Returns response information from the specified URL.
     * @param url
     * @return
     */
    // end::javadoc[]
    private Response getResponse(String url) {
        return client.target(url).request().get();
    }
    
    // tag::javadoc[]
    /**
     * Asserts that the given URL has the correct response code of 200.
     * @param url
     * @param response
     */
    // end::javadoc[]
    private void assertResponse(String url, Response response) {
        assertEquals("Incorrect response code from " + url, 200, response.getStatus());
    }
    
    // tag::javadoc[]
    /**
     * Changes the system property from old value to new value.
     * @param oldValue
     * @param newValue
     */
    // end::javadoc[]
    private void changeSystemProperty(String oldValue, String newValue) {
        try {
            String fileName = System.getProperty("user.dir").split("target")[0]
            + "/resource/CustomConfigSource.json";
            BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
            String line = "";
            String oldContent = "", newContent = "";
            while ((line = reader.readLine()) != null) {
                oldContent += line + "\r\n";
            }
            reader.close();
            newContent = oldContent.replaceAll(oldValue, newValue);
            FileWriter writer = new FileWriter(fileName);
            writer.write(newContent);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
// end::ft_testing[]
