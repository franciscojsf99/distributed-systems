package pt.tecnico.bicloin.hub;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.*;


public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;
	static HubFrontend frontend;
	
	@BeforeAll
	public static void oneTimeSetup () throws IOException {
		testProps = new Properties();
		
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Test properties:");
			System.out.println(testProps);
		}catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}
		
		final String host = testProps.getProperty("server.host");
		final String port = testProps.getProperty("server.port");
		frontend = new HubFrontend(host, port, "/grpc/bicloin/rec/1");
	}
	
	@AfterAll
	public static void cleanup() {
		
	}

}
