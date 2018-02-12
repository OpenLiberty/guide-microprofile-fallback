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
package io.openliberty.guides.inventory.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SystemClient {

  // Constants for building URI to the system service.
  private static final int DEFAULT_PORT = Integer.valueOf(System.getProperty("default.http.port"));
  private static final String SYSTEM_PROPERTIES = "/system/properties";
  private static final String PROTOCOL = "http";
  private static final String SECURED_PROTOCOL = "https";

  private String url;
  private boolean status;
  private Properties content;

  /**
   * Used by the following guide(s): CDI, MP-METRICS, FAULT-TOLERANCE
   */
  public SystemClient(String hostname) throws IOException {
    init(PROTOCOL, hostname, DEFAULT_PORT, null);
  }

  /**
   * Used by the following guide(s): MP-CONFIG, MP-HEALTH
   */
  /*public SystemClient(String hostname, int port) throws IOException {
    init(PROTOCOL, hostname, port, null);
  }*/

  /**
   * Used by the following guide(s): MP-JWT
   */
  /*public SystemClient(String hostname, String authHeader) throws IOException {
    init(SECURED_PROTOCOL, hostname, DEFAULT_PORT, authHeader);
  }*/

  /**
   * Helper function to set the attributes.
   */
  private void init(String protocol, String hostname, int port, String authHeader) throws IOException {
    this.setUrl(protocol, hostname, port, SYSTEM_PROPERTIES);
    this.setStatus();
    if (this.status) {
      this.setContent(authHeader);
    }
  }

  /**
   * URL getter
   */
  public String getUrl() {
    return this.url;
  }

  /**
   * Status getter
   */
  public boolean isResponseOk() {
    return this.status;
  }

  /**
   * Content getter
   */
  public Properties getContent() {
    return this.content;
  }

  /**
   * <p>
   * Builds the URI string to the system service for a particular host. This is
   * just a helper method.
   * </p>
   *
   * @param protocol
   *          - http or https.
   * @param host
   *          - name of host.
   * @param port
   *          - port number.
   * @param path
   *          - Note that the path needs to start with a slash!!!
   * @return String representation of the URI to the system properties service.
   */
  private void setUrl(String protocol, String host, int port, String path) {
    try {
      URI uri = new URI(protocol, null, host, port, path, null, null);
      this.url = uri.toString();
    } catch (Exception e) {
      System.out.println("URISyntaxException");
      this.url = null;
    }
  }

  /**
   * Status setter
   */
  private void setStatus() throws IOException {
    URL target = new URL(this.url);
    HttpURLConnection http = (HttpURLConnection) target.openConnection();
    http.setConnectTimeout(50);
    int response = http.getResponseCode();
    if (response == 503) throw new IOException();
    this.status = (response != 200) ? false : true;
    /*try {
      URL target = new URL(this.url);
      HttpURLConnection http = (HttpURLConnection) target.openConnection();
      http.setConnectTimeout(50);
      int response = http.getResponseCode();
      this.status = (response != 200) ? false : true;
    } catch (Exception e) {
      this.status = false;
    }*/
  }

  /**
   * Content setter
   */
  private void setContent(String authHeader) {
    Client client = ClientBuilder.newClient();
    Builder builder = client.target(this.url).request();
    builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    if (authHeader != null) {
      builder.header(HttpHeaders.AUTHORIZATION, authHeader);
    }
    Response response = builder.build("GET").invoke();
    this.content = response.readEntity(Properties.class);
  }

}
