package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import io.grpc.StatusRuntimeException;
import static io.grpc.Status.INVALID_ARGUMENT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.bicloin.hub.grpc.*;

public class SysStatusIT extends BaseIT {
	
	@Test
	public void sysStatusOKTest() {
		CtrlPingRequest request = CtrlPingRequest.newBuilder().setInput("sysStatus").build();
		CtrlPingResponse response = frontend.sys_status(request);
		assertEquals("hub ping OK rec ping OK", response.getOutput());
	}
}
