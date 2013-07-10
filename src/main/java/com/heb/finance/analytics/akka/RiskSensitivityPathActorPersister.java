package com.heb.finance.analytics.akka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import akka.actor.UntypedActor;

import com.heb.finance.analytics.RiskSensitivityDao;
import com.heb.finance.analytics.model.RiskSensitivity;
import com.heb.finance.analytics.utils.PathConverter;

public class RiskSensitivityPathActorPersister extends UntypedActor {

	private final RiskSensitivityDao riskSensitivityDao;
	private final PathConverter pathConverter;
	
	public RiskSensitivityPathActorPersister(RiskSensitivityDao riskSensitivityDao,
			PathConverter pathConverter){
		this.riskSensitivityDao = riskSensitivityDao;
		this.pathConverter = pathConverter;
	}
	
	public void onReceive(Object message) {
		if (message instanceof RiskSensitivity) {
			
			RiskSensitivity riskSensitivity = (RiskSensitivity) message;
			
			this.batchInsert(riskSensitivity);
		} else {
			unhandled(message);
		}
	}

	private void batchInsert(RiskSensitivity riskSensitivity) {
		
		List<String> parts = pathConverter.convertPathToParts(riskSensitivity.getPath());
		
		Map<String, Long> pathValueMap = new HashMap<String, Long>();
		
		for (String part : parts){
			pathValueMap.put(part, riskSensitivity.getValue().longValue());
		}
		
		this.riskSensitivityDao.incrementCounterMapIncrementsBatch(riskSensitivity.getName(), pathValueMap);		
	}
	
}