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
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import nl.das.terrariumpi.Util;
import nl.das.terrariumpi.objects.Terrarium;

/**
 *
 */
@Path("/history")
public class History {

	@GET
	@Path("/temperature")
	@Produces("application/json")
	public List<String> getTempTracefiles() {
		List<String> files = new ArrayList<>();
		try {
			files = Util.listTraceFiles(Terrarium.traceFolder, "temp_");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;
	}

	@GET
	@Path("/state")
	@Produces("application/json")
	public List<String> getStateTracefiles() {
		List<String> files = new ArrayList<>();
		try {
			files = Util.listTraceFiles(Terrarium.traceFolder, "state_");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;
	}

	@GET
	@Path("/temperature/{fname}")
	@Produces("text/plain")
	public String getTemperatureFile (@PathParam("fname") String fname) {
		String content = "";
		try {
			content = Files.readString(Paths.get(Terrarium.traceFolder + "/" + fname));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	@GET
	@Path("/state/{fname}")
	@Produces("text/plain")
	public String getStateFile (@PathParam("fname") String fname) {
		String content = "";
		try {
			content = Files.readString(Paths.get(Terrarium.traceFolder + "/" + fname));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
}
