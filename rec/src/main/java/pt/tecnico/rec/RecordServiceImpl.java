package pt.tecnico.rec;


import io.grpc.stub.StreamObserver;
import pt.tecnico.rec.grpc.*;

import io.grpc.Context;
import io.grpc.Status;
import static io.grpc.Status.INVALID_ARGUMENT;

public class RecordServiceImpl extends RecordServiceGrpc.RecordServiceImplBase {
	
	
	private RecordProcedures procedures;// = new RecordProcedures();
	private int instance;
	
	public RecordServiceImpl(int instance) {
		this.instance = instance;
		procedures = new RecordProcedures(instance);
	}

	@Override
	public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
		String input = request.getInput();
		String output = procedures.ping();
		
		/*if (Context.current().isCancelled()) {
			responseObserver.onError(Status.DEADLINE_EXCEEDED.withDescription("Time's up!").asRuntimeException());
			return;
		}*/
		
		if (input == null || input.isBlank()) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Input cannot be empty!").asRuntimeException());
		}
		
		PingResponse response = PingResponse.newBuilder().setOutput(output).build();
		
		responseObserver.onNext(response);
		responseObserver.onCompleted();
		
	}
	
	@Override
	public void read(ReadRequest request, StreamObserver<ReadResponse> responseObserver) {
		String id = request.getId();
		
		/*if (Context.current().isCancelled()) {
			responseObserver.onError(Status.DEADLINE_EXCEEDED.withDescription("Time's up!").asRuntimeException());
			return;
		}*/
		
		if (id == null || id.isBlank()) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Record name cannot be empty!").asRuntimeException());
		}
		
		double value = procedures.read(id);
		
		/*double[] res = procedures.read(id);
		double value = res[0];
		double tag = res[1];
		
		ReadResponse response = ReadResponse.newBuilder().setValue(value).setTag(tag).build();*/
		
		ReadResponse response = ReadResponse.newBuilder().setValue(value).build();
		
		responseObserver.onNext(response);
		responseObserver.onCompleted();
		
	}
	
	@Override
	public void write(WriteRequest request, StreamObserver<WriteResponse> responseObserver) {
		String id = request.getId();
		double value = request.getValue();

		/*double tag = request.getTag();
		double[] req = {value, tag};
		
		if (Context.current().isCancelled()) {
			responseObserver.onError(Status.DEADLINE_EXCEEDED.withDescription("Time's up!").asRuntimeException());
			return;
		}
		*/
		
		if (id == null || id.isBlank()) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Record name cannot be empty!").asRuntimeException());
		}
		
		procedures.write(id, value);
		//procedures.write(id, req);
		String message = "Record write success";
		
		WriteResponse response = WriteResponse.newBuilder().setResponse(message).build();
		
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

}
