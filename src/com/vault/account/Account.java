package com.vault.account;

public final class Account {
	private String username;
	private String password;
	
	public Account() {
		this("", "");
	}
	
	public Account(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return new String(username);
	}

	public String getPassword() {
		return new String(password);
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}