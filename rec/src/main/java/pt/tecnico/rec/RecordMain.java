package pt.tecnico.rec;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.sdis.zk.*;

import java.io.IOException;

public class RecordMain {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println(RecordMain.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		
		// check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", RecordMain.class.getName());
			return;
		}
		
		final String zooHost = args[0];
		final String zooPort = args[1];
		final String host = args[2];
		final String port = args[3];
		final String instance = args[4];
		final String path = "/grpc/bicloin/rec/" + instance;
		
		ZKNaming zkNaming = null;
		
		try {
				zkNaming = new ZKNaming(zooHost, zooPort);
				// publish
				zkNaming.rebind(path, host, port);
				
				final int recPort = Integer.parseInt(port);
			
				// start gRPC server
				final BindableService impl = new RecordServiceImpl(Integer.parseInt(instance));

				// Create a new server to listen on port
				Server server = ServerBuilder.forPort(recPort).addService(impl).build();

				// Start the server
				server.start();

				// Server threads are running in the background.
				System.out.println("Server started");
			
				// await termination
				// Do not exit the main thread. Wait until server is terminated.
				server.awaitTermination();
			
			} catch (ZKNamingException e) {
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
