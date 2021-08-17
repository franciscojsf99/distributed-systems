package pt.tecnico.bicloin.hub;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import pt.tecnico.rec.RecFrontend;
import pt.tecnico.rec.CallbackFrontend;
//import pt.tecnico.rec.RecordServiceImpl;
import pt.tecnico.rec.grpc.WriteRequest;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

import java.io.IOException;
import java.lang.InterruptedException;

public class HubMain {
	
	
	public static void main(String[] args) {
		System.out.println(HubMain.class.getSimpleName());
		
		// Receive and print arguments:
		//	hostZooKeeper portZookeeper hubHost hubPort instanceNr userData stationsData (initFlag)
		// Ex: localhost 2181 localhost 8081 1 users.csv stations.csv initRec
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		// Check arguments
		if (args.length < 7) { //TODO: validate args
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", HubMain.class.getName());
			return;
		}
		
		//zknaming host port
		String zooHost = args[0];
		String zooPort = args[1];
		String host = args[2];
		String port = args[3];
		String path = "/grpc/bicloin/hub/1";
		
		String usersFilename = args[5];
		String stationsFilename= args[6];

		HubProcedures procedures = new HubProcedures(); //TODO: populate 'procedures' with .csv data

		//RecFrontend frontend = new RecFrontend(zooHost, zooPort, "/grpc/bicloin/rec");
		CallbackFrontend frontend = new CallbackFrontend(zooHost, zooPort, "/grpc/bicloin/rec");
		
		//if (frontend != null) System.out.println("Connected to rec");

		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(stationsFilename), "UTF8"))) {
			 String line;
			 while ((line = br.readLine()) != null) { 
				 String[] values = line.split(",");
		  
				 String name = values[0]; 	
				 String id = values[1]; 
				 
				 double[] location = new double[] {0,0};
				 location[0] = Double.parseDouble(values[2]); 
				 location[1] = Double.parseDouble(values[3]); 
				 
				 int nrDocks = Integer.parseInt(values[4]); 
				 int nrBikes = Integer.parseInt(values[5]);
				 int prize = Integer.parseInt(values[6]);
				 
				 Station station = new Station(id, name, nrDocks, location, prize);
				 procedures.addStation(id, station); 
					
				 WriteRequest writeBikes= WriteRequest.newBuilder().setId(id+"BIKES").setValue((double) nrBikes).build();
				 frontend.write(writeBikes).getResponse();
				 
				 WriteRequest writeBikeUps = WriteRequest.newBuilder().setId(id+"BIKEUP").setValue(0).build();
				 frontend.write(writeBikeUps).getResponse();
				 
				 WriteRequest writeBikeDowns = WriteRequest.newBuilder().setId(id+"BIKEDOWN").setValue(0).build();
				 frontend.write(writeBikeDowns).getResponse();
			 }
			 				
			 br.close();
		  
		} catch (FileNotFoundException e) { 
			  System.err.println("Cannot found the file: " + stationsFilename); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
				 
	try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(usersFilename), "UTF8"))) { 
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
		  
				String id = values[0]; 
				String name = values[1]; 
				String telephone = values[2];
  
				User user = new User(id, name, telephone); 
				
				procedures.addUser(id, user); 
				
				WriteRequest userBike = WriteRequest.newBuilder().setId(id+"BIKE").setValue(0).build();
				frontend.write(userBike).getResponse();
			}
			 br.close();

		 } catch (Exception e) { 
			  System.err.println("Cannot found the file: " + usersFilename); 
		}
		
	
	ZKNaming zkNaming = null;
	
	
	// Create a new server to listen on port
	try {
		
		zkNaming = new ZKNaming(zooHost, zooPort);
		// publish
		
		zkNaming.rebind(path, host, port);
		
		final int portHub = Integer.parseInt(port);
		final BindableService impl = new HubServiceImpl(frontend, procedures);

		Server server = ServerBuilder.forPort(portHub).addService(impl).build();

		// Start the server
		try {
			server.start();
		} catch (IOException e) {
			System.out.println("Caught exception IOException\n");
		}

		// Server threads are running in the background.
		System.out.println("Server started");

		// Do not exit the main thread. Wait until server is terminated.
		try {
			server.awaitTermination();
		} catch (InterruptedException e) {
			System.out.println("Caught exception InterruptedException\n");
		}
	
	} catch(ZKNamingException e) {
		System.out.println("Caught exception with description: " + e.getMessage());
	} finally  {
		if (zkNaming != null) {
			try {
		        // remove
		        zkNaming.unbind(path, host, port);
			} catch (ZKNamingException e) {
				System.out.println("Caught exception with description: " + e.getMessage());
			}
		} 
	}
	}
}
