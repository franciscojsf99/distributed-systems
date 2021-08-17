package pt.tecnico.rec;

import pt.tecnico.rec.grpc.*;

import io.grpc.StatusRuntimeException;

public class RecordTester {
	
	public static void main(String[] args) {
		System.out.println(RecordTester.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		
		final String zooHost = args[0];
		final String zooPort = args[1];
		/*final String host = args[0];
		final String port = args[1];*/
		final String path = args[2];
		
		RecFrontend frontend = new RecFrontend(zooHost, zooPort, path);
		
		PingRequest pingRequest = PingRequest.newBuilder().setInput("friend").build();
		PingResponse pingResponse = frontend.ping(pingRequest);
		System.out.println(pingResponse);
		
		frontend.shutdownChannel();
		
		/*ReadRequest readRequest = ReadRequest.newBuilder().setId("ab").build();
		ReadResponse readResponse = frontend.read(readRequest);
		System.out.println(readResponse);
		
		WriteRequest writeRequest = WriteRequest.newBuilder().setId("ab").setValue(10).build();
		WriteResponse writeResponse = frontend.write(writeRequest);
		System.out.println(writeResponse);
		*/
		/*try {
			PingRequest request = PingRequest.newBuilder().setInput("").build();
			PingResponse response = frontend.ping(request);
		} catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " +
			e.getStatus().getDescription());
		}*/
		
		
	}
	
}
