// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2022 IBM Corporation and others.
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

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@RequestScoped
@Path("properties")
public class SystemResource {

  @Inject
  SystemConfig systemConfig;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getProperties() {
    // tag::isInMaintenance[]
    if (!systemConfig.isInMaintenance()) {
      return Response.ok(System.getProperties()).build();
    // end::isInMaintenance[]
    } else {
    // tag::response-status[]
      return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
    // end::response-status[]
    }
  }
}
// end::503_response[]
