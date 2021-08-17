package pt.tecnico.bicloin.hub;


import static io.grpc.Status.INVALID_ARGUMENT;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.grpc.stub.StreamObserver;
import pt.tecnico.bicloin.hub.grpc.*;

import pt.tecnico.rec.RecFrontend;
import pt.tecnico.rec.CallbackFrontend;

import pt.tecnico.rec.grpc.ReadRequest;
import pt.tecnico.rec.grpc.ReadResponse;
import pt.tecnico.rec.grpc.WriteRequest;
import pt.tecnico.rec.grpc.WriteResponse;

public class HubServiceImpl extends HubServiceGrpc.HubServiceImplBase {

	//private RecFrontend rec;
	private CallbackFrontend rec;
	private HubProcedures proc;

	//HubServiceImpl(RecFrontend frontend, HubProcedures procedures){
	HubServiceImpl(CallbackFrontend frontend, HubProcedures procedures){
		//super();
		rec = frontend;
		proc = procedures;
	}
	
	
	
	@Override
	public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
		String input = request.getInput();
		String output = "Hub is running.";

		if (input == null || input.isBlank()) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Input cannot be empty!").asRuntimeException());
		}

		PingResponse response = PingResponse.newBuilder().setOutput(output).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
	
	@Override
	public void sysStatus(CtrlPingRequest request, StreamObserver<CtrlPingResponse> responseObserver) {
		String input = request.getInput();
		String output = "";

		if (input == null || input.isBlank()) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Input cannot be empty!").asRuntimeException());
		}
		
		pt.tecnico.rec.grpc.PingRequest requestRec = pt.tecnico.rec.grpc.PingRequest.newBuilder().setInput(input).build();
		output = "Hub is running.\n" + rec.ping(requestRec).getOutput();
		//output = "Hub is running.\n";
		//rec.ping(requestRec);
		
		CtrlPingResponse response = CtrlPingResponse.newBuilder().setOutput(output).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
		
	@Override
	public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
		String id = request.getId();
		
		ReadRequest readReq = ReadRequest.newBuilder().setId(id+"BALANCE").build();
		int balance = (int) rec.read(readReq).getValue();
		BalanceResponse response = BalanceResponse.newBuilder().setBalance(balance).build();
		//rec.read(readReq);

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
	
	@Override
	public void topUp(TopUpRequest request, StreamObserver<TopUpResponse> responseObserver) {
		String id = request.getId();
		int value = request.getValue();
		String telephone = request.getTelephone();

		if (!telephone.equals(proc.getTelephoneByUser(id))){
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Invalid phone number!").asRuntimeException());
		}
		if (value < 0) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Value cannot be negative!").asRuntimeException());
		}
		ReadRequest readReq = ReadRequest.newBuilder().setId(id+"BALANCE").build();
		double balance = rec.read(readReq).getValue();
		balance = balance + value*10;
		WriteRequest writeReq = WriteRequest.newBuilder().setId(id+"BALANCE").setValue(balance).build();
		rec.write(writeReq).getResponse();
		
		
		//if write successful
		TopUpResponse response = TopUpResponse.newBuilder().setBalance((int) balance).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void bike(BikeRequest request, StreamObserver<BikeResponse> responseObserver) {
		String userId = request.getUserId();
		String stationId = request.getStationId();
		String type = request.getType();
		double latitude = request.getLocation().getLatitude();
		double longitude = request.getLocation().getLongitude();
			
		if (userId == null || userId.isBlank()) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("userId cannot be empty!").asRuntimeException());
		}
		if (stationId == null || stationId.isBlank()) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("stationId cannot be empty!").asRuntimeException());
		}
		if (type == null || type.isBlank()) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("type cannot be empty! (up or down)").asRuntimeException());
		}
		if (latitude == 0) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Latitude cannot be empty!").asRuntimeException());
		}
		if (longitude == 0){
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Longitude cannot be empty!").asRuntimeException());
		}
		
		double stationLatitude = proc.getLocationByStation(stationId)[0];
		double stationLongitude = proc.getLocationByStation(stationId)[1];
		
		String output = "";
		
		ReadRequest readUserBike = ReadRequest.newBuilder().setId(userId+"BIKE").build();
		int userHasBike = (int) rec.read(readUserBike).getValue();
		 
		ReadRequest readStationBike = ReadRequest.newBuilder().setId(stationId +"BIKES").build();
		int bikesAvailable = (int) rec.read(readStationBike).getValue();
		 
		ReadRequest readReq = ReadRequest.newBuilder().setId(userId+"BALANCE").build();
		int balance = (int) rec.read(readReq).getValue();
		
		if (type.equals("up") &&  proc.haversideFunction(latitude, longitude, stationLatitude, stationLongitude) < 200
					&& userHasBike == 0 && bikesAvailable > 0  && balance > 10) {
				
				WriteRequest writeUserBike = WriteRequest.newBuilder().setId(userId+"BIKE").setValue(1).build();
				rec.write(writeUserBike).getResponse();		

				WriteRequest writeStationBikes = WriteRequest.newBuilder().setId(stationId+"BIKES").setValue(bikesAvailable-1).build();
				rec.write(writeStationBikes).getResponse();	
				
				ReadRequest readBikeUps = ReadRequest.newBuilder().setId(stationId+"BIKEUP").build();
				double bikeUp = rec.read(readBikeUps).getValue();
				
				WriteRequest writeBikeUps = WriteRequest.newBuilder().setId(stationId+"BIKEUP").setValue(bikeUp+1).build();
				rec.write(writeBikeUps).getResponse();
				
				WriteRequest writeBalance = WriteRequest.newBuilder().setId(userId+"BALANCE").setValue(balance-10).build();
				rec.write(writeBalance).getResponse();
				
				output =  "OK";
		}
		
		else if (type.equals("down") && proc.haversideFunction(latitude, longitude, stationLatitude, stationLongitude) < 200
					&& userHasBike == 1 && proc.getStation(stationId).getNrDocks() - bikesAvailable > 0 ) {
				
				WriteRequest writeUserBike = WriteRequest.newBuilder().setId(userId+"BIKE").setValue(0).build();
				rec.write(writeUserBike).getResponse();		

				WriteRequest writeStationBikes = WriteRequest.newBuilder().setId(stationId+"BIKES").setValue(bikesAvailable+1).build();
				rec.write(writeStationBikes).getResponse();	
				
				ReadRequest readBikeDowns = ReadRequest.newBuilder().setId(stationId+"BIKEDOWN").build();
				double bikeDown = rec.read(readBikeDowns).getValue();
				
				WriteRequest writeBikeDowns = WriteRequest.newBuilder().setId(stationId+"BIKEDOWN").setValue(bikeDown+1).build();
				rec.write(writeBikeDowns).getResponse();
				
				WriteRequest writeBalance = WriteRequest.newBuilder().setId(userId+"BALANCE").setValue(balance+proc.getStation(stationId).getPrize()).build();
				rec.write(writeBalance).getResponse();
				
				output =  "OK";
			}
					
		else {
			output =  "ERRO";
		}
		
		BikeResponse response = BikeResponse.newBuilder().setResponse(output).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void infoStation(InfoRequest request, StreamObserver<InfoResponse> responseObserver) {
		String id = request.getId();
		String name = proc.getNameByStation(id);
		int nDocks = proc.getNrDocksByStation(id);
		int prize = proc.getPrizeByStation(id);
		double latitude = proc.getLocationByStation(id)[0];
		double longitude = proc.getLocationByStation(id)[1];

		if (id == null || id.isBlank()) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("stationId cannot be empty!").asRuntimeException());
		}

		ReadRequest readBikeUp = ReadRequest.newBuilder().setId(id+"BIKEUP").build();
		int bikeUps = (int) rec.read(readBikeUp).getValue();
		
		ReadRequest readBikeDown = ReadRequest.newBuilder().setId(id+"BIKEDOWN").build();
		int bikeDowns = (int) rec.read(readBikeDown).getValue();


		ReadRequest readNrBikes = ReadRequest.newBuilder().setId(id+"BIKES").build();
		int nrBikes = (int) rec.read(readNrBikes).getValue();

		InfoResponse response = InfoResponse.newBuilder().setName(name).setNrBikes(nrBikes).setNrDocks(nDocks).setPrize(prize)
				.setLocation(Location.newBuilder().setLongitude(longitude).setLatitude(latitude).build())
				.setStatistics(InfoResponse.Statistics.newBuilder().setBikeUps(bikeUps).setBikeDowns(bikeDowns).build())
				.build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();

	}

	@Override
	public void locateStation(LocateRequest request, StreamObserver<LocateResponse> responseObserver) {
		int k = request.getK();

		double lat1 = request.getLocation().getLatitude();
		double lon1 = request.getLocation().getLongitude();
		

		if (k <= 0) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Number of stations cannot be zero!").asRuntimeException());
		}
		if (lat1 == 0) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Latitude cannot be empty!").asRuntimeException());
		}
		if (lon1 == 0){
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Longitude cannot be empty!").asRuntimeException());
		}
		
		
		List<String> result = new ArrayList<String>();
		
		result = proc.getNearStations(lat1, lon1, k);
		
		LocateResponse response = LocateResponse.newBuilder().addAllId(result).build();
		
		
		responseObserver.onNext(response);
		responseObserver.onCompleted();

	}

}