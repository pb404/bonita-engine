/*
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
 */
package org.bonitasoft.engine.page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.bonitasoft.engine.commons.exceptions.SExecutionException;
import org.bonitasoft.engine.core.process.instance.api.ActivityInstanceService;
import org.bonitasoft.engine.core.process.instance.model.SProcessInstance;
import org.bonitasoft.engine.core.process.instance.model.archive.SAHumanTaskInstance;
import org.bonitasoft.engine.core.process.instance.model.archive.SAUserTaskInstance;
import org.bonitasoft.engine.persistence.QueryOptions;
import org.bonitasoft.engine.persistence.SBonitaReadException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * author Emmanuel Duchastenier
 */
@RunWith(MockitoJUnitRunner.class)
public class IsTaskPerformerRuleTest extends RuleTest {

    @Mock
    ActivityInstanceService activityInstanceService;

    @InjectMocks
    IsTaskPerformerRule rule;

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfUserIdParamNotPresent() throws Exception {
        Map<String, Serializable> context = buildContext(24L, null);

        rule.isAllowed("betje", context);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfProcessInstanceIdParamNotPresent() throws Exception {
        Map<String, Serializable> context = buildContext(null, 7L);

        rule.isAllowed("betje", context);
    }

    @Test
    public void shouldNotBeAllowedIfNoArchivedTasks() throws Exception {
        final long processInstanceId = 541L;

        Map<String, Serializable> context = buildContext(processInstanceId, 11L);
        doReturn(Collections.emptyList()).when(activityInstanceService).searchArchivedTasks(any(QueryOptions.class));

        final boolean allowed = rule.isAllowed("someKey", context);

        assertThat(allowed).isFalse();
    }

    @Test
    public void shouldBeAllowedIAnArchivedTaskIsAssignedToGivenUser() throws Exception {

        final long userId = 11L;
        Map<String, Serializable> context = buildContext(541L, userId);
        final SProcessInstance processInstance = mock(SProcessInstance.class);
        final SAUserTaskInstance userTaskInstance = mock(SAUserTaskInstance.class);
        doReturn(userId).when(userTaskInstance).getAssigneeId();
        doReturn(Arrays.asList(userTaskInstance)).when(activityInstanceService).searchArchivedTasks(any(QueryOptions.class));

        final boolean allowed = rule.isAllowed("someKey", context);

        assertThat(allowed).isTrue();
    }

    @Test
    public void shouldBeAllowedIAnArchivedTaskIsAssignedToGivenUserWithPagination() throws Exception {

        final long userId = 11L;
        Map<String, Serializable> context = buildContext(541L, userId);
        final SProcessInstance processInstance = mock(SProcessInstance.class);
        final SAUserTaskInstance notMatchingArchivedTask = mock(SAUserTaskInstance.class);
        final SAUserTaskInstance userTaskInstance = mock(SAUserTaskInstance.class);
        doReturn(userId).when(userTaskInstance).getAssigneeId();
        when(activityInstanceService.searchArchivedTasks(any(QueryOptions.class))).thenReturn(Arrays.<SAHumanTaskInstance> asList(notMatchingArchivedTask),
                Arrays.<SAHumanTaskInstance> asList(userTaskInstance));

        final boolean allowed = rule.isAllowed("someKey", context);

        assertThat(allowed).isTrue();
    }

    @Test(expected = SExecutionException.class)
    public void shouldThrowSExecutionExceptionIfExceptionOccurs() throws Exception {
        Map<String, Serializable> context = buildContext(256L, 31L);
        doThrow(SBonitaReadException.class).when(activityInstanceService).searchArchivedTasks(any(QueryOptions.class));

        rule.isAllowed("exception raised", context);
    }

    @Test
    public void getIdShouldReturnIsTaskPerformer() throws Exception {
        assertThat(rule.getId()).isEqualTo("IS_TASK_PERFORMER");
    }
}
