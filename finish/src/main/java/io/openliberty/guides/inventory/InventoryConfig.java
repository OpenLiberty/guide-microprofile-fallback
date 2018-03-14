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
import javax.inject.Inject;
import javax.inject.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
public class InventoryConfig {

    @Inject
    @ConfigProperty(name = "io_openliberty_guides_inventory_inMaintenance")
    private Provider<Boolean> inMaintenance;

    public boolean isInvInMaintenance() {
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inMaintenance.get();
    }
}