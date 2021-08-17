package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import io.grpc.StatusRuntimeException;
import static io.grpc.Status.INVALID_ARGUMENT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.bicloin.hub.grpc.*;

public class BikeUpIT extends BaseIT {
	
	@Test
	public void bikeUpOKTest() {
		BikeRequest request = BikeRequest.newBuilder().setUserId("eva")
				.setLocation(Location.newBuilder().setLatitude(38.7060).setLongitude(-9.1441).build())
				.setStationId("cais").setType("up").build();
		BikeResponse response = frontend.bike(request);
		assertEquals("bike_up success", response.getResponse());
	}
	
	@Test
	public void emptyUserIdTest() {
		BikeRequest request = BikeRequest.newBuilder().setUserId("")
				.setLocation(Location.newBuilder().setLatitude(38.7060).setLongitude(-9.1441).build())
				.setStationId("cais").setType("up").build();
		assertEquals(INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.bike(request))
		.getStatus()
		.getCode());
	}
	
	@Test
	public void emptyLatitudeTest() {
		BikeRequest request = BikeRequest.newBuilder().setUserId("eva")
				.setLocation(Location.newBuilder().setLatitude(0).setLongitude(-9.1441).build())
				.setStationId("cais").setType("up").build();
		assertEquals(INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.bike(request))
		.getStatus()
		.getCode());
	}
	
	@Test
	public void emptyLongitudeTest() {
		BikeRequest request = BikeRequest.newBuilder().setUserId("eva")
				.setLocation(Location.newBuilder().setLatitude(38.7060).setLongitude(0).build())
				.setStationId("cais").setType("up").build();
		assertEquals(INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.bike(request))
		.getStatus()
		.getCode());
	}
	
	@Test
	public void emptyStationIdTest() {
		BikeRequest request = BikeRequest.newBuilder().setUserId("eva")
				.setLocation(Location.newBuilder().setLatitude(38.7060).setLongitude(-9.1441).build())
				.setStationId("").setType("up").build();
		assertEquals(INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.bike(request))
		.getStatus()
		.getCode());
	}
	
}
