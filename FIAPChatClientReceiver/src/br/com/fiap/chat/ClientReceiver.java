package br.com.fiap.chat;

import br.com.fiap.chat.utils.Commands;
import br.com.fiap.chat.utils.Console;
import br.com.fiap.chat.utils.Receiver;
import br.com.fiap.chat.vo.Client;
import br.com.fiap.chat.vo.Message;

public class ClientReceiver extends Receiver {

	public ClientReceiver(int port) {
		super(port);
	}

	@Override
	protected void treatMessage(String sourceIp, Integer sourcePort, Message message) {
				
		String msg = message.getMessage().trim();
		Commands operation = message.getCommand();
		
		switch(operation) {
			case NOTIFICATION:
				Console.getConsole().println(msg);
				break;	
			case REQUEST_USER_NAME:
				Console.getConsole().println(msg);			
				Client.setInputCommand(Commands.SEND_USER_NAME);
				break;
			case DEFAULT_COMMAND:
			case INVALID_COMMAND:
				Console.getConsole().println(msg);			
				Client.setInputCommand(Commands.DEFAULT_COMMAND);			
				break;	
			case REQUEST_CREATE_ROOM:
				Console.getConsole().println(msg);
				Client.setInputCommand(Commands.SEND_CREATE_ROOM);
				break;
			case REQUEST_JOIN_ROOM:
				Console.getConsole().println(msg);			
				Client.setInputCommand(Commands.SEND_JOIN_ROOM);
				break;	
			case REQUEST_MESSAGE:
				Console.getConsole().println(msg);
				Client.setInputCommand(Commands.SEND_MESSAGE);
				break;
			case REQUEST_MESSAGE_PRIVATE:
				Console.getConsole().println(msg);
				Client.setInputCommand(Commands.SEND_MESSAGE_PRIVATE);
			default:
				break;
		}
	}

}
