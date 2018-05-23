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
// tag::mapper[]
package io.openliberty.guides.inventory.client;

import java.io.IOException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

@Provider
public class ExceptionMapper implements ResponseExceptionMapper<Exception> {

	@Override
	public boolean handles(int status, MultivaluedMap<String, Object> headers) {
		return status == 404 // UnknownURLException
				|| status == Status.SERVICE_UNAVAILABLE.getStatusCode(); // Fallback Exception
	}

	@Override
	public Exception toThrowable(Response response) {
		switch (response.getStatus()) {
		case 404:
			return new UnknownUrlException();
		case Status.SERVICE_UNAVAILABLE.getStatusCode():
			return new IOException();
		}
		return null;
	}
}
// end::mapper[]
