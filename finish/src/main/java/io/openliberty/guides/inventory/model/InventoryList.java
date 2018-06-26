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
import java.util.HashMap;
import java.util.Map;

public class InventoryList {

    private HashMap<String, Properties> systems = new HashMap<String, Properties>();

    public HashMap<String, Properties> getSystems() {
        return systems;
    }

    public int getTotal() {
        return systems.size();
    }

    public void addToInventoryList(String hostname, Properties systemProps) {
        Properties props = new Properties();
        props.setProperty("os.name", systemProps.getProperty("os.name"));
        props.setProperty("user.name", systemProps.getProperty("user.name"));
        systems.put(hostname, props);
    }

    public Properties findHost(String hostname) {
    	if (systems.containsKey(hostname)){
    		return systems.get(hostname);
    	}
    	return null;
    }

}
