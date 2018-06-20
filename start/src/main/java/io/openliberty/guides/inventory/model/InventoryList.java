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
package io.openliberty.guides.inventory.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class InventoryList {

  private List<SystemEntry> systems = new ArrayList<SystemEntry>();

  public List<SystemEntry> getSystems() {
    return systems;
  }

  public int getTotal() {
    return systems.size();
  }

  public void addToInventoryList(String hostname, Properties systemProps) {
    Properties props = new Properties();
    props.setProperty("os.name", systemProps.getProperty("os.name"));
    props.setProperty("user.name", systemProps.getProperty("user.name"));

    SystemEntry host = new SystemEntry(hostname, props);
    if (!systems.contains(host))
      systems.add(host);
  }

  public Properties findHost(String hostname) {
    for (SystemEntry system : systems) {
      if (system.getHostname().equals(hostname)) {
        return system.getProperties();
      }
    }
    return null;
  }

  class SystemEntry {

    private final String hostname;
    private final Properties properties;

    public SystemEntry(String hostname, Properties properties) {
      this.hostname = hostname;
      this.properties = properties;
    }

    public String getHostname() {
      return hostname;
    }

    public Properties getProperties() {
      return properties;
    }

    @Override
    public boolean equals(Object host) {
      if (host instanceof SystemEntry) {
        return hostname.equals(((SystemEntry) host).getHostname());
      }
      return false;
    }
  }
}