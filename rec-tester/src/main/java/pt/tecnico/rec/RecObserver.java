package pt.tecnico.rec;

import java.util.*;

import io.grpc.stub.StreamObserver;
import pt.tecnico.rec.grpc.*;

public class RecObserver<R> implements StreamObserver<R> {
	
	private List<R> responses = new ArrayList<R>();
	
    @Override
    public void onNext(R r) {
        //System.out.println("Received response: " + r + "sizeof responses = " + responses.size());
        responses.add(r);
        //System.out.println("Depois do add ---- sizeof responses = " + responses.size());
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Received error: " + throwable);
    }

    @Override
    public void onCompleted() {
        //System.out.println("Request completed");
    }

	public List<R> getResponses() {
		return responses;
	}
}