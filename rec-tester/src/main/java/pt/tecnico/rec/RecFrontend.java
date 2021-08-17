package pt.tecnico.rec;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.tecnico.rec.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.*;

import static io.grpc.Status.INVALID_ARGUMENT;

import java.util.*;

public class RecFrontend {
	
	//final String host;
	//final int port;
	final String zooHost;
	final String zooPort;
	ManagedChannel[] channel = null;
	RecordServiceGrpc.RecordServiceBlockingStub[] stub;
	int nrRecs = 0;
	int i;

	public RecFrontend(String host, String port, String path) {
		
		this.zooHost = host;
		this.zooPort = port;
		
		ZKNaming zkNaming = new ZKNaming(zooHost, zooPort);
		//ArrayList<ZKRecord> records = new ArrayList<ZKRecord>();
		ZKRecord[] records = null;
		Collection<ZKRecord> recordsList = null;
		
		try {
			//listRecords
			recordsList = zkNaming.listRecords(path);
			nrRecs = recordsList.size();
			records = new ZKRecord[nrRecs];
			System.out.println("List of Recs:");
			for (ZKRecord r : recordsList) {
				System.out.println(r.getURI());
			}
			
			//lookup
			for (i = 0; i < nrRecs; i++) {
				System.out.println(path + "/" + (i + 1));
				records[i] = zkNaming.lookup(path + "/" + (i + 1));
				System.out.println("depois do lookup " + i);
			}

		} catch (ZKNamingException e) {
			System.out.println("Caught exception with description: " + e.getMessage());
		}
		
		String[] targets = new String[nrRecs];
		for (i = 0; i < nrRecs; i++) {
			if (records[i] != null)  {
				targets[i] = records[i].getURI();
			}
			else targets[i] = "erro";
		}
		channel = new ManagedChannel[nrRecs];
		stub = new RecordServiceGrpc.RecordServiceBlockingStub[nrRecs];
		for (i = 0; i < nrRecs; i++) {
			if (targets[i] != "erro")  {
				channel[i] = ManagedChannelBuilder.forTarget(targets[i]).usePlaintext().build();
				System.out.println("Channel " + (i+1) + "created");
				stub[i] = RecordServiceGrpc.newBlockingStub(channel[i]);
				System.out.println("Stub " + (i+1) + "created");
			}
		}
	}

	public PingResponse ping(PingRequest request) {
		String output = "";
		for (i = 0; i < nrRecs; i++) {
			output += stub[i].ping(request).getOutput() + "\n";
		}
		return PingResponse.newBuilder().setOutput(output).build();
	}

	public ReadResponse read(ReadRequest request){
		double value = stub[0].read(request).getValue();
		return ReadResponse.newBuilder().setValue(value).build();
	}

	public WriteResponse write(WriteRequest request){
		String response = stub[0].write(request).getResponse();
		return WriteResponse.newBuilder().setResponse(response).build();
	}

	public void shutdownChannel() {
		channel[0].shutdownNow();
	}

}
