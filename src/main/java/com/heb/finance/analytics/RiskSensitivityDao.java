package com.heb.finance.analytics;

import java.util.Map;
import java.util.Set;

import me.prettyprint.cassandra.model.thrift.ThriftCounterColumnQuery;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HCounterColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.CounterQuery;

public class RiskSensitivityDao {

	private static StringSerializer STRING_SERILAIZER = StringSerializer.get();

	private static String COLUMN_FAMILY = "risk_sensitivity_aggregator";

	private final Keyspace keyspaceOperator;

	public RiskSensitivityDao(String clusterName, String url, String keyspace) {

		Cluster cluster = HFactory.getOrCreateCluster(clusterName, url);
		this.keyspaceOperator = HFactory.createKeyspace(keyspace, cluster);

		insertCounterColumn("irDelta", "/test/stad/ates/sta", 0l);
	}

	public boolean checkAlive(){
		
		try{
			HFactory.createMutator(this.keyspaceOperator, STRING_SERILAIZER);;
			return true;
		}catch (Exception e){
			return false;
		}		 
	}
	
	private void insertCounterColumn(String counterColumnName, String rowKey, Long initialValue) {

		Mutator<String> mutator = HFactory.createMutator(this.keyspaceOperator, STRING_SERILAIZER);

		// inserting a counter columns with initial value
		HCounterColumn<String> counterColumn = HFactory.createCounterColumn(counterColumnName, initialValue, STRING_SERILAIZER);
		mutator.insertCounter(rowKey, COLUMN_FAMILY, counterColumn);
		mutator.execute();

		CounterQuery<String, String> counter = new ThriftCounterColumnQuery<String, String>(this.keyspaceOperator, STRING_SERILAIZER,
				STRING_SERILAIZER);

		counter.setColumnFamily(COLUMN_FAMILY).setKey(rowKey).setName(counterColumnName);
		counter = null;
	}

	public void incrementCounterMapIncrementsBatch(String columnName, Map<String, Long> entries) {

		this.incrementCounterMapIncrementsBatch(COLUMN_FAMILY, columnName, entries);
	}

	public void incrementCounterMapIncrementsBatch(String columnFamilyName, String columnName, Map<String, Long> entries) {

		Set<String> keys = entries.keySet();

		for (String key : keys) {
			this.incrementCounter(columnName, key, entries.get(key));
		}
		entries.clear();
	}

	public void incrementCounter(String columnName, String key, Long incrementValue) {

		Mutator<String> mutator = HFactory.createMutator(this.keyspaceOperator, StringSerializer.get());
		mutator.incrementCounter(key, COLUMN_FAMILY, columnName, incrementValue);
		mutator.execute();
		mutator = null;
	}
}
