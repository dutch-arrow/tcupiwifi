/*
 * Copyright © 2021 Dutch Arrow Software - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the Apache Software License 2.0.
 *
 * Created 13 Aug 2021.
 */


package nl.das.terraria.rest;

import org.minijax.Minijax;
import org.minijax.json.JsonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class RestServer extends Minijax {

	private static Logger log = LoggerFactory.getLogger(RestServer.class);

	public RestServer(String host, int port) {
		super.host(host).port(port);
		super.register(JsonFeature.class);
		super.register(Timers.class);
		super.register(Rulesets.class);
		super.register(SprayerRule.class);
		super.register(Sensors.class);
		super.register(DeviceState.class);
		super.register(Properties.class);
		super.register(History.class);
	}


	@Override
	public void start() {
		super.start();
		log.info("RestServer running....");
	}

	@Override
	public void stop() {
		super.stop();
		log.info("RestServer stopped");
	}
}
