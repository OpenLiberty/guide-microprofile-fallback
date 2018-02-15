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

package io.openliberty.guides.inventory;

import javax.enterprise.context.RequestScoped;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Path("retries")
public class InventoryRetry {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject getRetries() {
    return InventoryManager.getRetryCounter();
  }
  
  @GET
  @Path("reset")
  @Produces(MediaType.TEXT_PLAIN)
  public String resetRetryCounter() {
    InventoryManager.resetRetryCounter();
    return "Counters have been reset";
  }

}