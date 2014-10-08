/**
 * Mule Anypoint Template
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.templates.utils.Employee;

import com.mulesoft.module.batch.BatchTestHelper;
import com.servicenow.sysuser.GetRecordsResponse;
import com.workday.hr.EmployeeGetType;
import com.workday.hr.EmployeeReferenceType;
import com.workday.hr.ExternalIntegrationIDReferenceDataType;
import com.workday.hr.IDType;
import com.workday.staffing.EventClassificationSubcategoryObjectIDType;
import com.workday.staffing.EventClassificationSubcategoryObjectType;
import com.workday.staffing.TerminateEmployeeDataType;
import com.workday.staffing.TerminateEmployeeRequestType;
import com.workday.staffing.TerminateEventDataType;

/**
 * The objective of this class is to validate the correct behavior of the flows
 * for this Anypoint Tempalte that make calls to external systems.
 * 
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {

	private static final String TEMPLATE_PREFIX = "wday2snow-worker-migration";
	
	protected static final int TIMEOUT_SEC = 60;
	private BatchTestHelper helper;
	private static final String PHONE_NUMBER = "650-232-2323";
	private static final String STREET = "999 Main St";
	private static final String CITY = "San Francisco";
	private static final String LAST_NAME = "Willis1";
	private String EXT_ID, EMAIL = TEMPLATE_PREFIX + System.currentTimeMillis() + "@test.com";
	private Employee employee;
	private String SNOW_ID;
	
	@BeforeClass
	public static void init(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Calendar cal = Calendar.getInstance();
		System.setProperty("migration.startDate", "\"" + sdf.format(cal.getTime()) + "\"");
	}
	
	@Before
	public void setUp() throws Exception {
		helper = new BatchTestHelper(muleContext);
		createTestDataInSandBox();
	}

	@After
	public void tearDown() throws Exception {		
		deleteTestDataFromSandBox();
	}

	@Test
	public void testMainFlow() throws Exception {
		Thread.sleep(10000);
		runFlow("mainFlow");
		// Wait for the batch job executed by the poll flow to finish
		helper.awaitJobTermination(TIMEOUT_SEC * 1000, 500);
		helper.assertJobWasSuccessful();

		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("getSnowUsers");
		flow.initialise();
		
		MuleEvent response = flow.process(getTestEvent(EMAIL, MessageExchangePattern.REQUEST_RESPONSE));
		GetRecordsResponse snowRes = ((GetRecordsResponse)response.getMessage().getPayload());
		logger.info("snow users: " + snowRes.getGetRecordsResult().size());
		
		Assert.assertTrue("There should be a user in ServiceNow.", snowRes.getGetRecordsResult().size() == 1);
		Assert.assertEquals("First name should be set", snowRes.getGetRecordsResult().get(0).getFirstName(), EXT_ID);
		Assert.assertEquals("Last name should be set", snowRes.getGetRecordsResult().get(0).getLastName(), LAST_NAME);
		Assert.assertEquals("City should be set", snowRes.getGetRecordsResult().get(0).getCity(), CITY);
		Assert.assertEquals("Street should be set", snowRes.getGetRecordsResult().get(0).getStreet(), STREET);		
		Assert.assertEquals("Home Phone number should be set", snowRes.getGetRecordsResult().get(0).getHomePhone(), "+1  " + PHONE_NUMBER);		
		
		SNOW_ID = snowRes.getGetRecordsResult().get(0).getSysId();
	}

	@SuppressWarnings("unchecked")
	private void createTestDataInSandBox() throws MuleException, Exception {
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("hireEmployee");
		flow.initialise();
		logger.info("creating a workday employee...");
		try {
			flow.process(getTestEvent(prepareNewHire(), MessageExchangePattern.REQUEST_RESPONSE));						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<Object> prepareNewHire(){
		EXT_ID = TEMPLATE_PREFIX + System.currentTimeMillis();
		logger.info("employee name: " + EXT_ID + ", email: " + EMAIL);
		employee = new Employee(EXT_ID, LAST_NAME, EMAIL, PHONE_NUMBER, STREET, CITY, "CA", "94105", "US", "o7aHYfwG", 
				"2014-04-17-07:00", "2014-04-21-07:00", "QA Engineer", "San_Francisco_site", "Regular", "Full Time", "Salary", "USD", "140000", "Annual", "39905", "21440", EXT_ID);
		List<Object> list = new ArrayList<Object>();
		list.add(employee);
		return list;
	}
	
	private void deleteTestDataFromSandBox() throws MuleException, Exception {
		logger.info("deleting test data...");
		// Delete the created users in Service Now
    	SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("deleteSnowUsers");
		flow.initialise();		
		flow.process(getTestEvent(SNOW_ID));				
		
		// Delete the created users in Workday
		flow = getSubFlow("getWorkdayEmployee");
		flow.initialise();
		
		try {
			MuleEvent response = flow.process(getTestEvent(getEmployee(), MessageExchangePattern.REQUEST_RESPONSE));			
			flow = getSubFlow("terminateWorkdayEmployee");
			flow.initialise();
			flow.process(getTestEvent(prepareTerminate(response), MessageExchangePattern.REQUEST_RESPONSE));								
		} catch (Exception e) {
			e.printStackTrace();
		}	

	}
	
	private TerminateEmployeeRequestType prepareTerminate(MuleEvent response) throws DatatypeConfigurationException{
		TerminateEmployeeRequestType req = (TerminateEmployeeRequestType) response.getMessage().getPayload();
		TerminateEmployeeDataType eeData = req.getTerminateEmployeeData();		
		TerminateEventDataType event = new TerminateEventDataType();
		eeData.setTerminationDate(xmlDate(new Date()));
		EventClassificationSubcategoryObjectType prim = new EventClassificationSubcategoryObjectType();
		List<EventClassificationSubcategoryObjectIDType> list = new ArrayList<EventClassificationSubcategoryObjectIDType>();
		EventClassificationSubcategoryObjectIDType id = new EventClassificationSubcategoryObjectIDType();
		id.setType("WID");
		id.setValue("208082cd6b66443e801d95ffdc77461b");
		list.add(id);
		prim.setID(list);
		event.setPrimaryReasonReference(prim);
		eeData.setTerminateEventData(event );
		return req;		
	}
	
	private static XMLGregorianCalendar xmlDate(Date date) throws DatatypeConfigurationException {
		GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
		gregorianCalendar.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
	}

	
	private EmployeeGetType getEmployee(){
		EmployeeGetType get = new EmployeeGetType();
		EmployeeReferenceType empRef = new EmployeeReferenceType();					
		ExternalIntegrationIDReferenceDataType value = new ExternalIntegrationIDReferenceDataType();
		IDType idType = new IDType();
		value.setID(idType);
		idType.setSystemID("Salesforce - Chatter");
		idType.setValue(EXT_ID);			
		empRef.setIntegrationIDReference(value);
		get.setEmployeeReference(empRef);		
		return get;
	}
}
