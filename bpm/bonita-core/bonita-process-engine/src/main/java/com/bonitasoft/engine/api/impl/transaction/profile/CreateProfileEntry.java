/*******************************************************************************
 * Copyright (C) 2011 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.api.impl.transaction.profile;

import org.bonitasoft.engine.commons.exceptions.SBonitaException;
import org.bonitasoft.engine.commons.transaction.TransactionContentWithResult;
import org.bonitasoft.engine.profile.ProfileService;
import org.bonitasoft.engine.profile.model.SProfileEntry;

/**
 * @author Julien Mege
 */
public class CreateProfileEntry implements TransactionContentWithResult<SProfileEntry> {

    private final ProfileService profileService;

    private SProfileEntry profileEntry;

    public CreateProfileEntry(final ProfileService profileService, final SProfileEntry profileEntry) {
        super();
        this.profileService = profileService;
        this.profileEntry = profileEntry;
    }

    @Override
    public void execute() throws SBonitaException {
        this.profileEntry = this.profileService.createProfileEntry(this.profileEntry);
    }

    @Override
    public SProfileEntry getResult() {
        return this.profileEntry;
    }

}
