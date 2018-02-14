// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]

// tag::add_retry_fallback[]
package io.openliberty.guides.inventory;

import java.io.IOException;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import io.openliberty.guides.inventory.client.SystemClient;
import io.openliberty.guides.inventory.model.InventoryList;
import io.openliberty.guides.system.SystemConfig;

@ApplicationScoped
public class InventoryManager {

  private InventoryList invList = new InventoryList();
  private SystemClient systemClient = new SystemClient();
  private static int retryCounter = 0;
  
  @Inject SystemConfig systemConfig;

  @Retry(retryOn = IOException.class, maxRetries = 3)
  @Fallback(fallbackMethod = "fallbackForGet")
  public Properties get(String hostname) throws IOException {
    if (systemConfig.isInMaintenance()) {
      retryCounter++;
    }
    
    systemClient.init(hostname);
    Properties properties = systemClient.getProperties();
    if (properties != null) {
        invList.addToInventoryList(hostname, properties);
        return properties;
    }
    return null;
  }

  public Properties fallbackForGet(String hostname) {
    Properties properties = invList.findHost(hostname);
    if (properties == null) {
      Properties msgProp = new Properties();
      msgProp.setProperty(hostname, "System is not found in the inventory");
      return msgProp;
    }
    return properties;
  }

  public InventoryList list() {
    return invList;
  }

  public static JsonObject getRetryCounter() {
    JsonObjectBuilder methods = Json.createObjectBuilder();
    methods.add("getRetryCounter", retryCounter);
    JsonObjectBuilder retries = Json.createObjectBuilder();
    retries.add("Inventory", methods.build());
    return retries.build();
  }
    
  public static void resetRetryCounter() {
    retryCounter = 0;
  }
}
// tag::add_retry_fallback[]
