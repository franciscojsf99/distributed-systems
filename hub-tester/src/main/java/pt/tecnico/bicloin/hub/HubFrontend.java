package pt.tecnico.bicloin.hub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.tecnico.bicloin.hub.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.*;

import static io.grpc.Status.INVALID_ARGUMENT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HubFrontend {
	
	final String zooHost;
	final String zooPort;
	final ManagedChannel channel;
	HubServiceGrpc.HubServiceBlockingStub stub;
	
	public HubFrontend(String host, String port, String path ) {
		this.zooHost = host;
		this.zooPort = port;
		
		ZKNaming zkNaming = new ZKNaming(zooHost, zooPort);
		ZKRecord record = null;
		Collection<ZKRecord> recordsList = null;
		
		try {
			//listRecords
			recordsList = zkNaming.listRecords(path);
			for (ZKRecord r : recordsList) System.out.println(r.getURI());
			
			// lookup
			record = zkNaming.lookup(path);

		} catch (ZKNamingException e) {
			System.out.println("Caught exception with description: " + e.getMessage());
		}
		
		String target;
		if (record != null) target = record.getURI();
		else target = "localhost:8081";
				
		
		//this.host = host;
		//this.port = port;
		//final String target = host + ":" + port;

		
		// Channel is the abstraction to connect to a service endpoint.
		// Let us use plaintext communication because we do not have certificates.
		channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

		// It is up to the client to determine whether to block the call.
		// Here we create a blocking stub, but an async stub,
		// or an async stub with Future are always possible.
		stub = HubServiceGrpc.newBlockingStub(channel);
	}

	public PingResponse ping(PingRequest request) {

		String output = stub.ping(request).getOutput();
		PingResponse response = PingResponse.newBuilder().setOutput(output).build();
		return response;
	}
	
	public BalanceResponse balance(BalanceRequest request) {
		
		int balance = stub.balance(request).getBalance();
		BalanceResponse response = BalanceResponse.newBuilder().setBalance(balance).build();
		return response;
	}

	public TopUpResponse top_up(TopUpRequest request) {
	
		int balance = stub.topUp(request).getBalance();
		TopUpResponse response = TopUpResponse.newBuilder().setBalance(balance).build();
		return response;
	}
	
	public InfoResponse info_station(InfoRequest request) {
		//return stub.infoStation(request);
		String name = stub.infoStation(request).getName();
		double latitude = stub.infoStation(request).getLocation().getLatitude();
		double longitude = stub.infoStation(request).getLocation().getLongitude();
		int nrDocks = stub.infoStation(request).getNrDocks();
		int prize = stub.infoStation(request).getPrize();
		int nrBikes = stub.infoStation(request).getNrBikes();
		int bikeUps = stub.infoStation(request).getStatistics().getBikeUps();
		int bikeDowns = stub.infoStation(request).getStatistics().getBikeDowns();
		InfoResponse response = InfoResponse.newBuilder().setName(name)
				.setLocation(Location.newBuilder().setLatitude(latitude).setLongitude(longitude).build())
				.setNrDocks(nrDocks).setPrize(prize).setNrBikes(nrBikes)
				.setStatistics(InfoResponse.Statistics.newBuilder().setBikeUps(bikeUps).setBikeDowns(bikeDowns).build()).build();
		return response;
	}
	
	public LocateResponse locate_station(LocateRequest request) {
		int k = request.getK();
		
		List<String> result = new ArrayList<String>(k);
		
		result = stub.locateStation(request).getIdList().subList(0, k);
		
		LocateResponse response = LocateResponse.newBuilder().addAllId(result).build();
		return response;
	}
	
	public BikeResponse bike(BikeRequest request) {
		
		String output = stub.bike(request).getResponse();
		BikeResponse response = BikeResponse.newBuilder().setResponse(output).build();
		return response;
	}
	
	public CtrlPingResponse sys_status(CtrlPingRequest request) {
		
		String output = stub.sysStatus(request).getOutput();
		CtrlPingResponse response = CtrlPingResponse.newBuilder().setOutput(output).build();
		return response;
	}
	
	public void shutdownChannel() {
		channel.shutdownNow();
	}

}
