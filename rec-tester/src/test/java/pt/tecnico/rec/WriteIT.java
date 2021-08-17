package pt.tecnico.rec;

import org.junit.jupiter.api.*;

import io.grpc.StatusRuntimeException;
import static io.grpc.Status.INVALID_ARGUMENT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.rec.grpc.*;

public class WriteIT extends BaseIT {
	
	@Test
	public void writeOKTest() {
		double value = 10;
		WriteRequest request = WriteRequest.newBuilder().setId("ab").setValue(value).build();
		WriteResponse response = frontend.write(request);
		assertEquals("Record write success", response.getResponse());
	}
	
	@Test
	public void emptyWriteTest() {
		WriteRequest writeRequest = WriteRequest.newBuilder().setId("").build();
		assertEquals(INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.write(writeRequest))
		.getStatus()
		.getCode());
	}
	
	
	@AfterEach
	public void cleanUp() {
		double value = 0;
		WriteRequest writeRequest = WriteRequest.newBuilder().setId("ab").setValue(value).build();
		frontend.write(writeRequest);
	}
	
}