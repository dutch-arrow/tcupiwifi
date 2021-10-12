/*
 * Copyright © 2021 Dutch Arrow Software - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the Apache Software License 2.0.
 *
 * Created 14 Aug 2021.
 */


package nl.das.terrariumpi.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import nl.das.terrariumpi.objects.Terrarium;

/**
 *
 */
@Path("/sensors")
public class Sensors {

	@GET
	@Produces("application/json")
	public nl.das.terrariumpi.objects.Sensors getSensorReadings() {
		return Terrarium.getInstance().getSensors();
	}

	@POST
	@Path("/{tr}/{tt}")
	public Response setSensorValues(@PathParam("tr") int tr, @PathParam("tt") int tt) {
		Terrarium.getInstance().setSensors(tr, tt);
		return Response.noContent().build();
	}

	@POST
	@Path("/auto")
	public Response setSensors() {
		Terrarium.getInstance().setTestOff();
		return Response.noContent().build();
	}
}
