/*
 * Copyright © 2021 Dutch Arrow Software - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the Apache Software License 2.0.
 *
 * Created 12 Aug 2021.
 */


package nl.das.terrariumpi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import nl.das.terrariumpi.objects.Terrarium;

/**
 *
 */
public class Util {

	public final static String DTRACEFILE = "tracefile_dev";
	public final static String TTRACEFILE = "tracefile_temp";

	private static DateTimeFormatter dtfmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


	public static String cvtPeriodToString(long l) {
		if (l == -1) {
			return "endlessly";
		}
		if (l == -2) {
			return "until ideal temperature has been reached";
		}
		if (l > 0) {
			LocalTime t = Util.ofEpochSecond(l).toLocalTime();
			return String.format("until %02d:%02d:%02d", t.getHour(), t.getMinute(), t.getSecond());
		}
		return "";
	}

	public static long cvtStringToMinutes(String hhmm) {
		return (Long.parseLong(hhmm.split(":")[0]) * 60L) + (Long.parseLong(hhmm.split(":")[1]));
	}

	public static long now(LocalDateTime now) {
		return now.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(Instant.now()));
	}

	public static LocalDateTime ofEpochSecond(long epochseconds) {
		return LocalDateTime.ofEpochSecond(epochseconds, 0, ZoneId.systemDefault().getRules().getOffset(Instant.now()));
	}

	public static void createStateTraceFile(String tracefile) {
		try {
			Files.deleteIfExists(Paths.get(tracefile));
			Files.createFile(Paths.get(tracefile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createStateTraceFile() {
		createStateTraceFile(DTRACEFILE);
	}

	public static void createTemperatureTraceFile(String tracefile) {
		try {
			Files.deleteIfExists(Paths.get(tracefile));
			Files.createFile(Paths.get(tracefile));
			TimeUnit.SECONDS.sleep(1);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void createTemperatureTraceFile() {
		createTemperatureTraceFile(TTRACEFILE);
	}

	public static void traceStateBase(String tracefile, LocalDateTime now, String fmt, Object ...args) {
		try {
			if (Terrarium.getInstance().isTraceOn()) {
				String nowstr = now.format(dtfmt);
				String format = nowstr + " " + fmt;
				Files.writeString(
						Paths.get(tracefile),
						String.format(format + "\n", args),
						StandardOpenOption.APPEND
				);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void traceState(LocalDateTime now, String fmt, Object ...args) {
		traceStateBase(DTRACEFILE, now, fmt, args);
	}

	public static void traceTemperatureBase(String tracefile, LocalDateTime now, String fmt, Object ...args) {
		try {
			if (Terrarium.getInstance().isTraceOn()) {
				String nowstr = now.format(dtfmt);
				String format = nowstr + " " + fmt;
				Files.writeString(
						Paths.get(tracefile),
						String.format(format + "\n", args),
						StandardOpenOption.APPEND
				);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void traceTemperature(LocalDateTime now, String fmt, Object ...args) {
		traceTemperatureBase(TTRACEFILE, now, fmt, args);
	}
}
