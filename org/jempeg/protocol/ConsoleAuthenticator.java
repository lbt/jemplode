package org.jempeg.protocol;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.inzyme.exception.ChainedRuntimeException;

/**
 * An authenticator that can input a password from stdin.
 * 
 * @author Mike Schrag
 */
public class ConsoleAuthenticator implements IAuthenticator {
	public PasswordAuthentication requestPassword(String _prompt) {
		try {
			System.out.print(_prompt);
			System.out.print(": ");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String password = br.readLine();
			PasswordAuthentication authentication = new PasswordAuthentication(password, false);
			return authentication;
		}
		catch (Throwable t) {
			throw new ChainedRuntimeException("Failed to read password from console.", t);
		} 
	}
}
