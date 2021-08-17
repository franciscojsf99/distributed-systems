package pt.tecnico.rec;

import org.junit.jupiter.api.*;

import io.grpc.StatusRuntimeException;
import static io.grpc.Status.INVALID_ARGUMENT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.rec.grpc.*;

public class ReadIT extends BaseIT {
	
	@Test
	public void readWithoutRecsTest() {
		ReadRequest request = ReadRequest.newBuilder().setId("ab").build();
		ReadResponse response = frontend.read(request);
		double value = 0;
		assertEquals(value, response.getValue());
	}
	
	@Test
	public void readOKTest() {
		double value = 10;
		WriteRequest writeRequest = WriteRequest.newBuilder().setId("eva").setValue(value).build();
		WriteResponse writeResponse = frontend.write(writeRequest);
		assertEquals("Record write success", writeResponse.getResponse());
		ReadRequest readRequest = ReadRequest.newBuilder().setId("eva").build();
		ReadResponse readResponse = frontend.read(readRequest);
		assertEquals(value, readResponse.getValue());
	}
	
	@Test
	public void emptyReadTest() {
		ReadRequest readRequest = ReadRequest.newBuilder().setId("").build();
		assertEquals(INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.read(readRequest))
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
