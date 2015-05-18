package br.com.fiap.chat;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;

import br.com.fiap.chat.utils.Commands;
import br.com.fiap.chat.utils.CommonMemory;
import br.com.fiap.chat.utils.Console;
import br.com.fiap.chat.vo.Client;
import br.com.fiap.chat.vo.Message;

public class InitClientSender {

	public static void main(String[] args) throws UnknownHostException {
		initConnection();

		initConsoleReader();
	}

	private static void initConsoleReader() {
		while (true) {
			try {
				Console.getConsole().clear();
				String message = Console.getConsole().readLine();
				
				
				
				String operationStr = CommonMemory.readInfo();
				Commands command = Commands.SEND_MESSAGE;
				if(operationStr.charAt(0) == '0') {
					operationStr = operationStr.substring(2);
					System.out.println("-" + operationStr + "-");
					command = Commands.valueOf(operationStr);
					CommonMemory.writeInfo(operationStr);
				}
				
				Message msg = new Message();
				msg.setMessage(message);
				msg.setCommand(command);
				
				Client.sender.sendMessage(msg);
				
			} catch (FileNotFoundException e) { 
				System.err.println("> CHAT: ERRO FATAL! OPERAÇÂO INDEFINIDA!");
				System.exit(-1);
			}

		}
	}

	private static void initConnection() {
		Console.getConsole().println("> Chat iniciado");
		String ipServer = Console.getConsole().readLine("> Digite o IP do servidor: ");

		try {
			Console.getConsole().println("> Conectando com o servidor...");
			Client.sender = new ClientSender(ipServer);
		} catch (Exception e) {
			System.err.println(">> Erro fatal! Impossível conectar com " + ipServer);
			System.exit(-1);
		}

		Message msg = new Message();
		msg.setMessage("");
		msg.setCommand(Commands.ACCESS);
		Client.sender.sendMessage(msg);
	}

}
