package br.com.fiap.chat;

import java.net.UnknownHostException;

public class InitClientReceiver {

	private static Integer PORT_LISTEN = 9010;

	public static void main(String[] args) throws UnknownHostException {
		(new ClientReceiver(PORT_LISTEN)).initReceiver();
	}

}
