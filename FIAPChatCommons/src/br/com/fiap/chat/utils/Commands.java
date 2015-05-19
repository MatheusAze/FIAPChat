package br.com.fiap.chat.utils;

public enum Commands {
	NOTIFICATION(false, null),
	ACCESS(false, null),
	REQUEST_USER_NAME(false, null),
	REQUEST_LIST_ROOMS(true, "listarSala"),
	REQUEST_CREATE_ROOM(true, "criarSala"),
	REQUEST_JOIN_ROOM(true, "entrarSala"),
	REQUEST_MESSAGE(false, "enviarMsg"),
	REQUEST_MESSAGE_PRIVATE(true, "enviarMsgPrivada"),
	REQUEST_USERS_IN_ROOM(true, "listarUsuarios"),
	REQUEST_LEFT_ROOM(true, "sairSala"),
	REQUEST_DELETE_ROOM(true, "deletarSala"),
	REQUEST_PRIVATE_USER(false, null),
	SEND_USER_NAME(false, null),
	SEND_CREATE_ROOM(false, null),
	SEND_JOIN_ROOM(false, null),
	SEND_MESSAGE(false, null),
	SEND_MESSAGE_PRIVATE(false, null),
	SEND_PRIVATE_USER(false, null),
	DEFAULT_COMMAND(false, null),
	INVALID_COMMAND(false, null),
	HELP(true, "/help");	
	
	private boolean isUserCommand;
	private String userFriendlyCommand;
	
	private Commands(boolean isUserCommand, String userFriendlyCommand) {
		this.isUserCommand = isUserCommand;
		this.userFriendlyCommand = userFriendlyCommand;
	}
	
	public static String getUserCommandsList() {
		String commandsList = "Lista de comandos:" + Utils.LINE_SEPARATOR;
		for (Commands command : Commands.values()) {
			if(command.isUserCommand) {
				commandsList += command.userFriendlyCommand + Utils.LINE_SEPARATOR;
			}
		}
		
		return commandsList;
	}
	
	public static Commands getCommandByFriendlyCommand(String friendlyCommand) {
		for (Commands command : Commands.values()) {
			if(command.isUserCommand &&
					command.userFriendlyCommand.equalsIgnoreCase(friendlyCommand)) {
				return command;
			}
		}
		
		return null;
	}
}
