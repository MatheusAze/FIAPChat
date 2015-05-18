package br.com.fiap.chat;

import br.com.fiap.chat.utils.Sender;
import br.com.fiap.chat.vo.Message;

public class ClientSender extends Sender {
	private static final Integer SERVER_PORT = 9009;
	private static String SERVER_IP;

	public ClientSender(String ipServer) {
		super(SERVER_PORT); // Cliente sempre envia para a 9009
		SERVER_IP = ipServer;
	}

	@Override
	public void sendMessage(Message message) {
		sendMessage(message, SERVER_IP);		
	}

}
