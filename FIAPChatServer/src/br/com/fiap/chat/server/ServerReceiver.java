package br.com.fiap.chat.server;

import br.com.fiap.chat.utils.Commands;
import br.com.fiap.chat.utils.GSON;
import br.com.fiap.chat.utils.Receiver;
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
			System.out.println("> IP " + sourceIp + " acessando pela primeira vez");
			Server.getAccess(sourceIp);
			message.setCommand(Commands.REQUEST_USER_NAME);
			message.setMessage("CHAT > Digite o seu nome de usu�rio: ");
			Server.sender.sendMessage(message);
			break;

		case SEND_USER_NAME:
			String userName = msg;
			User user = Server.getUserByName(userName);
			if (user == null) { // usu�rio n�o existe
				Server.register(userName, sourceIp);
				message.setCommand(Commands.MENU_APP);
				message.setMessage("CHAT > " + userName + " registrado com sucesso!\n"
						+ Server.menuToString(Server._appMenu));
			} else { // usu�rio j� existe
				message.setCommand(Commands.REQUEST_USER_NAME);
				message.setMessage("CHAT > O usu�rio " + userName
						+ " j� est� registrado\nCHAT > Digite o seu nome de usu�rio: ");
			}

			Server.sender.sendMessage(message);
			break;

		case MENU_APP:
			int menuAppKey = Integer.parseInt(msg);
			System.out.println("[Menu App] Op��o escolhida: " + menuAppKey);
			switch (menuAppKey) {
			case 1:
				message.setCommand(Commands.MENU_APP);
				message.setMessage("CHAT > \n" + Server.roomsToString() + "Escolha uma nova op��o:\n"
						+ Server.menuToString(Server._appMenu, 1));
				break;

			case 2:
				message.setCommand(Commands.REQUEST_CREATE_ROOM);
				message.setMessage("CHAT > Criando uma nova sala...\n");
				break;

			case 3:
				message.setCommand(Commands.REQUEST_JOIN_ROOM);
				message.setMessage("CHAT > Digite o nome da sala que deseja entrar:\n");
				break;

			default:
				message.setCommand(Commands.INVALID_COMMAND);
				message.setMessage("CHAT > O menu selecionado � invalido, tente novamente! \n"
						+ Server.menuToString(Server._appMenu));
				break;
			}
			Server.sender.sendMessage(message);
			break;

		case SEND_CREATE_ROOM:
			if (currentUser == null || !currentUser.isRegistred()) {
				message.setCommand(Commands.REQUEST_USER_NAME);
				message.setMessage("CHAT > Usu�rio n�o identificado.\n" + "Digite o seu nome de usu�rio: ");
			} else {
				Room newRoom = GSON.getInstance().fromJson(msg, Room.class);
				String result = Server.createRoom(currentUser.getName(), newRoom.getName(), newRoom.getDescription());
				message.setCommand(Commands.MENU_ROOM);
				message.setMessage("CHAT > " + result + "CHAT > Bem vindo � sala " + newRoom.getName() + ".\n"
						+ "Oque deseja fazer?\n" + Server.menuToString(Server._roomMenu));
			}

			Server.sender.sendMessage(message);
			break;

		case SEND_JOIN_ROOM:
			String roomName = msg;
			Room room = Server.getRoomByName(roomName);
			if (room == null) {
				message.setCommand(Commands.INVALID_COMMAND);
				message.setMessage("CHAT > Sala n�o encontrada, tente novamente! \n"
						+ Server.menuToString(Server._appMenu));
			} else {
				Server.joinRoom(currentUser.getName(), room.getName());
				message.setCommand(Commands.MENU_ROOM);
				message.setMessage("CHAT > Bem vindo � sala " + room.getName() + ".\n" + "Oque deseja fazer?\n"
						+ Server.menuToString(Server._roomMenu));
			}

			Server.sender.sendMessage(message);
			break;

		case MENU_ROOM:
			Room currentRoom = Server.getRoomByUser(currentUser.getName());
			if (currentRoom != null) {
				int menuRoomKey = Integer.parseInt(msg);
				System.out.println("[Menu Sala] Op��o escolhida: " + menuRoomKey);
				switch (menuRoomKey) {
				// ver usu�rios da sala
				case 1:
					message.setCommand(Commands.MENU_ROOM);
					message.setMessage("CHAT > \n" + Server.usersInRoomToString(currentRoom.getName())
							+ "Escolha uma nova op��o:\n" + Server.menuToString(Server._roomMenu, 1));
					break;

				case 2:
					message.setCommand(Commands.REQUEST_MESSAGE);
					message.setMessage("CHAT > escreva a mensagem \n");

					break;

				case 3:
					message.setCommand(Commands.REQUEST_MESSAGE_PRIVATE);
					message.setMessage("CHAT > escreva o usuario: \n");

					break;

				// Sair da Sala
				case 4:
					Server.leftRoom(currentUser.getName(), currentRoom.getName());
					message.setCommand(Commands.MENU_APP);
					message.setMessage("CHAT > Oque deseja fazer?\n" + Server.menuToString(Server._appMenu));
					break;

				// Excluir a Sala
				case 5:
					Server.deleteRoom(currentUser.getName(), currentRoom.getName());
					message.setCommand(Commands.MENU_APP);
					message.setMessage("CHAT > Oque deseja fazer?\n" + Server.menuToString(Server._appMenu));
					break;

				default:
					message.setCommand(Commands.INVALID_COMMAND);
					message.setMessage("CHAT > O menu selecionado � invalido, tente novamente! \n"
							+ Server.menuToString(Server._roomMenu));
					break;
				}
				Server.sender.sendMessage(message);
			} else {
				message.setCommand(Commands.INVALID_COMMAND);
				message.setMessage("CHAT > Sala n�o encontrada, tente novamente! \n"
						+ Server.menuToString(Server._appMenu));
			}
			break;

		case SEND_MESSAGE:
			Room cRoom = Server.getRoomByUser(currentUser.getName());
			message.setCommand(Commands.MENU_ROOM);
			Server.sendMessage(currentUser.getName(), cRoom.getName(), message.getMessage());
			Server.sender.sendMessage(message);

		case SEND_MESSAGE_PRIVATE:
			Room bRoom = Server.getRoomByUser(currentUser.getName());
			message.setCommand(Commands.MENU_ROOM);
			Server.sendPrivateMessage(currentUser.getName(), message.getAdditionalInfo().get("destUser"),
					bRoom.getName(), message.getMessage());
			Server.sender.sendMessage(message);

		default:
			message.setCommand(Commands.NOTIFICATION);
			message.setMessage("CHAT > Comando inv�lido!\n");
			Server.sender.sendMessage(message);
			break;

		}
	}

}
