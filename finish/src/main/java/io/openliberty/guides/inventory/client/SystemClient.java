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
// tag::throw_IOException[]
package io.openliberty.guides.inventory.client;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class SystemClient {

    // tag::javadoc[]
    /**
     * Constants for building URI to the system service.
     */
    // end::javadoc[]
    private final int DEFAULT_PORT = Integer.valueOf(System.getProperty("default.http.port"));
    private final String SYSTEM_PROPERTIES = "/system/properties";
    private final String PROTOCOL = "http";

    private String url;
    private Builder clientBuilder;

    public void init(String hostname) {
        this.initHelper(hostname, DEFAULT_PORT);
    }

    public void init(String hostname, int port) {
        this.initHelper(hostname, port);
    }

    // tag::javadoc[]
    /**
     * Helper method to set the attributes.
     * 
     * @param hostname
     * @param port
     */
    // end::javadoc[]
    private void initHelper(String hostname, int port) {
        this.url = buildUrl(PROTOCOL, hostname, port, SYSTEM_PROPERTIES);
        this.clientBuilder = buildClientBuilder(this.url);
    }

    // tag::javadoc[]
    /**
     * Wrapper function that gets properties
     * 
     * @return
     * @throws IOException
     */
    // end::javadoc[]
    public Properties getProperties() throws IOException {
        return getPropertiesHelper(this.clientBuilder);
    }

    // tag::javadoc[]
    /**
     * Builds the URI string to the system service for a particular host.
     * 
     * @param protocol
     *            - http or https.
     * @param host
     *            - name of host.
     * @param port
     *            - port number.
     * @param path
     *            - Note that the path needs to start with a slash!!!
     * @return String representation of the URI to the system properties
     *         service.
     */
    // end::javadoc[]
    protected String buildUrl(String protocol, String host, int port,
            String path) {
        try {
            URI uri = new URI(protocol, null, host, port, path, null, null);
            return uri.toString();
        } catch (Exception e) {
            System.err.println("Exception thrown while building the URL: "
                    + e.getMessage());
            return null;
        }
    }

    // tag::javadoc[]
    /**
     * Method that creates the client builder
     * 
     * @param urlString
     * @return
     */
    // end::javadoc[]
    protected Builder buildClientBuilder(String urlString) {
        try {
            Client client = ClientBuilder.newClient();
            Builder builder = client.target(urlString).request();
            return builder.header(HttpHeaders.CONTENT_TYPE,
                                  MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            System.err.println("Exception thrown while building the client: "
                    + e.getMessage());
            return null;
        }
    }

    // tag::javadoc[]
    /**
     * Helper method that processes the request
     * 
     * @param builder
     * @return
     * @throws IOException
     */
    // end::javadoc[]
    protected Properties getPropertiesHelper(Builder builder)
            throws IOException {
        try {
            Response response = builder.get();
            int status = response.getStatus();
            if (status == Status.SERVICE_UNAVAILABLE.getStatusCode()) {
                throw new IOException();
            } else if (status == Status.OK.getStatusCode()) {
                return response.readEntity(Properties.class);
            } else {
                System.err.println("Response Status is not OK.");
            }
        } catch (RuntimeException e) {
            System.err.println("Runtime exception: " + e.getMessage());
        }
        return null;
    }
}
// end::throw_IOException[]