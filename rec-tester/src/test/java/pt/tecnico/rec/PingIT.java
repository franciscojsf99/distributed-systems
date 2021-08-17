package pt.tecnico.rec;

import org.junit.jupiter.api.*;

import io.grpc.StatusRuntimeException;
import static io.grpc.Status.INVALID_ARGUMENT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.rec.grpc.*;

public class PingIT extends BaseIT {
	
	@Test
	public void pingOKTest() {
		PingRequest request = PingRequest.newBuilder().setInput("rec ping").build();
		PingResponse response = frontend.ping(request);
		assertEquals("Rec is running.", response.getOutput());
	}
	
	@Test
	public void emptyPingTest() {
		PingRequest request = PingRequest.newBuilder().setInput("").build();
		assertEquals(INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.ping(request))
		.getStatus()
		.getCode());
	}
	
}
