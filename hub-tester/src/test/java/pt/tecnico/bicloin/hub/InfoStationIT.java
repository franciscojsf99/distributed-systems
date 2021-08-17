package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import io.grpc.StatusRuntimeException;
import static io.grpc.Status.INVALID_ARGUMENT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.bicloin.hub.grpc.*;

public class InfoStationIT extends BaseIT {
	
	@Test
	public void infoStationOKTest() {
		InfoRequest request = InfoRequest.newBuilder().setId("ista").build();
		InfoResponse response = frontend.info_station(request);
		assertEquals("IST Alameda", response.getName());
		assertEquals((double)38.7369, response.getLocation().getLatitude());
		assertEquals((double)-9.1366, response.getLocation().getLongitude());
		assertEquals(20, response.getNrDocks());
		assertEquals(3, response.getPrize());
		assertEquals(19, response.getNrBikes());
		assertEquals(0, response.getStatistics().getBikeUps());
		assertEquals(0, response.getStatistics().getBikeDowns());
	}
	
	@Test
	public void emptyIdTest() {
		InfoRequest request = InfoRequest.newBuilder().setId("").build();
		assertEquals(INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.info_station(request))
		.getStatus()
		.getCode());
	}
	
}
