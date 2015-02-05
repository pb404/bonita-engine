/**
 * Copyright (C) 2015 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.data.instance.model.builder.impl;

import org.bonitasoft.engine.data.instance.model.builder.SDataInstanceLogBuilder;
import org.bonitasoft.engine.queriablelogger.model.SQueriableLog;
import org.bonitasoft.engine.queriablelogger.model.builder.SPersistenceLogBuilder;
import org.bonitasoft.engine.queriablelogger.model.builder.impl.CRUDELogBuilder;
import org.bonitasoft.engine.queriablelogger.model.builder.impl.MissingMandatoryFieldsException;

public class SDataInstanceLogBuilderImpl extends CRUDELogBuilder implements SDataInstanceLogBuilder {

    private static final String DATA_INSTANCE = "DATA_INSTANCE";

    private static int DATA_INSTANCE_INDEX = 0;

    @Override
    public SPersistenceLogBuilder objectId(final long objectId) {
        queriableLogBuilder.numericIndex(DATA_INSTANCE_INDEX, objectId);
        return this;
    }

    @Override
    protected String getActionTypePrefix() {
        return DATA_INSTANCE;
    }

    @Override
    protected void checkExtraRules(final SQueriableLog log) {
        if (log.getActionStatus() != SQueriableLog.STATUS_FAIL && log.getNumericIndex(DATA_INSTANCE_INDEX) == 0L) {
            throw new MissingMandatoryFieldsException("Some mandatoryFildes are missing: " + "data instance Id");
        }
    }

}
