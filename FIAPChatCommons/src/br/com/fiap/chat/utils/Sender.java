package br.com.fiap.chat.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import br.com.fiap.chat.vo.Message;

/**
 * Implementa o lado "Falador" de nosso chat UDP simples
 */
public abstract class Sender {
	private DatagramSocket speakSocket;
	private Integer PORT;

	public Sender(int port) {
		try {
			this.speakSocket = new DatagramSocket();
			PORT = port;
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public abstract void sendMessage(Message message);

	public synchronized void sendMessage(Message message, String destinationIP) {

		try {
			DatagramPacket packet = null;
			String jsonMsg = GSON.getInstance().toJson(message);

			packet = new DatagramPacket(jsonMsg.getBytes(),
					jsonMsg.length(), InetAddress.getByName(destinationIP),
					PORT);

			speakSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
