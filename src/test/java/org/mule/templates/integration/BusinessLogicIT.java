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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

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

import com.mulesoft.module.batch.BatchTestHelper;

/**
 * The objective of this class is to validate the correct behavior of the flows
 * for this Anypoint Tempalte that make calls to external systems.
 * 
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {

	private static final Logger LOGGER = LogManager.getLogger(BusinessLogicIT.class);
	private static final String PATH_TO_TEST_PROPERTIES = "./src/test/resources/mule.test.properties";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static final String PHONE_NUMBER = "232-2323";
	private static final String STREET = "999 Main St";
	private static final String CITY = "San Francisco";
	private static final String EMAIL = "darko.vukovic" + System.currentTimeMillis() + "@mulesoft.com";
	
	protected static final int TIMEOUT_SEC = 60;
	
	private static String WORKDAY_ID;
	private String SNOW_ID;
	private BatchTestHelper helper;
	private Map<String, String> user = new HashMap<String, String>();
	
	
	@BeforeClass
	public static void init(){
		
		Calendar cal = Calendar.getInstance();
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
		System.setProperty("migration.startDate", DATE_FORMAT.format(cal.getTime()));
	}
	
	@Before
	public void setUp() throws Exception {
		final Properties props = new Properties();
    	try {
    		props.load(new FileInputStream(PATH_TO_TEST_PROPERTIES));
    	} catch (Exception e) {
    		LOGGER.error("Error occured while reading mule.test.properties", e);
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

		MuleEvent response = runFlow("getSnowUsers", getTestEvent(EMAIL, MessageExchangePattern.REQUEST_RESPONSE));
		List<Map<String,String>> snowUserList = (List<Map<String,String>>) response.getMessage().getPayload();
		LOGGER.info("snow users: " + snowUserList.size());
		
		Assert.assertTrue("There should be a user in ServiceNow.", snowUserList.size() == 1);
		Assert.assertEquals("Street should be set", snowUserList.get(0).get("Street"), user.get("Street"));
		Assert.assertEquals("Email should be set", snowUserList.get(0).get("Email"), user.get("Email"));
		
		SNOW_ID = snowUserList.get(0).get("Id");
	}

	private void createTestDataInSandBox() throws MuleException, Exception {
		LOGGER.info("updating a workday employee...");
		try {
			runFlow("updateWorkdayEmployee", getTestEvent(prepareEdit(), MessageExchangePattern.REQUEST_RESPONSE));					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void deleteTestDataFromSandBox() throws MuleException, Exception {
		LOGGER.info("deleting test data...");
		// Delete the created users in Service Now
		runFlow("deleteSnowUsers", getTestEvent(SNOW_ID));
		
		// Workday test data do not need to be deleted, will be reused next time
	}
	
	private Map<String, String> prepareEdit(){			
		user.put("Location", "San_Francisco_site");
		user.put("Phone", PHONE_NUMBER);
		user.put("Extension", "+1");
		user.put("Email", EMAIL);
		user.put("ExtId__c", WORKDAY_ID);
		user.put("Street", STREET + System.currentTimeMillis());
		user.put("Country", "USA");
		user.put("State", "USA-CA");
		user.put("City", CITY);
		user.put("PostalCode", "94105");
		user.put("LastModifiedDate", DATE_FORMAT.format(System.currentTimeMillis()));
		return user;
	}
	
}
