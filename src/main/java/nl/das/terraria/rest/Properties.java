/*
 * Copyright © 2021 Dutch Arrow Software - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the Apache Software License 2.0.
 *
 * Created 14 Aug 2021.
 */


package nl.das.terraria.rest;

import java.time.LocalDateTime;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import nl.das.terraria.Util;
import nl.das.terraria.objects.Terrarium;

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
		Util.traceState(Terrarium.traceFolder, LocalDateTime.now(), "start");
		Util.traceTemperature(Terrarium.traceFolder, LocalDateTime.now(), "start");
		for (String d : t.deviceList) {
			Util.traceState(Terrarium.traceFolder, LocalDateTime.now(), "%s %s", d, t.isDeviceOn(d) ? "1" : "0");
		}
		return Response.noContent().build();
	}

	@POST
	@Path("/trace/off")
	@Produces("application/json")
	public Response setTraceOff() {
		Util.traceState(Terrarium.traceFolder, LocalDateTime.now(), "stop");
		Util.traceTemperature(Terrarium.traceFolder, LocalDateTime.now(), "stop");
		Terrarium.getInstance().setTrace(false);
		return Response.noContent().build();
	}
}
