package br.com.fiap.chat.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import br.com.fiap.chat.utils.Console;

public class InitServer {
	private static final Integer PORT = 9009;

	public static void main(String[] args) {
		String hostServer = "";
		try {
			hostServer = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.err
					.println(">> ERRO NÃO-FATAL: Não foi possível identificar o IP do Servidor.");
		}
		
		Console.getConsole().println("> Iniciando servidor." + hostServer);

		(new ServerReceiver(PORT)).initReceiver();
		
		Console.getConsole().println("> Servidor iniciado com sucesso!");

	}
}
