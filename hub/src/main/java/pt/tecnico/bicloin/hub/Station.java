package pt.tecnico.bicloin.hub;

public class Station {

	private String id;
	private String name;
	private int nrDocks;
	private double[] location;
	private int prize;
	
	public Station(String id, String name, int nrDocks, double[] location, int prize) {
		this.id = id;
		this.name = name;
		this.nrDocks = nrDocks;
		this.location = location;
		this.prize = prize;
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
	public int getNrDocks() {
		return nrDocks;
	}
	public void setNrDocks(int nr_docks) {
		this.nrDocks = nr_docks;
	}
	public double[] getLocation() {
		return location;
	}
	public void setLocation(double[] location) {
		this.location = location;
	}
	public int getPrize() {
		return prize;
	}
	public void setPrize(int prize) {
		this.prize = prize;
	}
	
	
}
