package com.heb.finance.analytics.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class RiskSensitivity implements Serializable{

	public final String name;
	public final String path;
	public final BigDecimal value;

	public RiskSensitivity(String name, String path, BigDecimal value) {
		super();
		this.name = name;
		this.path = path;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public BigDecimal getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "RiskSensitivity [name=" + name + ", path=" + path + ", value=" + value + "]";
	}
}
