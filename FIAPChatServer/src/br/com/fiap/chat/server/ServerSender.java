package br.com.fiap.chat.server;

import br.com.fiap.chat.utils.Sender;
import br.com.fiap.chat.vo.Message;


public class ServerSender extends Sender {
	
	public ServerSender() {
		super(9010); // Servidor sempre envia para 9010
	}

	@Override
	public void sendMessage(Message message) {
		sendMessage(message, message.getDestinationIp());
	}
	


}
