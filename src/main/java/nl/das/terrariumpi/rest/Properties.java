/*
 * Copyright © 2021 Dutch Arrow Software - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the Apache Software License 2.0.
 *
 * Created 14 Aug 2021.
 */


package nl.das.terrariumpi.rest;

import java.time.LocalDateTime;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import nl.das.terrariumpi.Util;
import nl.das.terrariumpi.objects.Terrarium;

/**
 *
 */

public class Properties {

	@GET
	@Path("/properties")
	@Produces("application/json")
	public String getProperties() {
		return Terrarium.getInstance().getProperties();
	}

	@POST
	@Path("/trace/on")
	@Produces("application/json")
	public Response setTraceOn() {
		Terrarium t = Terrarium.getInstance();
		t.setNow(LocalDateTime.now());
		t.setTrace(true);
		// Create the tracefiles
		Util.createStateTraceFile();
		Util.createTemperatureTraceFile();
		Util.traceState(LocalDateTime.now(), "start");
		Util.traceTemperature(LocalDateTime.now(), "start");
		for (String d : t.deviceList) {
			Util.traceState(LocalDateTime.now(), "%s %s", d, t.isDeviceOn(d) ? "1" : "0");
		}
		return Response.noContent().build();
	}

	@POST
	@Path("/trace/off")
	@Produces("application/json")
	public Response setTraceOff() {
		Util.traceState(LocalDateTime.now(), "stop");
		Util.traceTemperature(LocalDateTime.now(), "stop");
		Terrarium.getInstance().setTrace(false);
		return Response.noContent().build();
	}
}
