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
package io.openliberty.guides.inventory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// CDI
import javax.enterprise.context.ApplicationScoped;

// JSON-P
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import io.openliberty.guides.inventory.util.InventoryUtil;
import io.openliberty.guides.common.JsonMessages;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.eclipse.microprofile.faulttolerance.*;

@ApplicationScoped
public class InventoryManager {

  private ConcurrentMap<String, JsonObject> inv = new ConcurrentHashMap<>();

    @Retry(retryOn=IOException.class, maxRetries=3)
    @Fallback(fallbackMethod= "fallbackForGet")
    public JsonObject get(String hostname) throws IOException{
        try{
            JsonObject properties = InventoryUtil.getProperties(hostname);
            inv.putIfAbsent(hostname, properties);
            System.out.println("SUCCESS! You have connected to your microservice!");
            return properties;
        } catch (NullPointerException e) {
            e.printStackTrace();     
        }
        System.out.println("Is try catch working?");
        return JsonMessages.SERVICE_UNREACHABLE.getJson();
    }

    
    public JsonObject fallbackForGet(String hostname) {
        JsonObject properties = inv.get(hostname);
        if (properties == null) {
            System.out.println("This is the Fallback method being called!!!");
            return JsonMessages.SERVICE_UNREACHABLE.getJson();        }
        return properties;
    }


  public JsonObject list() {
    JsonObjectBuilder systems = Json.createObjectBuilder();
    inv.forEach((host, props) -> {
      JsonObject systemProps = Json.createObjectBuilder()
                                   .add("os.name", props.getString("os.name"))
                                   .add("user.name",
                                        props.getString("user.name"))
                                   .build();
      systems.add(host, systemProps);
    });
    systems.add("hosts", systems);
    systems.add("total", inv.size());
    return systems.build();
  }
}
