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
package io.openliberty.guides.rest.inventory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// CDI
import javax.enterprise.context.ApplicationScoped;

// JSON-P
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import io.openliberty.guides.rest.util.InventoryUtil;
import io.openliberty.guides.rest.util.JsonMessages;

@ApplicationScoped
public class InventoryManager {

    private ConcurrentMap<String, JsonObject> inv = new ConcurrentHashMap<>();
    
    @Retry(retryOn = Exception.class, maxRetries = 2, delay = 2000)
    @Fallback(fallbackMethod= "fallbackForGet")
    public JsonObject get(String hostname) throws Exception{
        System.out.println(++counter);
        if (InventoryUtil.responseOk(hostname)) {
            System.out.println("response OK");
            JsonObject properties = InventoryUtil.getProperties(hostname);
            inv.putIfAbsent(hostname, properties);
            return properties;
        } else {
            System.out.println("response not ok");
            throw new Exception("cannot connect");
        }
    }
    public JsonObject fallbackForGet(String hostname) {
        System.out.println("fallback");
        JsonObject properties = inv.get(hostname);
        if (properties == null) {
            return JsonMessages.SERVICE_UNREACHABLE.getJson();
        }
        return properties;
    }

    public JsonObject list() {
        JsonObjectBuilder systems = Json.createObjectBuilder();
        inv.forEach((host, props) -> {
            JsonObject systemProps = Json.createObjectBuilder()
                                              .add("os.name", props.getString("os.name"))
                                              .add("user.name", props.getString("user.name"))
                                              .build();
            systems.add(host, systemProps);
        });
        systems.add("hosts", systems);
        systems.add("total", inv.size());
        return systems.build();
    }
}
