/*******************************************************************************
 * Copyright (C) 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.api;

import org.bonitasoft.engine.exception.BonitaRuntimeException;

/**
 * 
 * occurs when we try to login with an other user than the technical user on a tenant that is in maintenance
 * 
 * @author Emmanuel Duchastenier
 */
public class TenantInMaintenanceException extends BonitaRuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *            the exception message
     */
    public TenantInMaintenanceException(final String message) {
        super(message);
    }

}
