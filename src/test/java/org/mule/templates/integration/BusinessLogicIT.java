/**
 * Mule Anypoint Template
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;

import com.mulesoft.module.batch.BatchTestHelper;
import com.servicenow.usermanagement.sysuser.GetRecordsResponse;

/**
 * The objective of this class is to validate the correct behavior of the flows
 * for this Anypoint Tempalte that make calls to external systems.
 * 
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {

	protected static final int TIMEOUT_SEC = 60;
	private BatchTestHelper helper;
	private static final String PHONE_NUMBER = "232-2323";
	private static final String STREET = "999 Main St";
	private static final String CITY = "San Francisco";
	private final String EMAIL = "darko.vukovic@mulesoft.com";
	private String SNOW_ID;
	private static final String PATH_TO_TEST_PROPERTIES = "./src/test/resources/mule.test.properties";
	private Map<String, String> user = new HashMap<String, String>();	
	private static String WORKDAY_ID;
	private static final Logger log = LogManager.getLogger(BusinessLogicIT.class);
	
	@BeforeClass
	public static void init(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Calendar cal = Calendar.getInstance();
		System.setProperty("migration.startDate", "\"" + sdf.format(cal.getTime()) + "\"");
	}
	
	@Before
	public void setUp() throws Exception {
		final Properties props = new Properties();
    	try {
    		props.load(new FileInputStream(PATH_TO_TEST_PROPERTIES));
    	} catch (Exception e) {
    		log.error("Error occured while reading mule.test.properties", e);
    	} 
    	WORKDAY_ID = props.getProperty("wday.testuser.id");
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
		log.info("snow users: " + snowRes.getGetRecordsResult().size());
		
		Assert.assertTrue("There should be a user in ServiceNow.", snowRes.getGetRecordsResult().size() == 1);
		Assert.assertEquals("Street should be set", snowRes.getGetRecordsResult().get(0).getStreet(), user.get("Street"));				
		
		SNOW_ID = snowRes.getGetRecordsResult().get(0).getSysId();
	}

	private void createTestDataInSandBox() throws MuleException, Exception {
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("updateWorkdayEmployee");
		flow.initialise();
		log.info("updating a workday employee...");
		try {
			flow.process(getTestEvent(prepareEdit(), MessageExchangePattern.REQUEST_RESPONSE));						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void deleteTestDataFromSandBox() throws MuleException, Exception {
		log.info("deleting test data...");
		// Delete the created users in Service Now
    	SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("deleteSnowUsers");
		flow.initialise();		
		flow.process(getTestEvent(SNOW_ID));				
		
		// Workday test data do not need to be deleted, will be reused next time
	}
	
	private Map<String, String> prepareEdit(){			
		user.put("Location", "San_Francisco_site");
		user.put("Phone", PHONE_NUMBER);
		user.put("Email", EMAIL);
		user.put("ExtId__c", WORKDAY_ID);
		user.put("Street", STREET + System.currentTimeMillis());
		user.put("Country", "USA");
		user.put("State", "USA-CA");
		user.put("City", CITY);
		user.put("PostalCode", "94105");
		user.put("LastModifiedDate", String.valueOf(System.currentTimeMillis()));
		return user;
	}
	
}
