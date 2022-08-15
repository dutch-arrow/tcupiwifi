/*
 * Copyright © 2021 Dutch Arrow Software - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the Apache Software License 2.0.
 *
 * Created 13 Aug 2021.
 */


package nl.das.terraria.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import nl.das.terraria.objects.Terrarium;

/**
 *
 */
@Path("/timers")
public class Timers {

	@GET
	@Produces("application/json")
	public TimerArray getAllTimers() {
		return new TimerArray(Terrarium.getInstance().getTimers());
	}

	@GET
	@Path("/{device}")
	@Produces("application/json")
	public TimerArray getTimersForDevice(@PathParam("device") String device) {
		return new TimerArray(Terrarium.getInstance().getTimersForDevice(device));
	}

	@PUT
    @Consumes("application/json")
	public Response saveTimers(TimerArray timers) {
		Terrarium.getInstance().replaceTimers(timers.getTimers());
		Terrarium.getInstance().saveSettings();
		return Response.noContent().build();
	}
}
