package com.liferay.petra.io.internal.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class InetAddressUtil {

	public static String getLocalHostName() throws Exception {
		return LocalHostNameHolder._LOCAL_HOST_NAME;
	}

	public static InetAddress getLocalInetAddress() throws Exception {
		Enumeration<NetworkInterface> enu1 =
			NetworkInterface.getNetworkInterfaces();

		while (enu1.hasMoreElements()) {
			NetworkInterface networkInterface = enu1.nextElement();

			Enumeration<InetAddress> enu2 = networkInterface.getInetAddresses();

			while (enu2.hasMoreElements()) {
				InetAddress inetAddress = enu2.nextElement();

				if (!inetAddress.isLoopbackAddress() &&
					(inetAddress instanceof Inet4Address)) {

					return inetAddress;
				}
			}
		}

		throw new RuntimeException("No local internet address");
	}

	public static InetAddress getLoopbackInetAddress()
		throws UnknownHostException {

		return InetAddress.getByName("127.0.0.1");
	}

	private static class LocalHostNameHolder {

		private static final String _LOCAL_HOST_NAME;

		static {
			try {
				InetAddress inetAddress = getLocalInetAddress();

				_LOCAL_HOST_NAME = inetAddress.getHostName();
			}
			catch (Exception e) {
				throw new ExceptionInInitializerError(e);
			}
		}

	}

}
