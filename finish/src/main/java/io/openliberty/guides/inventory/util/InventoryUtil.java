// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
 // end::copyright[]
package io.openliberty.guides.inventory.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

// JSON-P
import javax.json.JsonObject;

// JAX-RS
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;

import java.io.IOException;

public class InventoryUtil {

    // Constants for building URI to the system service.
    private static final int DEFAULT_PORT = 9080;
    private static final String PROTOCOL = "http";
    private static final String SYSTEM_PROPERTIES = "/system/properties";

    

    /**
     * <p>Builds the URI to the system service for a particular host. This is just a helper method.</p>
     *
     * @param hostname - name of host.
     * @return URI object representation of the URI to the system properties service.
     */
    private static URI buildUri(String hostname, int port) {
        System.out.println("Building the URI");
        return UriBuilder.fromUri(SYSTEM_PROPERTIES)
                         .host(hostname)
                         .port(port)
                         .scheme(PROTOCOL)
                         .build();
    }
    /**
     * <p>Returns whether or not a particular host is running the system service on the
     * given port number.</p>
     */
    private static boolean responseOkHelper(String hostname, int port) {
        try {
            URL target = new URL(buildUri(hostname, port).toString());
            HttpURLConnection http = (HttpURLConnection) target.openConnection();
            http.setConnectTimeout(50);
            int response = http.getResponseCode();
            System.out.println("Grace this is the response" +response);
            return (response != 200) ? false : true;
        } catch (Exception e) {
            System.out.println("Exception caused by non-200 response being sent");
            return false;
        }
    }
    /**
     * <p>Returns whether or not a particular host is exposing its JVM's system properties.
     * In other words, returns whether or not the system service is running on a
     * particular host.</p>
     *
     * @param hostname - name of host.
     * @return true if the host is currently running the system service and false otherwise.
     */
    public static boolean responseOk(String hostname) {
        System.out.println("ResponseOK being called");
        return responseOkHelper(hostname, DEFAULT_PORT);

    }
    /**
     * <p>Returns whether or not a particular host is exposing its JVM's system properties on the
     * given port number. In other words, returns whether or not the system service is running on a
     * particular host on the given port number.</p>
     *
     * @param hostname - name of host.
     * @param port     - port number.
     * @return true if the host is currently running the system service and false otherwise.
     */
    public static boolean responseOk(String hostname, int port) {
        System.out.println("ResponseOK for both hostname and port being called");
        return responseOkHelper(hostname, port);
    }

    














    /**
     * <p>Creates a JAX-RS client that retrieves the JVM system properties for the particular host
     * on the given port number.</p>
     */
    private static JsonObject getPropertiesHelper(String hostname, int port) throws IOException{
        if(serverUnavailableHelper(hostname, port) == true){
            Client client = ClientBuilder.newClient();
            URI propURI = InventoryUtil.buildUri(hostname, port);
            System.out.println("This is the builder for the properties");
            return client.target(propURI).request().get(JsonObject.class);        }
        else{
            System.out.println("FALSE GET PROPERTIES");
            throw new IOException();
        }
    }
    /**
     * <p>Retrieves the JVM system properties of a particular host.</p>
     *
     * <p>NOTE: the host must expose its JVM's system properties via the
     * system service; ie. the system service must be running on that host.</p>
     *
     * @param hostname - name of host.
     * @return JSON Java object containing the system properties of the host's JVM.
     */
    public static JsonObject getProperties(String hostname) throws IOException{
        System.out.println("GetProperties being called");
        return getPropertiesHelper(hostname, DEFAULT_PORT);
    }
    /**
     * <p>Retrieves the JVM system properties of a particular host for the given port number.</p>
     *
     * <p>NOTE: the host must expose its JVM's system properties via the
     * system service; ie. the system service must be running on that host.</p>
     *
     * @param hostname - name of host.
     * @param port     - port number for the system service.
     * @return JSON Java object containing the system properties of the host's JVM.
     */
    public static JsonObject getProperties(String hostname, int port) throws IOException{
        System.out.println("GetProperties with port being called");
        return getPropertiesHelper(hostname, port);
    }












    /**
     * <p>Returns whether or not a particular host is running the system service on the
     * given port number.</p>
     */
    private static boolean serverUnavailableHelper(String hostname, int port) {
        try {
            URL target = new URL(buildUri(hostname, port).toString());
            HttpURLConnection http = (HttpURLConnection) target.openConnection();
            http.setConnectTimeout(50);
            int response = http.getResponseCode();
            if(response == 503) {
                System.out.println("serverUnavailable returning false - 503 being returned");
                return false;
            }
            else {
                System.out.println("serverUnavailable returning true - 503 NOT being returned");
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }



}
