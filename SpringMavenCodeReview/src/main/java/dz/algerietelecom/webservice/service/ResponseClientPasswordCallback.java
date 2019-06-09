package dz.algerietelecom.webservice.service;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;

public class ResponseClientPasswordCallback implements CallbackHandler {

	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		WSPasswordCallback ps = (WSPasswordCallback) callbacks[0];
	        ps.setPassword("PAIEMENT_RECHARGE");
	   
	}

}
