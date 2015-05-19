package br.com.fiap.chat.vo;

import br.com.fiap.chat.ClientSender;
import br.com.fiap.chat.utils.Commands;
import br.com.fiap.chat.utils.CommonMemory;
import br.com.fiap.chat.utils.Console;

public class Client {

	public static ClientSender sender;

	public static void setInputCommand(Commands operation) {
		/**
		 * Indicates to the reader that this info wasn't read yet.
		 */
		final String NOT_READ = "0-";
		
		CommonMemory.writeInfo(NOT_READ + operation.toString());
	}
	
	public static Message requestUserInputForRoomCreation() {
		String message = null;
		
		String name = Console.getConsole().readLine("Digite o nome da Sala: ");
		String description = Console.getConsole().readLine("Digite a descricao da Sala: ");
				
		Room room = new Room();
		room.setName(name);
		room.setDescription(description);
		message = Room.toJsonString(room);
		
		Message msg = new Message();	
		msg.setCommand(Commands.SEND_CREATE_ROOM);
		msg.setMessage(message);
		
		return msg;
	}
	
	public static void requestUserInputForPrivateMessage() {	
		String destUser = Console.getConsole().readLine("CHAT > Digite o nome do usuário");
		String privateMessage = Console.getConsole().readLine("CHAT > Escreva a mensagem");
		
		Message msg = new Message();
		msg.setCommand(Commands.SEND_MESSAGE_PRIVATE);
		msg.getAdditionalInfo().put("destUser", destUser);
		msg.setMessage(privateMessage);
		
		sender.sendMessage(msg);
	}
}
