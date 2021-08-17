package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import io.grpc.StatusRuntimeException;
import static io.grpc.Status.INVALID_ARGUMENT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import pt.tecnico.bicloin.hub.grpc.*;

public class LocateStationIT extends BaseIT {
	
	@Test
	public void locateStationOKTest() {	
		LocateRequest request = LocateRequest.newBuilder().setLocation(Location.newBuilder().setLatitude(1.1).setLongitude(-1.1).build()).setK(2).build();
		LocateResponse response = frontend.locate_station(request);
		String[] stations = {"ista", "ocea"};
		assertEquals(stations, response.getIdList());
	}
	
	@Test
	public void emptyLatitudeTest() {
		LocateRequest request = LocateRequest.newBuilder().setLocation(Location.newBuilder().setLatitude(0).setLongitude(-1.1).build()).setK(1).build();
		assertEquals(INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.locate_station(request))
		.getStatus()
		.getCode());
	}
	
	@Test
	public void emptyLongitudeTest() {
		LocateRequest request = LocateRequest.newBuilder().setLocation(Location.newBuilder().setLatitude(1.1).setLongitude(0).build()).setK(1).build();
		assertEquals(INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.locate_station(request))
		.getStatus()
		.getCode());
	}
	
	@Test
	public void emptyKTest() {
		LocateRequest request = LocateRequest.newBuilder().setLocation(Location.newBuilder().setLatitude(1.1).setLongitude(-1.1).build()).setK(0).build();
		assertEquals(INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.locate_station(request))
		.getStatus()
		.getCode());
	}
	
}
