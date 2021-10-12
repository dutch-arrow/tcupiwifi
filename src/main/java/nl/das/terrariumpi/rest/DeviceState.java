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
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import nl.das.terrariumpi.Util;
import nl.das.terrariumpi.objects.Terrarium;

/**
 *
 */
public class DeviceState {

	@GET
	@Path("/state")
	@Produces("application/json")
	public String getState() {
		return Terrarium.getInstance().getState();
	}

	@PUT
	@Path("/device/{device}/on")
	public Response setDeviceOn(@PathParam("device") String device) {
		Terrarium.getInstance().setDeviceOn(device, -1);
		return Response.noContent().build();
	}

	@PUT
	@Path("/device/{device}/on/{period}")
	public Response setDeviceOn(@PathParam("device") String device, @PathParam("period") int period) {
		// period is in seconds (max 3600) so convert it to an endtime in Epoch seconds
		Terrarium.getInstance().setDeviceOn(device, Util.now(LocalDateTime.now()) + period);
		return Response.noContent().build();
	}

	@PUT
	@Path("/device/{device}/off")
	public Response setDeviceOff(@PathParam("device") String device) {
		Terrarium.getInstance().setDeviceOff(device);
		return Response.noContent().build();
	}

	@PUT
	@Path("/device/{device}/manual")
	public Response setDeviceManual(@PathParam("device") String device) {
		Terrarium.getInstance().setDeviceManualOn(device);
		return Response.noContent().build();
	}

	@PUT
	@Path("/device/{device}/auto")
	public Response setDeviceAuto(@PathParam("device") String device) {
		Terrarium.getInstance().setDeviceManualOff(device);
		return Response.noContent().build();
	}

	@POST
	@Path("/counter/{device}/{value}")
	public Response setLifecycleCounter(@PathParam("device") String device, @PathParam("value") int value) {
		Terrarium.getInstance().setLifecycleCounter(device, value);
		return Response.noContent().build();
	}
}
