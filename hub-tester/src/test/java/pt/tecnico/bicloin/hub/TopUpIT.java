package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import io.grpc.StatusRuntimeException;
import static io.grpc.Status.INVALID_ARGUMENT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.bicloin.hub.grpc.*;

public class TopUpIT extends BaseIT {
	
	@Test
	public void topUpOKTest() {
		int balance;
		TopUpRequest request = TopUpRequest.newBuilder().setId("topup").build();
		TopUpResponse response = frontend.top_up(request);
		balance = 0;
		assertEquals(balance, response.getBalance());
	}
	
	@Test
	public void emptyIdTest() {
		TopUpRequest request = TopUpRequest.newBuilder().setId("").setValue(10).setTelephone("1").build();
		assertEquals(INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.top_up(request))
		.getStatus()
		.getCode());
	}
	
	@Test
	public void emptyValueTest() {
		TopUpRequest request = TopUpRequest.newBuilder().setId("topUpValue").setValue(0).setTelephone("1").build();
		assertEquals(INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.top_up(request))
		.getStatus()
		.getCode());
	}
	
	@Test
	public void emptyTelephoneTest() {
		TopUpRequest request = TopUpRequest.newBuilder().setId("topUpTelephone").setValue(10).setTelephone("0").build();
		assertEquals(INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.top_up(request))
		.getStatus()
		.getCode());
	}
	
}
