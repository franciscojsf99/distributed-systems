package pt.tecnico.bicloin.hub;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pt.tecnico.bicloin.hub.*;
import pt.tecnico.bicloin.hub.User;
import pt.tecnico.bicloin.hub.Station;

public class HubProcedures {
	
	private Map<String, User> users;
	private Map<String, Station> stations;
	
    public HubProcedures() {
    	
    	this.users = new HashMap<String, User>();
    	
        this.stations = new HashMap<String, Station>();
    }

    
	public void addUser(String id, User user){
		users.put(id, user);
	}

	public void addStation(String id, Station station){
		stations.put(id, station);
	}
	
	public User getUser(String id) {
		return users.get(id);
	}
	
	public Station getStation(String id) {
		return stations.get(id);
	}
	
    public String getTelephoneByUser(String userId){
        if (users.containsKey(userId)){
            return users.get(userId).getTelephone();
        }
        return "";
    }
    
    public String getNameByStation(String stationId) {    	
    	if (stations.containsKey(stationId)) {
    		return stations.get(stationId).getName();
    	}
    	return "";   	
    }
    
    public int getNrDocksByStation(String stationId) {
    	if (stations.containsKey(stationId)) {
    		return stations.get(stationId).getNrDocks();
    	}
    	return -1;
    }
    
    public int getPrizeByStation (String stationId) {
    	if (stations.containsKey(stationId)) {
    		return stations.get(stationId).getPrize();
    	}
    	return -1;
    }
    
    public double[] getLocationByStation(String stationId) {  
    	double[] location = {};
 
     	if (stations.containsKey(stationId)) {
    		return stations.get(stationId).getLocation();
      	}
     	System.err.println("Station not found.");
     	return location;
    }
    
    public List<String> getNearStations(double lat1, double lon1, int k) {
        TreeMap<Double, Station> distanceStation = new TreeMap<Double, Station>();
        List<String> result = new ArrayList<>(k);
        
        double lat2;
        double lon2;
        double distance;
        
        for (Map.Entry<String,Station> entry : stations.entrySet()) {
		 	lat2 = entry.getValue().getLocation()[0];		
 			lon2 = entry.getValue().getLocation()[1];		 		
	 		distance = this.haversideFunction(lat1, lon1, lat2, lon2);
	 		distanceStation.put(distance, entry.getValue());
        }
		
        distanceStation.descendingMap();
   		for(Map.Entry<Double, Station> entry : distanceStation.entrySet()) {
   			String value = entry.getValue().getId();
   			result.add(value); 		
   		}
   		return result;
    }		
		 		
    
	public double haversideFunction(double lat1, double lon1, double lat2, double lon2) {
		//Radius of Earth in km
		final double radius = 6371;
		
		//Convert to Radians, first
		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);
		lat2 = Math.toRadians(lat2);
		lon2 = Math.toRadians(lon2);
		
		//Haverside auxiliar
		double haversideLatitude = Math.pow(Math.sin((lat2 - lat1)/2), 2);
		double haversideLongitude = Math.pow(Math.sin((lon2 - lon1)/2), 2);
		
		//Distance
		double distance = 2*radius*Math.sqrt(haversideLatitude + Math.cos(lat1)*Math.cos(lat2)*haversideLongitude);
		
		return distance;

	}

}
