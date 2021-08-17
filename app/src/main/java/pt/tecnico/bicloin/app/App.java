package pt.tecnico.bicloin.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import pt.tecnico.bicloin.hub.HubFrontend;
import pt.tecnico.bicloin.hub.grpc.*;
//import pt.tecnico.bicloin.hub.User;

public class App {
	
	String hostname;
	String port ;
	String target;
	String username;
	String phoneNumber;
	double latitude;
	double longitude;
	HubFrontend frontend;
	Scanner scanner;

	String[] menuOptions = {
			
			"balance: Consult the balance in Bicloins",															//0
			"top-up N: Load N Euros in Bicloins",																//1
			"tag latitude longitude tagName: Save coordinates (latitude, longitude) in a tag named tagName",	//2
			"move tagName / move latitude longitude: Move location to coordinates in tagName",					//3
			"at: Show the user's current location",																//4
			"scan N: List the nearest N stations",																//5
			"info stationName: List information for a station",													//6
			"bike-up stationName: Lift bicycle from the indicated station",										//7
			"bike-down stationName: Return the bicycle",														//8
			"ping: Responds with a message indicating the status of the server",								//9
			"sys-status: Responds with a message indicating the status of all system servers",					//10
			"exit: Exit the app"																				//11
	};
	
	
	public void run(String[] args) {
		hostname = args[0];
		port = args[1];
		target = hostname + ":" + port;

		username = args[2];
		phoneNumber = args[3];
		latitude = Double.parseDouble(args[4]);
		longitude = Double.parseDouble(args[5]);
		if (args.length > 6) {
			String filename= args[7];
			try {
				scanner = new Scanner(new File(filename));
				
				//System.out.println(scn.next());
			} catch (FileNotFoundException e) {
				System.err.println("File doesn' exist. Check the filename or the path.");
			}
		}
		else {
			scanner = new Scanner(System.in);

		}
		
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		
		frontend = new HubFrontend(hostname, port, "/grpc/bicloin/hub/1");
		
		System.out.println("Connected to Hub");
		
		System.out.println("\n\n\t\t\t\tHello, " + username + "\n\t\t\t\tWelcome to Bicloin App!\n\n");
		
		System.out.println("Type your command: \n( if you need help, type \'help\')\n(if you want to stop, please type \'exit\')");
		
				
		chooseCommand();

		// A Channel should be shutdown before stopping the process.
	    frontend.shutdownChannel();
		
	}
	
	public void chooseCommand() {
		String[] input = {};
		String command;
		
		Map<String, List<Double>> tags = new HashMap<String, List<Double>>();
		String tag;
		double lat;
		double lon;
		
		do  {
			System.out.print("> ");
			//command = scanner.nextLine();
			input = scanner.nextLine().split(" ");
			command = input[0];

			switch(command) {	
				
			
	 		case "balance":
				if (input.length == 1) {

					BalanceRequest requestBalance = BalanceRequest.newBuilder().setId(username).build();
					BalanceResponse responseBalance = frontend.balance(requestBalance);
					System.out.println(username + " " + responseBalance.getBalance() + " BIC");
				}
				else {
					System.out.println("Warning: You must type only 'balance'. [" + this.menuOptions[0] + "]");

				}
				 break;

			case "top-up":
				if (input.length == 2) {
					try {
						int value = Integer.parseInt(input[1]);

						TopUpRequest requestTopUp = TopUpRequest.newBuilder().setId(username).setValue(value).setTelephone(
							 	phoneNumber).build(); 
						TopUpResponse responseTopUp = frontend.top_up(requestTopUp);
						
						System.out.println(username + " " + responseTopUp.getBalance() + " BIC");

					} catch ( NumberFormatException e) {
						System.out.println("Warning: The amount must be an integer. [" + this.menuOptions[1] + "]");
					}
				} else {
					System.out.println("Warning: Missing amount to deposit. [" + this.menuOptions[1] + "]");
				}
			 	
			 	break;

				
			case "tag":
				if (input.length == 4) {
					try {
						lat = Double.parseDouble(input[1]);
						lon = Double.parseDouble(input[2]);
						tag = input[3];				
					
						List<Double> coordinates = Arrays.asList(lat, lon);
						tags.put(tag, coordinates);
					
						System.out.println("OK");
					
					} catch (NumberFormatException e) {
						System.out.println("Warning: Arguments are in the wrong format. [" + this.menuOptions[2] + "]");

					}
				} else {
					System.out.println("Warning: Missing arguments. [" + this.menuOptions[2] + "]");
				}
			
				break;

			case "move":
				if (input.length == 2) {
					tag = input[1];
					latitude = tags.get(tag).get(0);
					longitude = tags.get(tag).get(1);
					
					System.out.println(username + " em https://www.google.com/maps/place/" + latitude + "," + longitude);
				}
				else if (input.length == 3) {
					try {
						latitude = Double.parseDouble(input[1]);
						longitude = Double.parseDouble(input[2]);
						System.out.println(username + " em https://www.google.com/maps/place/" + latitude + "," + longitude);						
					} catch (NumberFormatException e) {
						System.out.println("Warning: Arguments are in the wrong format. [" + this.menuOptions[3] + "]");
					}
				}
				else {
					System.out.println("Warning: Too many arguments. [" + this.menuOptions[3] + "]");
				}
						
				break;
				
			case "at":
				if (input.length == 1) {
					System.out.println(username + " em https://www.google.com/maps/place/" + latitude + "," + longitude);

				}
				else {
					System.out.println("Warning: too many arguments. [" + this.menuOptions[4] + "]");
				}
				
				break;
				
			 case "scan": 
				 if (input.length == 2) {
					 try {
						 int k = Integer.parseInt(input[1]);
							
							LocateRequest requestScan = LocateRequest.newBuilder().setK(k).setLocation(Location.newBuilder().setLatitude(latitude).setLongitude(longitude).build()).build();
							
							LocateResponse responseScan = frontend.locate_station(requestScan); 
							
							for (int i = 0; i < k ; i++) {
								String id = responseScan.getId(i);
								
								 InfoRequest requestInfo = InfoRequest.newBuilder().setId(id).build();
								 InfoResponse responseInfo = frontend.info_station(requestInfo);
								 
								double distance = this.haversideFunction(latitude, longitude, responseInfo.getLocation().getLatitude(), responseInfo.getLocation().getLongitude());
								
								double stationLatitude = responseInfo.getLocation().getLatitude();
								double stationLongitude = responseInfo.getLocation().getLongitude();
								int stationDocks = responseInfo.getNrDocks();
								int stationPrize = responseInfo.getPrize();
								int stationBikes = responseInfo.getNrBikes();
								
								System.out.println(id + ", lat " + stationLatitude + ", "
										+ stationLongitude + ", " + stationDocks + " docas, " 
										+ stationPrize + " BIC prémio, " + stationBikes + " bicicletas, a "
										+ String.format("%.0f", distance) + " metros");
							}
							
					 } catch (NumberFormatException e) {
						 System.out.println("Warning: You must type an integer. [" + this.menuOptions[5] + "]");
					 }
				 }
				 else {
					 System.out.println("Warning: Check the number of arguments. [" + this.menuOptions[5] + "]");
				 }

				 break;
				 
			 case "info": 
					if (input.length == 2) {
						 String id = input[1];
						 InfoRequest requestInfo = InfoRequest.newBuilder().setId(id).build();
						 InfoResponse responseInfo = frontend.info_station(requestInfo);
						 
						 String stationName = responseInfo.getName();
						 double stationLatitude = responseInfo.getLocation().getLatitude();
						 double stationLongitude = responseInfo.getLocation().getLongitude();
						 int stationDocks = responseInfo.getNrDocks();
						 int stationPrize = responseInfo.getPrize();
						 int stationBikes = responseInfo.getNrBikes();
						 int stationBikeUps = responseInfo.getStatistics().getBikeUps();
						 int stationBikeDowns = responseInfo.getStatistics().getBikeUps();
						 
						 System.out.println( stationName + ", lat " + stationLatitude + ", "
						 + stationLongitude + " long, " + stationDocks + " docas, "
						 + stationPrize + " BIC prémio, "
						 + stationBikes + " bicicletas,"
						 + stationBikeUps + " levantamentos, "
						 + stationBikeDowns + " devoluções, "
						 + "https://www.google.com/maps/place/" + stationLatitude + "," + stationLongitude);

					}
					else {
						System.out.println("Warning: Check the number of arguments. [" + this.menuOptions[6] + "]");
					}
					break;

				case "bike-up":
					if (input.length == 2) {
						String station = input[1];
						
						BikeRequest requestBikeUp = BikeRequest.newBuilder().setUserId(username).setStationId(station).setType("up").setLocation(Location.newBuilder().setLatitude(latitude).setLongitude(longitude).build()).setType("up").build();
						BikeResponse responseBikeUp = frontend.bike(requestBikeUp);
						System.out.println(responseBikeUp.getResponse());
						
					}
					else {
						System.out.println("Warning: Check the number of arguments. [" + this.menuOptions[7] + "]");
					}
	
					break;
					
				case "bike-down":
					if (input.length == 2) {
						String station = input[1];
						BikeRequest requestBikeDown = BikeRequest.newBuilder().setUserId(username).setStationId(station).setType("down").setLocation(Location.newBuilder().setLatitude(latitude).setLongitude(longitude).build()).setType("down").build();
						BikeResponse responseBikeDown = frontend.bike(requestBikeDown);
						System.out.println(responseBikeDown.getResponse());
					}
					else {
						System.out.println("Warning: Check the number of arguments. [" + this.menuOptions[8] + "]");
					}
					
					break;
					
			 case "ping": 
				if (input.length == 1) {
					PingRequest requestPing = PingRequest.newBuilder().setInput(username).build();
					PingResponse responsePing = frontend.ping(requestPing);
					System.out.println(responsePing.getOutput());
				}
				else {
					System.out.println("Warning: You must type only 'ping'. [" + this.menuOptions[9] + "]" );
				}
				break;
			
			case "sys-status":
				if (input.length == 1) {
					CtrlPingRequest requestCtrlPing = CtrlPingRequest.newBuilder().setInput(username).build();
					CtrlPingResponse responseCtrlPing = frontend.sys_status(requestCtrlPing);
					System.out.println(responseCtrlPing.getOutput());						
				}
				else {
					System.out.println("Warning: You must type only 'sys-status'. [" + this.menuOptions[10] + "]" );
				}

			
				break;
									
			case "help":
					System.out.println("Available commands:\n");
					for (int i = 0; i < this.menuOptions.length; i++) {
						System.out.println("--->  " + menuOptions[i] + "\n");
					}
					break;
					
			case "exit":
					break;
					
			default:
					System.out.println("Command not found. If you need help, type help.");
					break;
				
			}

		} while (!command.equals("exit"));
		scanner.close();
		
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
		double distance = 2*radius*Math.asin(Math.sqrt(haversideLatitude + Math.cos(lat1)*Math.cos(lat2)*haversideLongitude));
		
		return distance * 1000;

	}
}
