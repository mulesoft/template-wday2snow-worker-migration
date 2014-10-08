/**
 * Mule Anypoint Template
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 */

package org.mule.kicks;

import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.servicenow.sysuser.GetRecordsResponse;

public class UserDateComparator {
	
	public static boolean isAfter(String userA, GetRecordsResponse snowUsers, int utcOffset) {
		Validate.notNull(userA, "The user A should not be null");
		Validate.notNull(snowUsers, "The user B should not be null");
		
		if (snowUsers.getGetRecordsResult() == null || snowUsers.getGetRecordsResult().isEmpty())
			return true;
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		DateTimeFormatter snowFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		
		DateTime lastModifiedDateOfA = formatter.parseDateTime(userA).minusHours(utcOffset);
		DateTime lastModifiedDateOfB = snowFormatter.parseDateTime(snowUsers.getGetRecordsResult().get(0).getSysUpdatedOn());
		System.out.println("user name : " + snowUsers.getGetRecordsResult().get(0).getFirstName() + " " + snowUsers.getGetRecordsResult().get(0).getLastName());
		System.out.println("wday last date: " + lastModifiedDateOfA.toDate());
		System.out.println("snow last date: " + lastModifiedDateOfB.toDate());
		return lastModifiedDateOfA.isAfter(lastModifiedDateOfB);
	}
}
