package pt.tecnico.bicloin.hub;

public class User {
	
	private String id;
	private String name;
	private String telephone;
	private boolean hasBike = false;
	
	public User(String id, String name, String telephone) {
		this.id = id;
		this.name = name;
		this.telephone = telephone;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public boolean getBike() {
		return hasBike;
	}
	
	public void setBike() {
		this.hasBike =  !this.hasBike;
	}
	

}
