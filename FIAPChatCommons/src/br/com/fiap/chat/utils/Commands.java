package br.com.fiap.chat.utils;

public enum Commands {
	// TODO continuar mapeamento comandos de usuário
	NOTIFICATION(false, null),
	ACCESS(false, null),
	REQUEST_USER_NAME(false, null),
	SEND_USER_NAME(false, null),
	MENU_APP(false, null),
	MENU_ROOM(false, null),
	INVALID_COMMAND(false, null),
	REQUEST_CREATE_ROOM(true, "criarSala"),
	SEND_CREATE_ROOM(false, null),
	REQUEST_JOIN_ROOM(true, "entrarSala"),
	SEND_JOIN_ROOM(false, null),
	REQUEST_MESSAGE(false, null),
	SEND_MESSAGE(false, null),
	REQUEST_MESSAGE_PRIVATE(false, null),
	SEND_MESSAGE_PRIVATE(false, null),
	REQUEST_PRIVATE_USER(false, null),
	SEND_PRIVATE_USER(false, null);
	
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
}
