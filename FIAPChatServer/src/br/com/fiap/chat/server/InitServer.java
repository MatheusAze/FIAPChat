package br.com.fiap.chat.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import br.com.fiap.chat.utils.Console;
import br.com.fiap.chat.vo.Room;
import br.com.fiap.chat.vo.User;

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

		User userAdmin = new User();
		userAdmin.setName("Admin");
		userAdmin.setRegistred(false);
		userAdmin.setUserIp(hostServer);

		Room lobby = new Room();
		lobby.setName(Server.LOBBY);
		lobby.setDescription("Voce esta no lobby! Converse com as pessoas aqui ou procure uma sala que tenha assuntos do seu interesse.");
		lobby.setOwner(userAdmin);

		Server.rooms.add(lobby);
		
		(new ServerReceiver(PORT)).initReceiver();

		Console.getConsole().println("> Servidor iniciado com sucesso!");

	}
}
