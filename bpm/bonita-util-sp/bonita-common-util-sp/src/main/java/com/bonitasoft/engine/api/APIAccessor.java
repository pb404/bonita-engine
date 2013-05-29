/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.api;

import org.bonitasoft.engine.api.CommandAPI;

/**
 * @author Matthieu Chaffotte
 * @author Celine Souchet
 */
public interface APIAccessor extends org.bonitasoft.engine.api.APIAccessor {

    @Override
    IdentityAPI getIdentityAPI();

    @Override
    ProcessAPI getProcessAPI();

    MonitoringAPI getMonitoringAPI();

    PlatformMonitoringAPI getPlatformMonitoringAPI();

    LogAPI getLogAPI();

    NodeAPI getNodeAPI();

    @Override
    CommandAPI getCommandAPI();

    @Override
    ReportingAPI getReportingAPI();

    @Override
    ProfileAPI getProfileAPI();

}
