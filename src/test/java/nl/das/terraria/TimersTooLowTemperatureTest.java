/*
 * Copyright © 2021 Dutch Arrow Software - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the Apache Software License 2.0.
 *
 * Created 14 Aug 2021.
 */

package nl.das.terraria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.das.terraria.Util;
import nl.das.terraria.objects.DeviceState;
import nl.das.terraria.objects.Terrarium;

/**
 *
 */
public class TimersTooLowTemperatureTest {

	public static Terrarium terrarium;

	@BeforeAll
	public static void beforeAll () throws IOException {
		Terrarium.traceFolder = "src/test/resources/tracefiles";
		String json = Files.readString(Paths.get("src/test/resources/settings.json"));
		terrarium = Terrarium.getInstance(json);
		assertNotNull(terrarium, "Terrarium object cannot be null");
		terrarium.setNow(LocalDateTime.now());
		terrarium.initMockDevices();
		terrarium.initDeviceState();
		terrarium.initSensors();
		terrarium.initRules();
		terrarium.setTrace(false);
		terrarium.setSensors(21, 21); // Too low temperature, so rules will be activated
	}

	@BeforeEach
	public void before () {
	}

	@AfterEach
	public void after () {
	}

	@AfterAll
	public static void afterAll () {
	}

	@Test
	public void testAllTimers() throws InterruptedException {
		{
			// Time: 05:00:00
			LocalDateTime now = LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(5, 0, 0));
			terrarium.setNow(now);
			terrarium.setTrace(true);
			TimeUnit.SECONDS.sleep(1);
		}
		{
			// Time: 05:00:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(5, 0, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// No device should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {0,      0,      0,      0,      0,       0,      0,    0,       0,    0,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			terrarium.checkTrace();
			assertTrue(terrarium.isTraceOn());
			traceTemperature();
		}
		{
			// Time: 06:00:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(6, 0, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// No device should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {0,      0,      0,      0,      0,       0,      0,    0,      -1,    0,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be inactive
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   0,      0};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time: 06:45:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(6, 45, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// No device should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {0,      0,      0,      0,      0,       0,      0,    0,       0,    0,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 09:00:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(9, 0, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// Only light1 should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {-1,     0,      0,      0,      0,       0,      0,    0,       0,    0,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 09:30:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(9, 30, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			//light1 and light2 should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {-1,     -1,     0,      0,     0,        0,      0,    0,       0,    0,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 10:00:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(10, 0, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			//light1, light2 and light3 should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {-1,     -1,     -1,     0,      0,       0,      0,    0,       0,    0,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 10:05:00
			LocalDateTime now = LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(10, 5, 0));
			terrarium.setNow(now);
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// Sprayer timer will be activated, so check if the sprayer rule is also active
			long tm = Util.now(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(10, 5, 30)));
			//light1, light2, light3, sprayer should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {-1,     -1,     -1,     0,      0,       0,      0,    tm,      0,    0,      0};
			checkDeviceState(expected);
			assertTrue(terrarium.isSprayerRuleActive(), "Sprayer rule must be active");
			// ... and the rules for fan_in and fan_out should not be inactive
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   0,      0};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 10:05:30
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(10, 5, 30)));
			terrarium.checkDevices();
			// Sprayer should be switched off
			//light1, light2, light3 should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {-1,     -1,     -1,     0,      0,       0,      0,    0,       0,     0,     0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   0,      0};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 10:15:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(10, 15, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			//light1, light2, light3, light4 fan_in and fan_out should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {-1,     -1,     -1,     -1,     0,       0,      0,    0,       0,    -1,     -1};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be inactive
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   0,      0};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 10:20:00
			// The delay should be over, so sprayer rule actions must be active
			LocalDateTime now = LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(10, 20, 0));
			terrarium.setNow(now);
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			//light1, light2, light3, light4 should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {-1,     -1,     -1,     -1,     0,       0,      0,    0,       0,    -1,     -1};
			checkDeviceState(expected);
			// ... and the rule for fan_in and fan_out should be inactive
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   0,      0};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 10:25:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(10, 25, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			//light1, light2, light3, light4 should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {-1,     -1,     -1,     -1,     0,       0,      0,    0,       0,    0,     0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 10:35:00
			// The sprayer rule actions must be active
			LocalDateTime now = LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(10, 35, 0));
			terrarium.setNow(now);
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			//light1, light2, light3, light4, light5 should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {-1,     -1,     -1,     -1,     0,       0,      0,    0,       0,    -2,      0};
			checkDeviceState(expected);
			// ... and the rule for fan_in and fan_out should be inactive
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 11:15:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(11, 15, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			//light1, light2, light3, light4, light5 should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {-1,     -1,     -1,     -1,     -1,      0,      0,    0,       0,    -2,     0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 15:30:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 1), LocalTime.of(15, 30, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			//light1, light2, light3, light4, fan_in and fan_out should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {-1,     -1,     -1,     -1,     -1,      0,      0,    0,       0,    -2,     0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be inactive
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 15:40:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 1), LocalTime.of(15, 40, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			//light1, light2, light3, light4 should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {-1,     -1,     -1,     -1,     -1,      0,      0,    0,       0,    -2,     0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 20:00:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(20, 0, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			//light1, light2, light3, light4, fan_in and fan_out should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {-1,     -1,     -1,     -1,     -1,      0,      -1,   0,       0,    -2,     0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 20:05:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(20, 5, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			//light1, light2, light3, light4, fan_in and fan_out should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {-1,     -1,     -1,     -1,     -1,      0,      -1,   0,       0,    -2,     -1};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be inactive
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   0,      0};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 20:15:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(20, 15, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			//light1, light2, light3, light4 should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {-1,     -1,     -1,     -1,     -1,      0,      0,    0,       0,    -2,     0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 21:00:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(21, 0, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// light2, light3, light4, light5 should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = { 0,     -1,     -1,     -1,     -1,      0,      0,    0,       0,    -2,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 21:30:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(21, 30, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// light3, light4, light5 should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = { 0,     0,      -1,     -1,     -1,      0,      0,    0,       0,    -2,     0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,      -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 22:00:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(22, 0, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// light4 should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = { 0,     0,      0,      -1,     0,       0,      0,    0,       0,    -2,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 22:15:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(22, 15, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// light6 should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = { 0,      0,     0,      0,      0,       -1,     0,    0,       0,    -2,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be inactive
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 23:00:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 3), LocalTime.of(23, 0, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// No device should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = { 0,      0,     0,      0,      0,       -1,     0,    0,       0,     0,     0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be inactive
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			traceTemperature();
		}
		{
			// Time 05:10:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 8, 4), LocalTime.of(5, 10, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// No device should be switched on
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = { 0,      0,     0,      0,      0,       -1,     0,    0,       0,     0,     0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be inactive
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
			terrarium.setTrace(false);
		}

	}

	private void traceTemperature() {
		if (terrarium.isTraceOn()) {
			Util.traceTemperature(Terrarium.traceFolder + "/" + Terrarium.traceTempFilename, terrarium.getNow(), "r=%d t=%d", terrarium.getRoomTemperature(), terrarium.getTerrariumTemperature());
		}

	}

	private void checkDeviceState(long[] e) {
		DeviceState[] deviceStates = terrarium.getDeviceStates();
		assertEquals(e[ 0], deviceStates[terrarium.getDeviceIndex("light1")].getOnPeriod());
		assertEquals(e[ 1], deviceStates[terrarium.getDeviceIndex("light2")].getOnPeriod());
		assertEquals(e[ 2], deviceStates[terrarium.getDeviceIndex("light3")].getOnPeriod());
		assertEquals(e[ 3], deviceStates[terrarium.getDeviceIndex("light4")].getOnPeriod());
		assertEquals(e[ 4], deviceStates[terrarium.getDeviceIndex("uvlight")].getOnPeriod());
		assertEquals(e[ 5], deviceStates[terrarium.getDeviceIndex("light6")].getOnPeriod());
		assertEquals(e[ 6], deviceStates[terrarium.getDeviceIndex("pump")].getOnPeriod());
		assertEquals(e[ 7], deviceStates[terrarium.getDeviceIndex("sprayer")].getOnPeriod());
		assertEquals(e[ 8], deviceStates[terrarium.getDeviceIndex("mist")].getOnPeriod());
		assertEquals(e[ 9], deviceStates[terrarium.getDeviceIndex("fan_in")].getOnPeriod());
		assertEquals(e[10], deviceStates[terrarium.getDeviceIndex("fan_out")].getOnPeriod());
	}

	private void checkRuleActiveState(int[] e) {
		assertEquals(e[ 0], terrarium.getRuleActive("light1"));
		assertEquals(e[ 1], terrarium.getRuleActive("light2"));
		assertEquals(e[ 2], terrarium.getRuleActive("light3"));
		assertEquals(e[ 3], terrarium.getRuleActive("light4"));
		assertEquals(e[ 4], terrarium.getRuleActive("uvlight"));
		assertEquals(e[ 5], terrarium.getRuleActive("light6"));
		assertEquals(e[ 6], terrarium.getRuleActive("pump"));
		assertEquals(e[ 7], terrarium.getRuleActive("sprayer"));
		assertEquals(e[ 8], terrarium.getRuleActive("mist"));
		assertEquals(e[ 9], terrarium.getRuleActive("fan_in"));
		assertEquals(e[10], terrarium.getRuleActive("fan_out"));

	}
}
