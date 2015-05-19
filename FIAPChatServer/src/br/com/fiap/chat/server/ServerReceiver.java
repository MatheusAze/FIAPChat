package br.com.fiap.chat.server;

import br.com.fiap.chat.utils.Commands;
import br.com.fiap.chat.utils.GSON;
import br.com.fiap.chat.utils.Receiver;
import br.com.fiap.chat.utils.Utils;
import br.com.fiap.chat.vo.Message;
import br.com.fiap.chat.vo.Room;
import br.com.fiap.chat.vo.User;

public class ServerReceiver extends Receiver {

	public ServerReceiver(int port) {
		super(port);
	}

	@Override
	protected void treatMessage(String sourceIp, Integer sourcePort, Message message) {

		message.setDestinationIp(sourceIp);
		User currentUser = Server.getUserByIp(sourceIp);

		String msg = message.getMessage().trim();
		Commands operation = message.getCommand();

		switch (operation) {
		case ACCESS:
			// System.out.println("> IP " + sourceIp + " acessando pela primeira vez");
			Server.getAccess(sourceIp);
			message.setCommand(Commands.REQUEST_USER_NAME);
			message.setMessage("CHAT > Digite o seu nome de usuário");
			Server.sender.sendMessage(message);
			break;

		case SEND_USER_NAME:
			String userName = msg;
			User user = Server.getUserByName(userName);
			if (user == null) { // usuário não existe
				Server.register(userName, sourceIp);
				message.setCommand(Commands.NOTIFICATION);
				message.setMessage("CHAT > " + userName + " registrado com sucesso!" + Utils.LINE_SEPARATOR
						+ "Para uma lista de comandos, digite '/help'");
			} else { // usuário já existe
				message.setCommand(Commands.REQUEST_USER_NAME);
				message.setMessage("CHAT > O usuário " + userName
						+ " já está registrado\nCHAT > Digite o seu nome de usuário");
			}

			Server.sender.sendMessage(message);
			break;

		case REQUEST_LIST_ROOMS:
			message.setCommand(Commands.DEFAULT_COMMAND);
			message.setMessage(Utils.LINE_SEPARATOR + Server.roomsToString());
			Server.sender.sendMessage(message);
			break;

		case REQUEST_CREATE_ROOM:
			message.setCommand(Commands.REQUEST_CREATE_ROOM);
			message.setMessage("CHAT > Você irá criar uma nova sala. Pressione 'Enter' para continuar.");
			Server.sender.sendMessage(message);
			break;

		case REQUEST_JOIN_ROOM:
			message.setCommand(Commands.REQUEST_JOIN_ROOM);
			message.setMessage("CHAT > Digite o nome da sala que deseja entrar");
			Server.sender.sendMessage(message);
			break;

		case SEND_CREATE_ROOM:
			if (currentUser == null || !currentUser.isRegistred()) {
				message.setCommand(Commands.REQUEST_USER_NAME);
				message.setMessage("CHAT > Usuário não identificado." + Utils.LINE_SEPARATOR
						+ "Digite o seu nome de usuário: ");
			} else {
				Room currentRoom = Server.getRoomByUser(currentUser.getName());
				Server.leftRoom(currentUser.getName(), currentRoom.getName());
				
				Room newRoom = GSON.getInstance().fromJson(msg, Room.class);
				String result = Server.createRoom(currentUser.getName(), newRoom.getName(), newRoom.getDescription());
				message.setCommand(Commands.DEFAULT_COMMAND);
				message.setMessage("CHAT > " + result + "CHAT > Bem vindo à sala " + newRoom.getName() + ".");
			}

			Server.sender.sendMessage(message);
			break;

		case SEND_JOIN_ROOM:
			String roomName = msg;
			Room room = Server.getRoomByName(roomName);
			if (room == null) {
				message.setCommand(Commands.INVALID_COMMAND);
				message.setMessage("CHAT > Sala não encontrada, tente novamente! ");
			} else {
				Room currentRoom = Server.getRoomByUser(currentUser.getName());
				Server.leftRoom(currentUser.getName(), currentRoom.getName());
				
				Server.joinRoom(currentUser.getName(), room.getName());
				message.setCommand(Commands.DEFAULT_COMMAND);
				message.setMessage(room.getName() + " > Bem vindo à sala " + room.getName() + ".");
			}

			Server.sender.sendMessage(message);
			break;

		case REQUEST_USERS_IN_ROOM:
			Room currentRoom = Server.getRoomByUser(currentUser.getName());
			message.setCommand(Commands.DEFAULT_COMMAND);
			message.setMessage(currentRoom.getName() + " > " + Utils.LINE_SEPARATOR + Server.usersInRoomToString(currentRoom.getName()));
			Server.sender.sendMessage(message);
			break;

		case REQUEST_MESSAGE:
			message.setCommand(Commands.REQUEST_MESSAGE);
			message.setMessage("CHAT > escreva a mensagem ");
			Server.sender.sendMessage(message);
			break;

		case REQUEST_MESSAGE_PRIVATE:
			message.setCommand(Commands.REQUEST_MESSAGE_PRIVATE);
			message.setMessage("CHAT > escreva o usuario: ");
			Server.sender.sendMessage(message);
			break;

		case REQUEST_LEFT_ROOM:
			Room crntRoom = Server.getRoomByUser(currentUser.getName());
			Server.leftRoom(currentUser.getName(), crntRoom.getName());
			message.setCommand(Commands.DEFAULT_COMMAND);
			message.setMessage("CHAT > Você saiu da sala " + crntRoom.getName());
			Server.sender.sendMessage(message);
			break;

		case REQUEST_DELETE_ROOM:
			Room crtRoom = Server.getRoomByUser(currentUser.getName());
			Server.deleteRoom(currentUser.getName(), crtRoom.getName());
			message.setCommand(Commands.DEFAULT_COMMAND);
			message.setMessage("");
			Server.sender.sendMessage(message);
			break;

		case SEND_MESSAGE:
			Room cRoom = Server.getRoomByUser(currentUser.getName());
			message.setCommand(Commands.DEFAULT_COMMAND);
			Server.sendMessage(currentUser.getName(), cRoom.getName(), message.getMessage());
			// Server.sender.sendMessage(message);
			break;

		case SEND_MESSAGE_PRIVATE:
			Room bRoom = Server.getRoomByUser(currentUser.getName());
			message.setCommand(Commands.DEFAULT_COMMAND);
			Server.sendPrivateMessage(currentUser.getName(), message.getAdditionalInfo().get("destUser"),
					bRoom.getName(), message.getMessage());
			Server.sender.sendMessage(message);
			break;

		case HELP:

			message.setCommand(Commands.NOTIFICATION);
			message.setMessage(Commands.getUserCommandsList());
			Server.sender.sendMessage(message);

			break;

		default:
			message.setCommand(Commands.NOTIFICATION);
			message.setMessage("CHAT > Comando inválido!" + Utils.LINE_SEPARATOR);
			Server.sender.sendMessage(message);
			break;

		}
	}

}
