package com.heb.finance.analytics;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.kernel.Bootable;

import com.heb.finance.analytics.akka.RiskSensitivityPersisterActorService;
import com.typesafe.config.ConfigFactory;

public class RiskSensitivityBoot implements Bootable {

	private final ActorSystem system;

	public RiskSensitivityBoot(int port) {
		this.system = ActorSystem.create("RiskSensitivityActorSystem", 
				ConfigFactory.load().getConfig("riskSensitivityService" + port));
	}
	
	public void init() {
		this.startup();
		
		while (!Thread.interrupted()){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				System.exit(1);
			}
		}
	}

	@SuppressWarnings("serial")
	public void startup() {
			
		system.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new RiskSensitivityPersisterActorService(1);
			}
		}), "riskSensitivityPersister");
	}

	public void shutdown() {
		system.shutdown();
	}

	public static void main(String args[]) {
		if (args.length < 1){
			System.err.println("Usage: Boot port");
		}
		
		new RiskSensitivityBoot(Integer.parseInt(args[0])).init();
	}
}
