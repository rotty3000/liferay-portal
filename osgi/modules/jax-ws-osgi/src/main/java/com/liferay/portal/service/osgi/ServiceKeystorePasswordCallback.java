package com.liferay.portal.service.osgi;

import org.apache.ws.security.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public class ServiceKeystorePasswordCallback implements CallbackHandler {

	private Map<String, String> passwords =
		new HashMap<String, String>();

	public ServiceKeystorePasswordCallback() {
		passwords.put("myservicekey", "skpass");
		passwords.put("clientUserNameToken", "clientPassword");
	}

	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (int i = 0; i < callbacks.length; i++) {
			WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];

			String pass = passwords.get(pc.getIdentifier());
			if (pass != null) {
				pc.setPassword(pass);
				return;
			}
		}
	}
}