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
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import io.openliberty.guides.common.JsonMessages;
import io.openliberty.guides.inventory.client.SystemClient;
import io.openliberty.guides.inventory.model.InventoryList;

@ApplicationScoped
public class InventoryManager {

  private InventoryList invList = new InventoryList();

  @Retry(retryOn = IOException.class, maxRetries = 3)
  @Fallback(fallbackMethod = "fallbackForGet")
  public Properties get(String hostname) throws IOException {
    SystemClient systemClient = new SystemClient(hostname);
    if (systemClient.isResponseOk()) {
      Properties properties = systemClient.getContent();
      invList.addToInventoryList(hostname, properties);
      return properties;
    }
    return null;
  }

  public Properties fallbackForGet(String hostname) {
    Properties properties = invList.findHost(hostname);
    if (properties == null) {
      System.out.println("This is the Fallback method being called!!!");
      // return JsonMessages.SERVICE_UNREACHABLE.getJson(); - incorrect
      JsonMessages.serviceInMaintenance("system"); //need to fix this msg
    }
    return properties;
  }

  public InventoryList list() {
    return invList;
  }
}
// end::add_retry_fallback[]
