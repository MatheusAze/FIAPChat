package br.com.fiap.chat;

import java.io.FileNotFoundException;
import java.net.InetAddress;
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
				if (operationStr.charAt(0) == '0' // Not used yet
						&& (!operationStr.contains(Commands.DEFAULT_COMMAND.toString())  // not contains default command
						 && !operationStr.equalsIgnoreCase(Commands.NOTIFICATION.toString()))) { // not a notification from server
					
					operationStr = operationStr.substring(2);
					command = Commands.valueOf(operationStr);
					// Writes the operation without the '0-' in front of it, 
					// indicating that this operations was already used
					CommonMemory.writeInfo(operationStr);
				} else {
					Commands commandFromInput = Commands.getCommandByFriendlyCommand(message);
					if (commandFromInput != null) { // command exists
						command = commandFromInput;
					}
				}

				Message msg = treatLocalOperations(command);
				if (msg == null) {// no local treatment
					msg = new Message();
					msg.setMessage(message);
					msg.setCommand(command);
				}

				Client.sender.sendMessage(msg);

			} catch (FileNotFoundException e) {
				System.err.println("> CHAT: ERRO FATAL! OPERAÇÂO INDEFINIDA!");
				System.exit(-1);
			}

		}
	}

	private static void initConnection() {
		Console.getConsole().println("> Chat iniciado");
		boolean validIp = false;
		String ipServer = null;
		while (!validIp) {
			ipServer = Console.getConsole().readLine("> Digite o IP do servidor: ");
			try {
				InetAddress.getByName(ipServer);
				validIp = true;
			} catch (UnknownHostException e) {
				Console.getConsole().println("> O servidor " + ipServer + " não é conhecido.");
			}
		}

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

	/**
	 * Method that treats consecutive user input when needed
	 * @param command
	 * @return
	 */
	private static Message treatLocalOperations(Commands command) {
		Message msg = null;
		switch (command) {
		case SEND_CREATE_ROOM:
			msg = Client.requestUserInputForRoomCreation();
			break;
		case SEND_MESSAGE_PRIVATE:
			msg = Client.requestUserInputForPrivateMessage();
		default:
			break;
		}

		return msg;
	}

}
