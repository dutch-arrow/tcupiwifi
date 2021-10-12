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
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import nl.das.terrariumpi.objects.Ruleset;
import nl.das.terrariumpi.objects.Terrarium;

/**
 *
 */
@Path("/ruleset")
public class Rulesets {

	@GET
	@Path("/{nr}")
	@Produces("application/json")
	public Ruleset getRuleset(@PathParam("nr") int nr) {
		return Terrarium.getInstance().getRuleset(nr);
	}

	@PUT
	@Path("/{nr}")
	@Consumes("application/json")
	public Response saveRuleset(@PathParam("nr") int nr, Ruleset ruleset) {
		Terrarium.getInstance().replaceRuleset(nr, ruleset);
		Terrarium.getInstance().saveSettings();
		return Response.noContent().build();
	}

}
