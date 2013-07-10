package com.heb.finance.analytics;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;

import com.heb.finance.analytics.model.RiskSensitivity;
import com.sun.istack.NotNull;
import com.yammer.metrics.annotation.Metered;
import com.yammer.metrics.annotation.Timed;

public class RiskSensitivityPersisterServiceImpl implements RiskSensitivityPersisterService{ 

	@Autowired
	private RiskSensitivityPathPersister riskSensitivityPathPersister;
	
	private ExecutorService executorService; 
	
	public void setRiskSensitivityPathPersister(RiskSensitivityPathPersister riskSensitivityPathPersister) {
		this.riskSensitivityPathPersister = riskSensitivityPathPersister;
	}

	@SuppressWarnings("serial")
	public RiskSensitivityPersisterServiceImpl(int noOfWorkers) {
		executorService = Executors.newFixedThreadPool(noOfWorkers);
	}
	
	/* (non-Javadoc)
	 * @see com.lab49.finance.analytics.RiskSensitivityPersisterService#persist(com.lab49.finance.analytics.model.RiskSensitivity)
	 */
	@Override
	@Timed
	@Metered
	public void persist(@NotNull final RiskSensitivity riskSensitivity){
		
		executorService.execute(new Runnable(){
			@Override
			public void run() {
				riskSensitivityPathPersister.batchInsert(riskSensitivity);				
			}			
		});	
	}
}
