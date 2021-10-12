/*
 * Copyright © 2021 Dutch Arrow Software - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the Apache Software License 2.0.
 *
 * Created 13 Aug 2021.
 */


package nl.das.terrariumpi.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import nl.das.terrariumpi.objects.Terrarium;

/**
 *
 */
@Path("/sprayerrule")
public class SprayerRule {

	@GET
	@Produces("application/json")
	public nl.das.terrariumpi.objects.SprayerRule getSprayerRule() {
		return Terrarium.getInstance().getSprayerRule();
	}

	@PUT
	@Consumes("application/json")
	public Response replaceSprayerRule(nl.das.terrariumpi.objects.SprayerRule sprayerRule) {
		Terrarium.getInstance().setSprayerRule(sprayerRule);
		Terrarium.getInstance().saveSettings();
		return Response.noContent().build();
	}
}

