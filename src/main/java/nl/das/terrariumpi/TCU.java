/*
 * Copyright © 2021 Dutch Arrow Software - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the Apache Software License 2.0.
 *
 * Created 05 Aug 2021.
 */

package nl.das.terrariumpi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.util.Console;

import nl.das.terrariumpi.hw.LCD;
import nl.das.terrariumpi.objects.Terrarium;
import nl.das.terrariumpi.rest.RestServer;

public class TCU {

	private static Logger log = LoggerFactory.getLogger(TCU.class);

	private static RestServer server;

	public static void main (String[] args) throws InterruptedException {
		// Initialize the LCD
		LCD lcd = new LCD();
		lcd.init(2, 16);
		lcd.write(0, "Initialize....");

	    Console console = new Console();
		// print program title/header
		console.title("Starting the application");
		// allow for user to exit program using CTRL-C
		console.promptForExit();
		// Initialize and start the webserver
		server = new RestServer("0.0.0.0", 8080);
		server.start();
		// Retrieve the settings from disk
		Terrarium terrarium = null;
		try {
			String json = Files.readString(Paths.get("settings.json"));
			terrarium = Terrarium.getInstance(json);
		} catch (NoSuchFileException e) {
			terrarium = Terrarium.getInstance();
			terrarium.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		LocalDateTime now = LocalDateTime.now();
		terrarium.setNow(now);
		// Initially do not trace
		terrarium.setTrace(false);
		// Initialize the devices
		terrarium.initDevices();
		// Initialize device state
		terrarium.initDeviceState();
		// Retrieve the lifecycle values from disk
		try {
			String json = Files.readString(Paths.get("lifecycle.txt"));
			String lns[] = json.split("\n");
			for (String ln : lns) {
				String lp[] = ln.split("=");
				Terrarium.getInstance().setDeviceLifecycle(lp[0], Integer.parseInt(lp[1]));
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
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
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
		log.info("Initialization done, start loop");
		while (true) {
			now = LocalDateTime.now();
			Terrarium.getInstance().setNow(now);
			if (now.getSecond() != currentSec) {
				currentSec = now.getSecond();
				// A second has passed
				// Each second check devices
				Terrarium.getInstance().checkDevices();
				if (now.getMinute() != currentMin) {
					currentMin = now.getMinute();
					// A minute has passed
//					console.println(LocalDateTime.now().format(fmt) + " A minute has passed");
					// Each minute
					// - display temperature on LCD line 1
					Terrarium.getInstance().readSensorValues();
					tterr = Terrarium.getInstance().getTerrariumTemperature();
					troom = Terrarium.getInstance().getRoomTemperature();
					lcd.displayLine1(troom, tterr);
					Util.traceTemperature(now, "r=%d t=%d", troom, tterr);
					// - check timers
					Terrarium.getInstance().checkTimers();
					// - check sprayerrule
					Terrarium.getInstance().checkSprayerRule();
					// - check rulesets
					Terrarium.getInstance().checkRules();
					// Check if tracing should be switched off (max 1 day)
					Terrarium.getInstance().checkTrace();
				}
				if (now.getHour() != currentHour) {
					currentHour = now.getHour();
					// An hour has passed
//					console.println(LocalDateTime.now().format(fmt) + " An hour has passed");
					// Each hour
					// - decrement lifecycle value
					Terrarium.getInstance().decreaseLifetime(1);
					Terrarium.getInstance().saveLifecycleCounters();
				}
			}
		}
	}
}
