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

// tag::503_response[]
package io.openliberty.guides.system;

// JAX-RS
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

// JSON-P
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

// CDI
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import io.openliberty.guides.common.JsonMessages;
import io.openliberty.guides.system.SystemConfig;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("properties")
public class PropertiesResource {

  @Inject
  SystemConfig systemConfig;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getProperties() {
    if (!systemConfig.isInMaintenance()) {
      JsonObjectBuilder builder = Json.createObjectBuilder();

      System.getProperties().entrySet().stream()
            .forEach(entry -> builder.add((String) entry.getKey(),
                                          (String) entry.getValue()));

      return Response.ok(builder.build()).build();
    } else {
      System.out.println("The system is not working!");
      return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
    }

  }

}
// end::503_response[]