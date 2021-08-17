package pt.tecnico.rec;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.tecnico.rec.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.*;

import static io.grpc.Status.INVALID_ARGUMENT;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;

import java.util.*;

public class CallbackFrontend {
	
	final String zooHost;
	final String zooPort;
	ManagedChannel[] channel = null;
	RecordServiceGrpc.RecordServiceStub[] stub;
	int nrRecs = 0;
	int i;
	/*int quorun = 0;
	double tag = 0;*/

	public CallbackFrontend(String host, String port, String path) {
		
		this.zooHost = host;
		this.zooPort = port;
		
		ZKNaming zkNaming = new ZKNaming(zooHost, zooPort);
		ZKRecord[] records = null;
		Collection<ZKRecord> recordsList = null;
		
		try {
			//listRecords
			recordsList = zkNaming.listRecords(path);
			nrRecs = recordsList.size();
			//quorun = nrRecs/2 + 1;
			records = new ZKRecord[nrRecs];
			System.out.println("List of Recs:");
			for (ZKRecord r : recordsList) {
				System.out.println(r.getURI());
			}
			
			//lookup
			for (i = 0; i < nrRecs; i++) {
				records[i] = zkNaming.lookup(path + "/" + (i + 1));
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

		stub = new RecordServiceGrpc.RecordServiceStub[nrRecs];
		for (i = 0; i < nrRecs; i++) {
			if (targets[i] != "erro")  {
				channel[i] = ManagedChannelBuilder.forTarget(targets[i]).usePlaintext().build();

				stub[i] = RecordServiceGrpc.newStub(channel[i]);

			}
		}
	}
	
	public PingResponse ping(PingRequest request) {
		RecObserver<PingResponse> r = new RecObserver<PingResponse>();
		for (i = 0; i < nrRecs; i++) {
			//stub[i].ping(request, r);
			stub[i].withDeadlineAfter(5000, TimeUnit.MILLISECONDS).ping(request, r);
		}
		List<PingResponse> responses;
		while (r.getResponses().size() < nrRecs) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				System.out.println("Caught exception with description: " + e.getMessage());
			}
		}
		responses = r.getResponses();
		String output = "";
		for (PingResponse x : responses) output += x.getOutput() + "\n";

		return PingResponse.newBuilder().setOutput(output).build();
	}

	public ReadResponse read(ReadRequest request){
		RecObserver<ReadResponse> r = new RecObserver<ReadResponse>();
		/*
		for (i = 0; i < nrRecs; i++) {
			stub[i].withDeadlineAfter(5000, TimeUnit.MILLISECONDS).read(request, r);
		}
		*/
		stub[0].read(request, r);

		List<ReadResponse> responses;
		while (r.getResponses().size() < 1) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				System.out.println("Caught exception with description: " + e.getMessage());
			}
		}
		responses = r.getResponses();
		/*
		double maxTag = 0;
		ReadResponse response = responses.get(0);
		System.out.println("antes de ver tags");
		for (ReadResponse rr : responses) {
			if (rr.getTag() > maxTag) {
				maxTag = rr.getTag();
				response = rr;
			}
		}
		System.out.println("depois de ver tags");
		return response;
		*/

		return responses.get(0);
	}
	
	public WriteResponse write(WriteRequest request) {
		RecObserver<WriteResponse> r = new RecObserver<WriteResponse>();
		/*
		String id = request.getId();
		double value = request.getValue();
		WriteRequest req = WriteRequest.newBuilder().setId(id).setValue(value).setTag(tag).build();
		tag++;

		for (i = 0; i < nrRecs; i++) {
			stub[i].withDeadlineAfter(5000, TimeUnit.MILLISECONDS).write(req, r);
		}
		*/

		stub[0].write(request, r);
		List<WriteResponse> responses;

		while (r.getResponses().size() < 1) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				System.out.println("Caught exception with description: " + e.getMessage());
			}
		}
		responses = r.getResponses();

		return responses.get(0);
		
	}
	
	public void shutdownChannel() {
		for (i = 0; i < nrRecs; i++) {
			channel[i].shutdown();
		}
	}

}