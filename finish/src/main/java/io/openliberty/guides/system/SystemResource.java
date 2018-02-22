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
// tag::503_response[]
package io.openliberty.guides.system;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Properties;
import org.eclipse.microprofile.config.Config;

@RequestScoped
@Path("properties")
public class SystemResource {
    @Inject
    Config config;

    @Inject
    SystemConfig systemConfig;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProperties() {
        if (!systemConfig.isInMaintenance()) {
            Properties props = new Properties();
            Iterable<String> names = config.getPropertyNames();
            for (String name:names){
                String value = config.getValue(name, String.class);
                props.put(name, value);
            }
            return Response.ok(props).build();
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
    }
}
// end::503_response[]