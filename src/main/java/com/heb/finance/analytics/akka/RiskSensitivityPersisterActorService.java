package com.heb.finance.analytics.akka;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;

import com.heb.finance.analytics.RiskSensitivityDao;
import com.heb.finance.analytics.model.RiskSensitivity;
import com.heb.finance.analytics.utils.PathConverterImpl;

public class RiskSensitivityPersisterActorService extends UntypedActor {

	private final ActorRef workerRouter;
	private final LoggingAdapter LOGGER = Logging.getLogger(getContext().system(), this);

	@SuppressWarnings("serial")
	public RiskSensitivityPersisterActorService(int noOfWorkers) {

		final RiskSensitivityDao riskSensitivityDao = new RiskSensitivityDao("MyCluster", "localhost:9160", "Analytics");

		this.workerRouter = getContext().actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new RiskSensitivityPathActorPersister(riskSensitivityDao, new PathConverterImpl());
			}
		}));
	}

	public void onReceive(Object object) {

		if (object instanceof RiskSensitivity) {

			LOGGER.info("Received String message: {}", ((RiskSensitivity) object).toString());

			this.workerRouter.tell((RiskSensitivity) object);
			object = null;
		} else {
			throw new IllegalArgumentException("Invalid Object");
		}
	}

	private static SupervisorStrategy strategy = new OneForOneStrategy(10, Duration.create("1 minute"),
			new Function<Throwable, Directive>() {
				@Override
				public Directive apply(Throwable t) {
					
					if (t instanceof Exception) {
						System.out.println("Got Error - " + t.getMessage());
					}
					return null;
				}
			});

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}
}
