/*
 * Copyright © 2021 Dutch Arrow Software - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the Apache Software License 2.0.
 *
 * Created 05 Aug 2021.
 */

package nl.das.terraria;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.pi4j.system.NetworkInfo;

import nl.das.terraria.hw.LCD;
import nl.das.terraria.objects.Terrarium;
import nl.das.terraria.rest.RestServer;

public class TCU {

	private static DateTimeFormatter dtfmt = DateTimeFormatter.ofPattern("HH:mm:ss");

	private static RestServer server;

	public static void main (String[] args) throws InterruptedException {
		LocalDateTime now = LocalDateTime.now();
		System.out.println(now.format(dtfmt) + " Start Initialization ...");
		System.err.println(now.format(dtfmt) + " System started.");

		// Initialize the LCD
		LCD lcd = new LCD();
		lcd.init(2, 16);
		lcd.write(0, "Initialize....");

		System.out.println(now.format(dtfmt) + " Starting the application");
		// Initialize and start the webserver
		int portnr = 80;
		server = new RestServer("0.0.0.0", portnr);
		server.start();
		// Retrieve the settings from disk
		Terrarium terrarium = null;
		try {
			String json = new String(Files.readAllBytes(Paths.get("settings.json")));
			terrarium = Terrarium.getInstance(json);
		} catch (NoSuchFileException e) {
			terrarium = Terrarium.getInstance();
			terrarium.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		terrarium.setNow(now);
		// Initialize the devices
		terrarium.initDevices();
		// Initialize device state
		terrarium.initDeviceState();
		// Retrieve the lifecycle values from disk
		try {
			String json = new String(Files.readAllBytes(Paths.get("lifecycle.txt")));
			if (json != null) {
				String lns[] = json.split("\n");
				for (String ln : lns) {
					String lp[] = ln.split("=");
					if (lp.length == 2) {
						terrarium.setDeviceLifecycle(lp[0], Integer.parseInt(lp[1]));
					}
				}
			}
		} catch (NoSuchFileException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Initialize the Temperature sensors
		terrarium.initSensors();
		int tterr = terrarium.getTerrariumTemperature();
		int troom =  terrarium.getRoomTemperature();
		lcd.displayLine1(troom, tterr);
		String ip="";
		try {
	        for (String ipAddress : NetworkInfo.getIPAddresses()) {
	        	if (ipAddress.startsWith("192")) {
	        		ip = ipAddress;
	        	}
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
		lcd.write(1, ip);
		// Check timers if devices should be on
		terrarium.initTimers(now);
		terrarium.initRules();
		// Get the current datetime
		int currentSec = now.getSecond();
		int currentMin = now.getMinute();
		int currentHour = now.getHour();
		// Start the loop
		System.out.println(now.format(dtfmt) + " Initialization done, start loop");
		while (true) {
			now = LocalDateTime.now();
			terrarium.setNow(now);
			if (now.getSecond() != currentSec) {
				currentSec = now.getSecond();
				// A second has passed
				// Each second check devices
				terrarium.checkDevices();
				if (now.getMinute() != currentMin) {
					currentMin = now.getMinute();
					// A minute has passed
//					System.out.println(now.format(dtfmt) + " A minute has passed");
					// Each minute
					// - display temperature on LCD line 1
					terrarium.readSensorValues();
					tterr = terrarium.getTerrariumTemperature();
					troom = terrarium.getRoomTemperature();
					lcd.displayLine1(troom, tterr);
					Util.traceTemperature(Terrarium.traceFolder + "/" +  Terrarium.traceTempFilename, now, "r=%d t=%d", troom, tterr);
					// - check timers
					terrarium.checkTimers();
					// - check sprayerrule
					terrarium.checkSprayerRule();
					// - check rulesets
					terrarium.checkRules();
					// Check if tracing should be switched off (max 1 day)
					terrarium.checkTrace();
				}
				if (now.getHour() != currentHour) {
					// An hour has passed
//					System.out.println(now.format(dtfmt) + " An hour has passed");
					currentHour = now.getHour();
					if (!terrarium.isTraceOn()) {
						// Start trace on the whole hour
						terrarium.setTrace(true);
					}
					// Each hour
					// - decrement lifecycle value
					terrarium.decreaseLifetime(1);
					terrarium.saveLifecycleCounters();
				}
			}
		}
	}
}
