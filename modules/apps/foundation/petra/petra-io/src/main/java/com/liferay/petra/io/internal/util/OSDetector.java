package com.liferay.petra.io.internal.util;

import java.io.File;

public class OSDetector {

	public static boolean isWindows() {
		if (_windows != null) {
			return _windows.booleanValue();
		}

		if (File.pathSeparator.equals(";")) {
			_windows = Boolean.TRUE;
		}
		else {
			_windows = Boolean.FALSE;
		}

		return _windows.booleanValue();
	}

	private static Boolean _windows;

}
