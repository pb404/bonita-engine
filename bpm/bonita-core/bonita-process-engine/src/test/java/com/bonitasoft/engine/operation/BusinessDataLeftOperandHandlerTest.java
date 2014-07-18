package com.bonitasoft.engine.operation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.commons.exceptions.SBonitaException;
import org.bonitasoft.engine.core.expression.control.model.SExpressionContext;
import org.bonitasoft.engine.core.operation.exception.SOperationExecutionException;
import org.bonitasoft.engine.core.operation.model.SLeftOperand;
import org.bonitasoft.engine.core.operation.model.impl.SLeftOperandImpl;
import org.bonitasoft.engine.core.process.instance.api.FlowNodeInstanceService;
import org.bonitasoft.engine.data.instance.api.DataInstanceContainer;
import org.bonitasoft.engine.persistence.SBonitaReadException;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.bonitasoft.engine.bdm.Entity;
import com.bonitasoft.engine.business.data.BusinessDataRepository;
import com.bonitasoft.engine.core.process.instance.api.RefBusinessDataService;
import com.bonitasoft.engine.core.process.instance.model.SMultiRefBusinessDataInstance;
import com.bonitasoft.engine.core.process.instance.model.SSimpleRefBusinessDataInstance;
import com.bonitasoft.engine.core.process.instance.model.impl.SMultiRefBusinessDataInstanceImpl;
import com.bonitasoft.engine.core.process.instance.model.impl.SSimpleRefBusinessDataInstanceImpl;
import com.bonitasoft.engine.operation.pojo.Employee;
import com.bonitasoft.engine.operation.pojo.InvalidTravel;
import com.bonitasoft.engine.operation.pojo.Travel;

@RunWith(MockitoJUnitRunner.class)
public class BusinessDataLeftOperandHandlerTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private FlowNodeInstanceService flowNodeInstanceService;

    @Mock
    private BusinessDataRepository repository;

    @Mock
    private RefBusinessDataService refBusinessDataService;

    @InjectMocks
    private BusinessDataLeftOperandHandler leftOperandHandler;

    @Test
    public void updateShouldSetReferenceForNonNullBizDataId() throws Exception {
        final BusinessDataLeftOperandHandler spy = spy(leftOperandHandler);
        final SSimpleRefBusinessDataInstance refBiz = mock(SSimpleRefBusinessDataInstance.class);
        final Long bizDataId = 98744L;
        doReturn(refBiz).when(spy).getRefBusinessDataInstance(eq("myBizData"), eq(9L), eq("some container"));
        final Peticion bizData = new Peticion(bizDataId);
        doReturn(bizData).when(repository).merge(eq(bizData));

        spy.update(createLeftOperand("myBizData"), bizData, 9L, "some container");

        verify(refBusinessDataService).updateRefBusinessDataInstance(refBiz, bizDataId);
    }

    class Peticion implements Entity {

        private static final long serialVersionUID = 1L;

        private final Long id;

        public Peticion(final Long id) {
            this.id = id;
        }

        @Override
        public Long getPersistenceId() {
            return id;
        }

        @Override
        public Long getPersistenceVersion() {
            return null;
        }

    }

    @Test
    public void insertBusinessData() throws SBonitaException {
        final long dataId = 789l;
        final Employee employee = new Employee(dataId, 52L, "firstName", "lastName");
        final long processInstanceId = 76846321l;

        final SLeftOperand leftOperand = mock(SLeftOperand.class);
        final SSimpleRefBusinessDataInstance refBizDataInstance = mock(SSimpleRefBusinessDataInstance.class);
        when(leftOperand.getName()).thenReturn("unused");
        when(flowNodeInstanceService.getProcessInstanceId(processInstanceId, DataInstanceContainer.PROCESS_INSTANCE.name())).thenReturn(processInstanceId);
        when(refBusinessDataService.getRefBusinessDataInstance("unused", processInstanceId)).thenReturn(refBizDataInstance);
        when(refBizDataInstance.getDataId()).thenReturn(null);
        when(repository.merge(employee)).thenReturn(employee);
        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                return null;
            }

        }).when(refBusinessDataService).updateRefBusinessDataInstance(refBizDataInstance, dataId);

        leftOperandHandler.update(leftOperand, employee, processInstanceId, DataInstanceContainer.PROCESS_INSTANCE.name());

        verify(repository).merge(employee);
        verify(refBusinessDataService).getRefBusinessDataInstance("unused", processInstanceId);
        verify(refBusinessDataService).updateRefBusinessDataInstance(refBizDataInstance, dataId);
    }

    @Test
    public void should_retrieve_retrieve_the_business_data() throws Exception {
        // given:
        final String bizDataName = "myTravel";
        final Travel myTravel = new Travel();

        final BusinessDataLeftOperandHandler spy = spy(leftOperandHandler);
        doReturn(myTravel).when(spy).getBusinessData(anyString(), anyLong());
        final Map<String, Serializable> inputValues = new HashMap<String, Serializable>(1);
        final SExpressionContext expressionContext = new SExpressionContext(-1L, "unused", inputValues);
        final SLeftOperand leftOperand = createLeftOperand(bizDataName);
        // when:
        final Object retrieve = spy.retrieve(leftOperand, expressionContext);

        // then:
        assertThat(retrieve).isEqualTo(myTravel);
    }

    private SLeftOperand createLeftOperand(final String bizDataName) {
        final SLeftOperandImpl leftOperand = new SLeftOperandImpl();
        leftOperand.setName(bizDataName);
        return leftOperand;
    }

    @Test(expected = SOperationExecutionException.class)
    public void shouldUpdate_ThrowAnSOperationExecutionException() throws Exception {
        // given:
        final InvalidTravel myTravel = new InvalidTravel();
        final SSimpleRefBusinessDataInstance refInstance = mock(SSimpleRefBusinessDataInstance.class);
        final BusinessDataLeftOperandHandler spy = spy(leftOperandHandler);
        doReturn(myTravel).when(spy).getBusinessData(anyString(), anyLong());
        doReturn(refInstance).when(spy).getRefBusinessDataInstance(anyString(), anyLong(), anyString());
        final SLeftOperand leftOp = mock(SLeftOperand.class);
        when(leftOp.getName()).thenReturn("bizData");

        spy.update(leftOp, new Object(), 1L, "");
    }

    @Test
    public void shouldGetOperatorType_Return_BUSINESS_DATA_JAVA_SETTER() throws Exception {
        assertThat(leftOperandHandler.getType()).isEqualTo(SLeftOperand.TYPE_BUSINESS_DATA);
    }

    @Test
    public void getBusinessDataReturnTheBusinessData() throws Exception {
        final SSimpleRefBusinessDataInstance refInstance = mock(SSimpleRefBusinessDataInstance.class);
        final String bizDataName = "employee";
        final int processInstanceId = 457;
        when(refBusinessDataService.getRefBusinessDataInstance(bizDataName, processInstanceId)).thenReturn(refInstance);
        when(refInstance.getDataId()).thenReturn(45l);
        when(refInstance.getDataClassName()).thenReturn(Employee.class.getName());

        leftOperandHandler.getBusinessData(bizDataName, processInstanceId);

        verify(repository).findById(Employee.class, 45l);
    }

    @Test
    public void getBusinessDataCreateAnInstanceIfNoReferenceExists() throws Exception {
        final SSimpleRefBusinessDataInstance refInstance = mock(SSimpleRefBusinessDataInstance.class);
        final String bizDataName = "employee";
        final int processInstanceId = 457;
        when(refBusinessDataService.getRefBusinessDataInstance(bizDataName, processInstanceId)).thenReturn(refInstance);
        when(refInstance.getDataId()).thenReturn(null);
        when(refInstance.getDataClassName()).thenReturn(Employee.class.getName());

        final Employee employee = (Employee) leftOperandHandler.getBusinessData(bizDataName, processInstanceId);

        assertThat(employee).isNotNull();
        assertThat(employee.getPersistenceId()).isNull();
        assertThat(employee.getFirstName()).isNull();
        assertThat(employee.getLastName()).isNull();
    }

    @Test(expected = SBonitaReadException.class)
    public void getBusinessDataDoesNotCreateAnInstanceIfNoReferenceExistsWhenTheClassNameIsWrong() throws Exception {
        final SSimpleRefBusinessDataInstance refInstance = mock(SSimpleRefBusinessDataInstance.class);
        final String bizDataName = "employee";
        final int processInstanceId = 457;
        when(refBusinessDataService.getRefBusinessDataInstance(bizDataName, processInstanceId)).thenReturn(refInstance);
        when(refInstance.getDataId()).thenReturn(null);
        when(refInstance.getDataClassName()).thenReturn("fr.bonitasoft.engine.Employee");

        leftOperandHandler.getBusinessData(bizDataName, processInstanceId);
    }

    @Test(expected = SBonitaReadException.class)
    public void getBusinessDataDoesNotCreateAnInstanceIfNoReferenceExistsWhenTheClassNameIsAnInterface() throws Exception {
        final SSimpleRefBusinessDataInstance refInstance = mock(SSimpleRefBusinessDataInstance.class);
        final String bizDataName = "employee";
        final int processInstanceId = 457;
        when(refBusinessDataService.getRefBusinessDataInstance(bizDataName, processInstanceId)).thenReturn(refInstance);
        when(refInstance.getDataId()).thenReturn(null);
        when(refInstance.getDataClassName()).thenReturn(List.class.getName());

        leftOperandHandler.getBusinessData(bizDataName, processInstanceId);
    }

    @Test
    public void getMultiBusinessDataReturnTheBusinessData() throws Exception {
        final SMultiRefBusinessDataInstance refInstance = mock(SMultiRefBusinessDataInstance.class);
        final String bizDataName = "employee";
        final int processInstanceId = 457;
        when(refBusinessDataService.getRefBusinessDataInstance(bizDataName, processInstanceId)).thenReturn(refInstance);
        when(refInstance.getDataIds()).thenReturn(Arrays.asList(45l));
        when(refInstance.getDataClassName()).thenReturn(Employee.class.getName());

        leftOperandHandler.getBusinessData(bizDataName, processInstanceId);

        verify(repository).findByIds(Employee.class, Arrays.asList(45l));
    }

    @Test
    public void getMultiBusinessDataCreateAnInstanceIfNoReferenceExists() throws Exception {
        final SMultiRefBusinessDataInstance refInstance = mock(SMultiRefBusinessDataInstance.class);
        final String bizDataName = "employee";
        final int processInstanceId = 457;
        when(refBusinessDataService.getRefBusinessDataInstance(bizDataName, processInstanceId)).thenReturn(refInstance);
        when(refInstance.getDataIds()).thenReturn(new ArrayList<Long>());
        when(refInstance.getDataClassName()).thenReturn(Employee.class.getName());

        final List<Employee> employees = (List<Employee>) leftOperandHandler.getBusinessData(bizDataName, processInstanceId);
        assertThat(employees).hasSize(1);
        final Employee employee = employees.get(0);
        assertThat(employee).isNotNull();
        assertThat(employee.getPersistenceId()).isNull();
        assertThat(employee.getFirstName()).isNull();
        assertThat(employee.getLastName()).isNull();
    }

    @Test
    public void should_update_check_business_data_is_not_null() throws Exception {
        expectedException.expect(SOperationExecutionException.class);
        expectedException.expectMessage("Unable to insert/update a null business data");
        // when
        leftOperandHandler.update(createLeftOperand("bizData"), null, 1, "cont");
    }

    @Test
    public void should_update_update_business_data() throws Exception {
        // given: business data having id and ref having the same id
        final SLeftOperand leftOperand = createLeftOperand("bizData");
        final Peticion bizData = new Peticion(123456789L);
        doReturn(123l).when(flowNodeInstanceService).getProcessInstanceId(1l, "cont");
        final SSimpleRefBusinessDataInstanceImpl ref = createRefBusinessDataInstance(123456789L);
        doReturn(ref).when(refBusinessDataService).getRefBusinessDataInstance("bizData", 123l);
        doReturn(bizData).when(repository).merge(bizData);

        // when
        leftOperandHandler.update(leftOperand, bizData, 1, "cont");

        // then
        verify(repository).merge(bizData);
        verify(refBusinessDataService, times(0)).updateRefBusinessDataInstance(eq(ref), anyLong());
    }

    private SSimpleRefBusinessDataInstanceImpl createRefBusinessDataInstance(final Long dataId) {
        final SSimpleRefBusinessDataInstanceImpl sRefBusinessDataInstanceImpl = new SSimpleRefBusinessDataInstanceImpl();
        sRefBusinessDataInstanceImpl.setDataId(dataId);
        return sRefBusinessDataInstanceImpl;
    }

    @Test
    public void should_update_insert_business_data() throws Exception {
        // given: business data having null id and ref having null id
        final SLeftOperand leftOperand = createLeftOperand("bizData");
        final Peticion bizData = new Peticion(null);
        final Peticion mergedBizData = new Peticion(123456789L);
        doReturn(123l).when(flowNodeInstanceService).getProcessInstanceId(1l, "cont");
        final SSimpleRefBusinessDataInstanceImpl ref = createRefBusinessDataInstance(null);
        doReturn(ref).when(refBusinessDataService).getRefBusinessDataInstance("bizData", 123l);
        doReturn(mergedBizData).when(repository).merge(bizData);

        // when
        leftOperandHandler.update(leftOperand, bizData, 1, "cont");

        // then
        verify(repository, times(1)).merge(bizData);
        verify(refBusinessDataService, times(1)).updateRefBusinessDataInstance(ref, 123456789L);
    }

    @Test
    public void should_update_attach_business_data() throws Exception {
        // given: business data having not null id and ref having null id
        final SLeftOperand leftOperand = createLeftOperand("bizData");
        final Peticion bizData = new Peticion(123456789L);
        doReturn(123l).when(flowNodeInstanceService).getProcessInstanceId(1l, "cont");
        final SSimpleRefBusinessDataInstanceImpl ref = createRefBusinessDataInstance(null);
        doReturn(ref).when(refBusinessDataService).getRefBusinessDataInstance("bizData", 123l);
        doReturn(bizData).when(repository).merge(bizData);

        // when
        leftOperandHandler.update(leftOperand, bizData, 1, "cont");

        // then
        verify(repository, times(1)).merge(bizData);
        verify(refBusinessDataService, times(1)).updateRefBusinessDataInstance(ref, 123456789L);
    }

    @Test
    public void should_update_attach_multi_business_data() throws Exception {
        final SLeftOperand leftOperand = createLeftOperand("employees");
        final List<Peticion> peticions = new ArrayList<Peticion>();
        final Peticion bizData = new Peticion(123456789L);
        peticions.add(bizData);
        doReturn(123l).when(flowNodeInstanceService).getProcessInstanceId(1l, "cont");
        final SMultiRefBusinessDataInstanceImpl ref = mock(SMultiRefBusinessDataInstanceImpl.class);
        doReturn(ref).when(refBusinessDataService).getRefBusinessDataInstance("employees", 123l);
        doReturn(bizData).when(repository).merge(bizData);

        // when
        leftOperandHandler.update(leftOperand, peticions, 1, "cont");

        // then
        verify(repository).merge(bizData);
        verify(refBusinessDataService).updateRefBusinessDataInstance(ref, Arrays.asList(123456789L));
    }

    @Test
    @Ignore
    public void should_update_throw_exception_if_we_update_a_ref_with_an_already_existing_business_data() throws Exception {
        // given: business data having null id and ref having not null id
        final SLeftOperand leftOperand = createLeftOperand("bizData");
        final Peticion bizData = new Peticion(null);
        final Peticion mergedBizData = new Peticion(123456789L);
        doReturn(123l).when(flowNodeInstanceService).getProcessInstanceId(1l, "cont");
        final SSimpleRefBusinessDataInstanceImpl ref = createRefBusinessDataInstance(123456L);
        doReturn(ref).when(refBusinessDataService).getRefBusinessDataInstance("bizData", 123l);
        doReturn(mergedBizData).when(repository).merge(bizData);

        // when
        leftOperandHandler.update(leftOperand, bizData, 1, "cont");

        // then
        verify(repository, times(0)).merge(any(Entity.class));
        verify(refBusinessDataService, times(0)).updateRefBusinessDataInstance(eq(ref), anyLong());
    }

}
