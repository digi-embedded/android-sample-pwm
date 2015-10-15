/**
 * Copyright (c) 2014-2015 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */

package com.digi.android.pwm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

/**
 * Utility class used to determine development board parameters.
 */
class BoardUtils {
	
	// Constants.
	private final static String BOARD_VERSION_FILE_MX6SBC = "/sys/kernel/ccimx6sbc/mod_ver";
	private final static String BOARD_VERSION_FILE_CC6SBC = "/proc/device-tree/digi,hwid,hv";
	private final static String KERNEL_VERSION_FILE = "/proc/version";
	
	private final static String PROC_VERSION_REGEX =
					"\\w+\\s+" + /* ignore: Linux */
					"\\w+\\s+" + /* ignore: version */
					"([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
					"\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /* group 2: (xxxxxx@xxxxx.constant) */
					"\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+" + /* ignore: (gcc ..) */
					"([^\\s]+)\\s+" + /* group 3: #26 */
					"(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
					"(.+)"; /* group 4: date */
	
	private final static String LOG_TAG = "Board Utils";

	/**
	 * Checks whether the module is a CC i-MX6 SBC
	 *
	 * @return True if the board is a MX6 board, false otherwise.
	 */
	public static boolean isMX6SBC() {
		return (readFile(new File(BOARD_VERSION_FILE_MX6SBC)) != null) ||
				(readFile(new File(BOARD_VERSION_FILE_CC6SBC)) != null);
	}
	/**
	 * Retrieves the system kernel version.
	 * 
	 * @return The system kernel version, null if it could not be read.
	 */
	public static String getKernelVersion() {
		String procVersionStr = readFile(new File(KERNEL_VERSION_FILE));
		if (procVersionStr == null)
			return null;
		Pattern p = Pattern.compile(PROC_VERSION_REGEX);
		Matcher m = p.matcher(procVersionStr);
		if (!m.matches()) {
			Log.e(LOG_TAG, "Read kernel version did not match regex: " + procVersionStr);
			return null;
		} else if (m.groupCount() < 4) {
			Log.e(LOG_TAG, "Read kernel version only returned " + m.groupCount() + " groups");
			return null;
		} else {
			return m.group(1);
		}
	}
	
	/**
	 * Reads the the first line of the given file.
	 *
	 * <p>Attempts to read the first line of the given file returning it as
	 * a String.</p>
	 * 
	 * @param file File to read first line from.
	 */
	private static String readFile(File file) {
		if (!file.exists())
			return null;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file), 8);
			String value = reader.readLine();
			reader.close();
			return value.trim();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
