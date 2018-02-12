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

// tag::fault_tolerance[]
package io.openliberty.guides.inventory;

import java.util.Properties;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.openliberty.guides.inventory.model.InventoryList;

@RequestScoped
@Path("systems")
public class InventoryResource {

  @Inject InventoryManager manager;
  @Inject InventoryConfig config;
  
  @GET
  @Path("{hostname}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPropertiesForHost(@PathParam("hostname") String hostname) throws Exception {
    Properties props = manager.get(hostname);
    if (props == null) {
      return Response.status(Response.Status.NOT_FOUND)
                     .entity("ERROR: Unknown hostname or the resource may not be running on the host machine")
                     .build();
    }
    return Response.ok(props).build();
  }
  //may need to rework the Exception to be more specific.. 
  //also don't think we need to set/check if inventory service is in maintenancef for this guide's purpose

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public InventoryList listContents() {
    return manager.list();
  }

}
// end::fault_tolerance[]

