package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import io.grpc.StatusRuntimeException;
import static io.grpc.Status.INVALID_ARGUMENT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.bicloin.hub.grpc.*;

public class BalanceIT extends BaseIT {
	
	@Test
	public void balanceOKTest() {
		BalanceRequest request = BalanceRequest.newBuilder().setId("balance").build();
		BalanceResponse response = frontend.balance(request);
		assertEquals(0, response.getBalance());
	}
	
	@Test
	public void emptyIdTest() {
		BalanceRequest request = BalanceRequest.newBuilder().setId("").build();
		assertEquals(INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.balance(request))
		.getStatus()
		.getCode());
	}
	
}