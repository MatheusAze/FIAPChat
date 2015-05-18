package br.com.fiap.chat.vo;

import java.util.HashMap;
import java.util.Map;

import br.com.fiap.chat.utils.Commands;

public class Message {
	private String destinationIp;
	private Commands command;
	private String message;
	private Map<String, String> additionalInfo;

	public Message() {
	}

	public Message(String destinationIp, Commands command, String message) {
		this.destinationIp = destinationIp;
		this.command = command;
		this.message = message;
	}

	public String getDestinationIp() {
		return destinationIp;
	}

	public void setDestinationIp(String destinationIp) {
		this.destinationIp = destinationIp;
	}

	public Commands getCommand() {
		return command;
	}

	public void setCommand(Commands command) {
		this.command = command;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, String> getAdditionalInfo() {
		if (additionalInfo == null) {
			additionalInfo = new HashMap<String, String>();
		}
		return additionalInfo;
	}

	public void setAdditionalInfo(Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

}
