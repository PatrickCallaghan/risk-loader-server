package com.heb.finance.analytics;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinRouter;

import com.heb.finance.analytics.model.RiskSensitivity;
import com.typesafe.config.ConfigFactory;

public class RiskSensitivityServiceAkkaTest {

	private static final long STOP_COUNTER = 1000000;

	private static String[] roots = { "London", "New York", "Hong Kong",
			"Singapore", "Tokyo", "Paris", "Frankfurt" };
	private static String[] divisions = { "FX", "Equity", "Equity Derivatives",
			"Bonds", "IRS", "Futures" };
	private static String[] sensitivities = { "irDelta", "irGamma", "irVega",
			"fxDelta", "fxGamma", "fxVega", "crDelta", "crGamma", "crVega",
			"maturity" };
	private static String DESK = "desk";
	private static String TRADER = "trader";
	private static Set<String> noOfDistinctPaths = new HashSet<String>();

	@Test
	@Ignore
	public void testPersister() throws InterruptedException {

		// Create an Akka system
		ActorSystem system = ActorSystem.create("RiskSensitivitySystem",
				ConfigFactory.load().getConfig("remotelookup"));

		// Remote Actor
		ActorRef riskSensitivityPersisterService1 = system
				.actorFor("akka://RiskSensitivityActorSystem@10.0.1.49:2562/user/riskSensitivityPersister");
		ActorRef riskSensitivityPersisterService2 = system
				.actorFor("akka://RiskSensitivityActorSystem@10.0.1.49:2563/user/riskSensitivityPersister");
		ActorRef riskSensitivityPersisterService3 = system
				.actorFor("akka://RiskSensitivityActorSystem@10.0.1.49:2564/user/riskSensitivityPersister");

		Iterable<ActorRef> routees = Arrays.asList(new ActorRef[] {
				riskSensitivityPersisterService1,
				riskSensitivityPersisterService2,
				riskSensitivityPersisterService3 });
		ActorRef router = system.actorOf(new Props()
				.withRouter(RoundRobinRouter.create(routees)));

		long counter = 0;
		long start = System.currentTimeMillis();

		while (true) {
			router.tell(createNewRandomRiskSensitivity());
			
			counter++;

			if (counter % 10000 == 0) {
				System.out.println("Processed " + counter);
			}

			if (counter == STOP_COUNTER) {
				break;
			}
		}
		long end = System.currentTimeMillis();

		System.out.println("Router - " + router.path());		
		System.out.println(counter + " done in " + (end - start) + "ms.");
		System.out.println("No of distinct paths " + noOfDistinctPaths.size());

		noOfDistinctPaths = null;

		system.shutdown();
	}

	private RiskSensitivity createNewRandomRiskSensitivity() {

		String root = roots[(int) (Math.random() * roots.length)];
		String division = divisions[(int) (Math.random() * divisions.length)];
		String sensitivity = sensitivities[(int) (Math.random() * sensitivities.length)];
		String desk = DESK + new Double(Math.random() * 10).intValue();
		String trader = TRADER + new Double(Math.random() * 20).intValue();
		String path = root + "/" + division + "/" + desk + "/" + trader;

		noOfDistinctPaths.add(path);

		return new RiskSensitivity(sensitivity, path, new BigDecimal(1));
	}
}
