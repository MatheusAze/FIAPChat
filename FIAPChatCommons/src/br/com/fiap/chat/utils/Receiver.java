package br.com.fiap.chat.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import br.com.fiap.chat.vo.Message;

/**
 * Implementa o lado "Ouvidor" de nosso chat UDP simples
 */
public abstract class Receiver {

	// PARA PESQUISAR: Qual o tamanho maximo do buffer?
	private static int BUFSIZE = 4096;
	private DatagramSocket listenSocket;

	public Receiver(int port) {
		try {
			listenSocket = new DatagramSocket(port);
			System.out.println("> Iniciado com sucesso (" + listenSocket.getLocalSocketAddress() + ")");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	protected abstract void treatMessage(String sourceIp, Integer sourcePort, Message message);

	public void initReceiver() {
		String ip;
		byte[] buffer = new byte[BUFSIZE];
		while (listenSocket != null) {
			try {
				Arrays.fill(buffer, (byte) ' ');
				DatagramPacket packet = new DatagramPacket(buffer, BUFSIZE);
				listenSocket.receive(packet);

				ip = packet.getAddress().toString();
				if (ip.substring(0, 1).equals("/")) {
					ip = ip.substring(1);
				}

				treatMessage(ip, packet.getPort(), GSON.getInstance().fromJson(new String(packet.getData()), Message.class));

				Thread.yield();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
