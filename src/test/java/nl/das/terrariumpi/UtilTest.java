/*
 * Copyright © 2021 Dutch Arrow Software - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the Apache Software License 2.0.
 *
 * Created 14 Aug 2021.
 */

package nl.das.terrariumpi;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import nl.das.terrariumpi.Util;

/**
 *
 */
public class UtilTest {

	private static final String TRACEFILE = "tracefile_test";

	@BeforeAll
	public static void beforeAll () {
	}

	@BeforeEach
	public void before () {
	}

	@AfterEach
	public void after () {
	}

	@AfterAll
	public static void afterAll () {
		try {
			Files.deleteIfExists(Paths.get(TRACEFILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Disabled
	@Test
	public void testTrace () throws FileNotFoundException, IOException {
		// Create
		Util.createStateTraceFile(TRACEFILE);
		Util.traceStateBase(TRACEFILE, LocalDateTime.now(), "test%d", 1);
		Util.traceStateBase(TRACEFILE, LocalDateTime.now(), "test%d", 2);
		Util.traceStateBase(TRACEFILE, LocalDateTime.now(), "test%d", 3);
		// Now check
		File file = new File(TRACEFILE);
		try (FileReader fr = new FileReader(file)) {
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			assertNotNull(line, "End-of-file");
			assertTrue(line.endsWith("test1"));
			line = br.readLine();
			assertNotNull(line, "End-of-file");
			assertTrue(line.endsWith("test2"));
			line = br.readLine();
			assertNotNull(line, "End-of-file");
			assertTrue(line.endsWith("test3"));
			line = br.readLine();
			assertNull(line, "EOF not reached");
		}
	}

	@Test
	public void test_cvtPeriodToString() {
		LocalDateTime t = LocalDateTime.of(2021, 8, 1, 13, 42, 28);
		// Time = 13:42:28
		assertEquals("until 13:42:28", Util.cvtPeriodToString(t.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(Instant.now()))), "Incorrect time");
		// Time = 09:42:00
		t = LocalDateTime.of(2021, 8, 1, 9, 42, 0);
		assertEquals("until 09:42:00", Util.cvtPeriodToString(t.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(Instant.now()))), "Incorrect time");
		// Time = 23:00:00
		t = LocalDateTime.of(2021, 8, 1, 23, 0, 0);
		assertEquals("until 23:00:00", Util.cvtPeriodToString(t.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(Instant.now()))), "Incorrect time");
	}

}
