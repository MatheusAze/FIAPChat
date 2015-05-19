package br.com.fiap.chat.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.fiap.chat.utils.Commands;
import br.com.fiap.chat.utils.Utils;
import br.com.fiap.chat.vo.Message;
import br.com.fiap.chat.vo.Room;
import br.com.fiap.chat.vo.User;

public class Server {
	public static List<Room> rooms;
	public static Set<User> users;
	public static ServerSender sender;

	public static final String LOBBY ="Lobby";

	static {
		rooms = new ArrayList<Room>();
		users = new HashSet<User>();
		sender = new ServerSender();
	}

	public static void getAccess(String userIp) {
		User user = new User();
		user.setUserIp(userIp);
		users.add(user);
	}

	public static void register(String userName, String sourceIp) {
		for (User user : users) {
			if (user.getUserIp().equals(sourceIp)) {
				user.setName(userName);
				user.setRegistred(true);
				
				getRoomByName(LOBBY).getUsers().add(user);
			}
		}
	}

	public static Room getRoomByName(String name) {
		for (Room r : rooms) {
			if (r != null && r.getName().equals(name)) {
				return r;
			}
		}
		return null;

	}

	public static Room getRoomByUser(String userName) {
		for (Room r : rooms) {
			if (r != null) {
				for (User user : r.getUsers()) {
					if (user != null && user.getName().equalsIgnoreCase(userName)) {
						return r;
					}
				}
			}
		}
		return null;
	}

	public static void joinRoom(String userName, String roomName) {
		Room room = getRoomByName(roomName);
		User user = getUserByName(userName);
		
		Room currentRoom = getRoomByUser(userName);
		if(!currentRoom.getName().equals(LOBBY)) {
			leftRoom(userName, roomName);
		}
		
		room.getUsers().add(user);

		for (User u : room.getUsers()) {
			Message mensagem = new Message(u.getUserIp(), Commands.NOTIFICATION, roomName + " > Usuário " + userName
					+ " entrou na sala." + Utils.LINE_SEPARATOR);
			Server.sender.sendMessage(mensagem);
		}
	}

	public static void leftRoom(String userName, String roomName) {
		Room room = getRoomByName(roomName);
		
		for(User user : room.getUsers()) {
			if (user.getName().equalsIgnoreCase(userName)) {
				room.getUsers().remove(user);
				joinRoom(user.getName(), LOBBY);
			} else {
				Message mensagem = new Message(user.getUserIp(), Commands.NOTIFICATION, "CHAT > Usuário " + userName
						+ " saiu da sala." + Utils.LINE_SEPARATOR);
				Server.sender.sendMessage(mensagem);
			}
		}
		
	}

	public static User getUserByName(String name) {
		for (User user : users) {
			if (user.getName() != null && user.getName().equalsIgnoreCase(name)) {
				return user;
			}
		}
		return null;
	}

	public static String roomsToString() {
		if (rooms.size() <= 0)
			return " --- Não existem salas cadastradas ---" + Utils.LINE_SEPARATOR;

		String formatedString = " --- Salas: ---" + Utils.LINE_SEPARATOR;
		for (int i = 0; i < rooms.size(); i++) {
			formatedString += "(" + (i + 1) + ") " + rooms.get(i).getName() + Utils.LINE_SEPARATOR;
		}
		formatedString += "--- ------- ----" + Utils.LINE_SEPARATOR;
		return formatedString;
	}

	public static String usersInRoomToString(String roomName) {
		Room room = getRoomByName(roomName);
		String formatedString = " --- Usuários da sala " + roomName + ":  --- " + Utils.LINE_SEPARATOR;
		for (int i = 0; i < room.getUsers().size(); i++) {
			formatedString += "(" + (i + 1) + ") " + room.getUsers().get(i).getName() + Utils.LINE_SEPARATOR;
		}
		return formatedString;

	}

	public static String createRoom(String ownerName, String roomName, String roomDescription) {
		User user = getUserByName(ownerName);
		Room newRoom = new Room(roomName, roomDescription, user);
		rooms.add(newRoom);
		newRoom.getUsers().add(user);

		return "Sala criada com sucesso" + Utils.LINE_SEPARATOR;
	}

	public static void deleteRoom(String userName, String roomName) {
		Room room = getRoomByName(roomName);
		User user = getUserByName(userName);

		if (room.getOwner().equals(user)) {
			rooms.remove(room);

			Iterator<User> iterUser = room.getUsers().iterator();
			while (iterUser.hasNext()) {
				User usuario = iterUser.next();

				Message mensagem = new Message(usuario.getUserIp(), Commands.NOTIFICATION,
						"CHAT > Você foi removido da sala " + room.getName()
								+ ", pois a sala foi deletada pelo administrador." + Utils.LINE_SEPARATOR);
				Server.sender.sendMessage(mensagem);
			}
		} else {
			Message mensagem = new Message(user.getUserIp(), Commands.NOTIFICATION,
					"CHAT > A sala somente pode ser deletada pelo administrador." + Utils.LINE_SEPARATOR);
			Server.sender.sendMessage(mensagem);
		}
	}

	public static User getUserByIp(String ip) {
		User user = null;

		for (User usuario : users) {
			if (usuario.getUserIp().equals(ip)) {
				user = usuario;
			}
		}

		return user;
	}

	/*
	 * enviarMensagem(usuárioRemetente, nomeDaSala, msg) – envia a mensagem “msg” para a sala “nomeDaSala”, garantindo o
	 * envio a todos os outros usuários. ◦ enviarMensagemPrivada(usuárioOrigem, usuárioDestino, msg) – Mensagem privada.
	 */

	public static void sendMessage(String remetente, String nomeSala, String msg) {

		for (User u : getRoomByName(nomeSala).getUsers()) {
			Message message = new Message();
			message.setDestinationIp(u.getUserIp());
			message.setCommand(Commands.MENU_APP);
			message.setMessage("CHAT > Usuário " + remetente + ": " + msg + Utils.LINE_SEPARATOR);
			Server.sender.sendMessage(message);
		}
	}

	public static void sendPrivateMessage(String remetente, String destino, String nomeSala, String msg) {

		try {
			Room r = getRoomByUser(destino);
			User u = getUserByName(destino);
			Message message = new Message();
			if (r != null) {
				message.setDestinationIp(u.getUserIp());
				message.setCommand(Commands.MENU_APP);
				message.setMessage("CHAT > Usuário " + remetente + ": " + msg + Utils.LINE_SEPARATOR);
			} else {
				message.setCommand(Commands.MENU_APP);
				message.setMessage("CHAT > Usuário" + destino + " não estava na sala " + Utils.LINE_SEPARATOR);
			}
			Server.sender.sendMessage(message);
		} catch (Exception e) {
			Message message = new Message();
			message.setCommand(Commands.MENU_APP);
			message.setMessage("CHAT > erro ao enviar mensagem " + Utils.LINE_SEPARATOR);
			Server.sender.sendMessage(message);
		}

	}
}
