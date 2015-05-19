package br.com.fiap.chat.server;

import java.util.ArrayList;
import java.util.HashSet;
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
		Room selectedRoom = null;
		for (Room room : rooms) {
			if (room != null && room.getName().equals(name)) {
				selectedRoom = room;
				break;
			}
		}
		return selectedRoom;

	}

	public static Room getRoomByUser(String userName) {
		Room selectedRoom = null;
		FOR_ROOMS: for (Room r : rooms) {
			if (r != null) {
				for (User user : r.getUsers()) {
					if (user != null && user.getName().equalsIgnoreCase(userName)) {
						selectedRoom = r;
						break FOR_ROOMS;
					}
				}
			}
		}
		return selectedRoom;
	}

	public static void joinRoom(String userName, String roomName) {
		Room room = getRoomByName(roomName);
		User user = getUserByName(userName);		

		room.getUsers().add(user);

		for (User u : room.getUsers()) {
			Message mensagem = new Message(u.getUserIp(), Commands.NOTIFICATION, roomName + " > Usuário " + userName
					+ " entrou na sala." + Utils.LINE_SEPARATOR);
			Server.sender.sendMessage(mensagem);
		}
	}

	public static void leftRoom(String userName, String roomName) {
		Room room = getRoomByName(roomName);
		User userToRemove = null;
		
		for (User user : room.getUsers()) {
			if (user.getName().equalsIgnoreCase(userName)) {
				userToRemove = user;
			} else {
				Message mensagem = new Message(user.getUserIp(), Commands.NOTIFICATION, 
						"CHAT > Usuário " + userName + " saiu do(a) " + roomName + ".");
				Server.sender.sendMessage(mensagem);
			}
		}
		
		if(userToRemove != null) {
			room.getUsers().remove(userToRemove);
			Message mensagem = new Message(userToRemove.getUserIp(), Commands.NOTIFICATION, 
					"CHAT > Você deixou a sala: " + roomName + ".");
			Server.sender.sendMessage(mensagem);
			if (!roomName.equalsIgnoreCase(LOBBY)) {
				joinRoom(userToRemove.getName(), LOBBY);
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
		String formatedString = " --- Usuários da sala (" + roomName + ")  --- " + Utils.LINE_SEPARATOR;
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

		if (room.getOwner().getUserIp().equalsIgnoreCase(user.getUserIp())) {
			
			for(User usuario : room.getUsers()) {
				Server.leftRoom(usuario.getName(), roomName);
				if (!userName.equals(usuario.getName()))
				{
					Message mensagem = new Message(usuario.getUserIp(), Commands.NOTIFICATION,
							"CHAT > Você foi removido de " + room.getName()
									+ ", pois a sala foi deletada pelo administrador.");
					Server.sender.sendMessage(mensagem);
				}
			}
			
			rooms.remove(room);

		} else {
			Message mensagem = new Message(user.getUserIp(), Commands.NOTIFICATION,
					"CHAT > A sala somente pode ser deletada pelo seu criador.");
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

	public static void sendMessage(String sender, String roomName, String msg) {
		Message message = new Message();
		message.setCommand(Commands.NOTIFICATION);
		message.setMessage(sender + ": " + msg);
		
		for (User u : getRoomByName(roomName).getUsers()) {
			message.setDestinationIp(u.getUserIp());
			Server.sender.sendMessage(message);
		}
	}

	public static void sendPrivateMessage(String sender, String destination, String msg) {
		try {
			Message message = new Message();
			User destinationUser = getUserByName(destination);
					
			Room senderRoom = getRoomByUser(sender);
			Room destinationRoom = getRoomByUser(destination);
			
			if(destinationUser == null)
			{
				message.setCommand(Commands.NOTIFICATION);
				message.setMessage("CHAT > Usuário " + destination + " não existe.");
				Server.sender.sendMessage(message);
			} else if(!senderRoom.getName().equals(destinationRoom.getName()))
			{
				message.setCommand(Commands.NOTIFICATION);
				message.setMessage("CHAT > Usuário " + destination + " não está na mesma sala que a sua.");
				Server.sender.sendMessage(message);
			}
			else
			{
				message.setCommand(Commands.NOTIFICATION);
				message.setMessage("(private) " + sender + ": " + msg);
				Server.sender.sendMessage(message);
				
				message.setDestinationIp(destinationUser.getUserIp());
				message.setCommand(Commands.NOTIFICATION);
				message.setMessage("(private) " + sender + ": " + msg);
				Server.sender.sendMessage(message);
			}			
		} catch (Exception e) {
			Message message = new Message();
			message.setCommand(Commands.NOTIFICATION);
			message.setMessage("CHAT > Erro inesperado ao enviar mensagem. Por favor, tente novamente.");
			Server.sender.sendMessage(message);
		}
	}
}
