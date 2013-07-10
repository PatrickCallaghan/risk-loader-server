package com.heb.finance.analytics.health;

import com.heb.finance.analytics.RiskSensitivityDao;
import com.yammer.metrics.health.HealthCheck;

public class RiskSensitivityDaoHealthCheck extends HealthCheck {
    private final RiskSensitivityDao riskSensitivityDao;

    public RiskSensitivityDaoHealthCheck(RiskSensitivityDao riskSensitivityDao) {
        this.riskSensitivityDao = riskSensitivityDao;
    }

    @Override
    protected Result check() throws Exception {
    	        
        if (!riskSensitivityDao.checkAlive()) {
            return Result.unhealthy("Could not connect to the RiskSensitivityDao");
        }
        return Result.healthy();
    }
}