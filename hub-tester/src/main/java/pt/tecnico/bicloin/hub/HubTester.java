package pt.tecnico.bicloin.hub;

import io.grpc.StatusRuntimeException;

import pt.tecnico.bicloin.hub.grpc.*;

public class HubTester {
	
	public static void main(String[] args) {
		System.out.println(HubTester.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		
		final String host = args[0];
		final String port = args[1];
		
		HubFrontend frontend = new HubFrontend(host, port, "/grpc/bicloin/rec/1");
		
		/*PingRequest request = PingRequest.newBuilder().setInput("friend").build();
		PingResponse response = frontend.ping(request);
		System.out.println(response);*/
		
		/*try {
			PingRequest request = PingRequest.newBuilder().setInput("").build();
			PingResponse response = frontend.ping(request);
		} catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " +
			e.getStatus().getDescription());
		}*/
		
	}
	
}
