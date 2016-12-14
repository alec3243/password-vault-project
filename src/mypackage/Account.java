package mypackage;

public final class Account {
	private String username;
	protected String password;
	
	Account() {
		username = "";
		password = "";
	}
	
	Account(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return new String(username);
	}

	public String getPassword() {
		return new String(password);
	}
	
	public void setUsername(String u) {
		username = u;
	}
	
	public void setPassword(String p) {
		password = p;
	}
}