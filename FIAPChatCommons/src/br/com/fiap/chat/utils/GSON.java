package br.com.fiap.chat.utils;

import com.google.gson.Gson;

public class GSON {
	
	private static Gson gson;
	public static Gson getInstance() {
		if(gson == null) {
			gson = new Gson();
		}
		
		return gson;
	}
	
	
}
