/*
 * Copyright © 2022 Dutch Arrow Software - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the Apache Software License 2.0.
 *
 * Created 16 Jul 2022.
 */


package nl.das.terraria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
public class SpayerRulesTest {

	private static Terrarium terrarium;

	@BeforeAll
	public static void beforeAll () throws IOException {
		String json = Files.readString(Paths.get("src/test/resources/settings_sprayertest.json"));
		terrarium = Terrarium.getInstance(json);
		assertNotNull(terrarium, "Terrarium object cannot be null");
		terrarium.setNow(LocalDateTime.now());
		terrarium.initMockDevices();
		terrarium.initDeviceState();
		terrarium.initSensors();
		terrarium.initRules();
		terrarium.setTrace(false);
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
	public void testSprayerRuleWithIdealTemperature() {
		terrarium.setSensors(21, 26); // ideal temparature
		{
			// Time: 05:00:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(5, 0, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// No device should be switched on
			// 0 (off), -1 (on by timer), -2 (on by rule)
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {0,      0,      0,      0,      0,       0,      0,    0,       0,    0,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			// -1 = no rule defined, 0 rule not active, 1 rule active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
		}
		{
			// Time: 10:30:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(10, 30, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// rules are switched on, fan_in and fan_out should be switched on
			// 0 (off), -1 (on by timer), -2 (on by rule)
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {0,      0,      0,      0,      0,       0,      0,    0,       0,    -1,      -1};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should not be active
			// -1 = no rule defined, 0 rule not active, 1 rule active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,    0,     0};
			checkRuleActiveState(expra);
		}
		{
			// Time: 11:00:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(11, 0, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// 0 (off), -1 (on by timer), -2 (on by rule)
			// sprayer should be switched on for 30 seconds and fan_in and fan_out should be off
			long tm = Util.now(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(11, 0, 30)));
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {0,      0,      0,      0,      0,       0,      0,    tm,      0,    0,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should not be active
			// -1 = no rule defined, 0 rule not active, 1 rule active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,    0,     0};
			checkRuleActiveState(expra);
		}
		{
			// Time: 11:00:30
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(11, 0, 30)));
			terrarium.checkDevices();
			// Don't check timers because there is not a minute passed.
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// sprayer should be switched off
			// 0 (off), -1 (on by timer), -2 (on by rule)
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {0,      0,      0,      0,      0,       0,      0,    0,       0,    0,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should still not be active
			// -1 = no rule defined, 0 rule not active, 1 rule active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   0,      0};
			checkRuleActiveState(expra);
		}
		{
			// Time: 11:15:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(11, 15, 00)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// fan_in and fan_out should be switched on for 15 minutes and light1 till light4 and uvlight should be on
			long tm = Util.now(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(11, 30, 0)));
			// 0 (off), -1 (on by timer), -2 (on by rule)
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {0,      0,      0,      0,      0,       0,      0,    0,       0,    tm,     tm};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should still not be active
			// -1 = no rule defined, 0 rule not active, 1 rule active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   0,      0};
			checkRuleActiveState(expra);
		}
		{
			// Time: 11:30:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(11, 30, 00)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// no device should be on
			// 0 (off), -1 (on by timer), -2 (on by rule)
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {0,      0,      0,      0,      0,       0,      0,    0,       0,    0,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active again
			// -1 = no rule defined, 0 rule not active, 1 rule active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
		}
		/*
		 */
	}

	@Test
	public void testSprayerRuleWithLowTemperature() {
		terrarium.setSensors(21, 21); // temparature too low
		{
			// Time: 05:00:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(5, 0, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// No device should be switched on
			// 0 (off), -1 (on by timer), -2 (on by rule)
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {0,      0,      0,      0,      0,       0,      0,    0,       0,    0,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active
			// -1 = no rule defined, 0 rule not active, 1 rule active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
		}
		{
			// Time: 10:30:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(10, 30, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// rules are switched on, fan_in and fan_out should be switched on
			// 0 (off), -1 (on by timer), -2 (on by rule)
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {0,      0,      0,      0,      0,       0,      0,    0,       0,    -1,     -1};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should not be active
			// -1 = no rule defined, 0 rule not active, 1 rule active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,    0,     0};
			checkRuleActiveState(expra);
		}
		{
			// Time: 11:00:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(11, 0, 0)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// 0 (off), -1 (on by timer), -2 (on by rule)
			// sprayer should be switched on for 30 seconds and fan_in and fan_out should be switched off
			long tm = Util.now(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(11, 0, 30)));
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {0,      0,      0,      0,      0,       0,      0,    tm,      0,    0,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should not be active
			// -1 = no rule defined, 0 rule not active, 1 rule active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,    0,     0};
			checkRuleActiveState(expra);
		}
		{
			// Time: 11:00:30
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(11, 0, 30)));
			terrarium.checkDevices();
			// Don't check timers because there is not a minute passed.
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// sprayer should be switched off
			// 0 (off), -1 (on by timer), -2 (on by rule)
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {0,      0,      0,      0,      0,       0,      0,    0,       0,    0,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should still not be active
			// -1 = no rule defined, 0 rule not active, 1 rule active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   0,      0};
			checkRuleActiveState(expra);
		}
		{
			// Time: 11:15:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(11, 15, 00)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// fan_in and fan_out should be switched on for 15 minutes
			long tm = Util.now(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(11, 30, 0)));
			// 0 (off), -1 (on by timer), -2 (on by rule)
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {0,      0,      0,      0,      0,       0,      0,    0,       0,    tm,     tm};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should still not be active
			// -1 = no rule defined, 0 rule not active, 1 rule active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   0,      0};
			checkRuleActiveState(expra);
		}
		{
			// Time: 11:30:00
			terrarium.setNow(LocalDateTime.of(LocalDate.of(2021, 1, 8), LocalTime.of(11, 30, 00)));
			terrarium.checkDevices();
			terrarium.checkTimers();
			terrarium.checkSprayerRule();
			terrarium.checkRules();
			// fan in should be on because of rule
			// 0 (off), -1 (on by timer), -2 (on by rule)
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			long[] expected = {0,      0,      0,      0,      0,       0,      0,    0,       0,    -2,      0};
			checkDeviceState(expected);
			// ... and the rules for fan_in and fan_out should be active again
			// -1 = no rule defined, 0 rule not active, 1 rule active
			//                 light1, light2, light3, light4, uvlight, light6, pump, sprayer, mist, fan_in, fan_out
			int[] expra     = {-1,     -1,     -1,     -1,     -1,      -1,     -1,   -1,      -1,   1,      1};
			checkRuleActiveState(expra);
		}
		/*
		 */
	}

	private void checkDeviceState(long[] e) {
		DeviceState[] deviceStates = terrarium.getDeviceStates();
		// onPeriod = 0 (off), -1 (on by timer), -2 (on by rule)
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
		// -1 = no rule defined, 0 rule not active, 1 rule active
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
