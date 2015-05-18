package br.com.fiap.chat.vo;

import java.util.ArrayList;
import java.util.List;

import br.com.fiap.chat.utils.GSON;

public class Room {
	
	private String name;
	private String description;
	private User owner;
	private List<User> users;
	
	public Room()
	{
		this.owner = new User();
		this.users = new ArrayList<User>();		
	}	
	
	public Room(String name, String description, User owner)
	{
		this.name = name;
		this.description = description;
		this.owner = owner;
		this.users = new ArrayList<User>();		
	}	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public static String toJsonString(Room room) {
		return GSON.getInstance().toJson(room);
	}
	
}