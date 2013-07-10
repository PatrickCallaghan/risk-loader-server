package com.heb.finance.analytics;
 
import com.heb.finance.analytics.akka.RiskSensitivityPersisterActorService;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.kernel.Bootable;
 
public class RiskSensitivityMain implements Bootable {
	
  final ActorSystem system = ActorSystem.create("RiskSensitivityActorSystem");
 
   public void startup() {    
    system.actorOf(new Props(new UntypedActorFactory() {
		public UntypedActor create() {
			return new RiskSensitivityPersisterActorService(1);
		}
	}));
  }
 
  public void shutdown() {
    system.shutdown();
  }
}
