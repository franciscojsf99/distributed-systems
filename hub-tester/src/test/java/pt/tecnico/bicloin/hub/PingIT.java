package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import io.grpc.StatusRuntimeException;
import static io.grpc.Status.INVALID_ARGUMENT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.bicloin.hub.grpc.*;

public class PingIT extends BaseIT {
	
	@Test
	public void pingOKTest() {
		PingRequest request = PingRequest.newBuilder().setInput("hub ping").build();
		PingResponse response = frontend.ping(request);
		assertEquals("Hub is running.", response.getOutput());
	}
}
