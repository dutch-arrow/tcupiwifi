/*
 * Copyright © 2021 Dutch Arrow Software - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the Apache Software License 2.0.
 *
 * Created 17 Aug 2021.
 */

package nl.das.terrariumpi.rest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import nl.das.terrariumpi.Util;

/**
 *
 */
@Path("/history")
public class History {

	@GET
	@Path("/temperature")
	@Produces("text/plain")
	public String getTemperatureFile () {
		String content = "";
		try {
			content = Files.readString(Paths.get(Util.TTRACEFILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	@GET
	@Path("/state")
	@Produces("text/plain")
	public String getStateFile () {
		String content = "";
		try {
			content = Files.readString(Paths.get(Util.DTRACEFILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
}
