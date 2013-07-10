package com.heb.finance.analytics;

import org.junit.Test;

import com.heb.finance.analytics.RiskSensitivityDao;


public class RiskSensitivityDaoTest {

	@Test
	public void testSetup(){
		RiskSensitivityDao riskSensitivityDao = new RiskSensitivityDao("MyCluster", "localhost:9160", "Analytics");
		
		riskSensitivityDao.incrementCounter("irDelta", "Test", 1l);
	}

}
