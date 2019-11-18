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

// tag::add_fallback[]
package io.openliberty.guides.inventory;

import java.io.IOException;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.Fallback;
import io.openliberty.guides.inventory.model.*;

@ApplicationScoped
public class InventoryManager {

  private List<SystemData> systems = Collections.synchronizedList(new ArrayList<>());
  private InventoryUtils invUtils = new InventoryUtils();

  // tag::Fallback[]
  @Fallback(fallbackMethod = "fallbackForGet")
  // end::Fallback[]
  // tag::get[]
  public Properties get(String hostname) throws IOException {
    return invUtils.getProperties(hostname);
  }
  // end::get[]

  // tag::fallbackForGet[]
  public Properties fallbackForGet(String hostname) {
    Properties properties = findHost(hostname);
    if (properties == null) {
      Properties msgProp = new Properties();
      msgProp.setProperty(hostname, "System is not found in the inventory");
      return msgProp;
    }
    return properties;
  }
  // end::fallbackForGet[]

  public void add(String hostname, Properties systemProps) {
    Properties props = new Properties();
    props.setProperty("os.name", systemProps.getProperty("os.name"));
    props.setProperty("user.name", systemProps.getProperty("user.name"));
 
    SystemData system = new SystemData(hostname, props);
    if (!systems.contains(system)) {
      systems.add(system);
    }
  }

  public InventoryList list() {
    return new InventoryList(systems);
  }

  private Properties findHost(String hostname) {
    for (SystemData system : systems) {
      if (system.getHostname().equals(hostname)) {
        return system.getProperties();
      }
    }
    return null;
  }
}
// end::add_fallback[]
