package nato.ivct.etc.fr.tc_cs_verification;

import static org.junit.Assert.assertTrue;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_Verdict;

public class TestTC_001_Files_Check {

	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestTC_001_Files_Check.class);

	private static BrokerService broker = null;
//	protected static String tcParamJson = "{ \"growthRate\" : \"1.0003\", \"SOMfile\":  \"HelloWorld.xml\" }";
	protected static String tcParamJson = "{ 	" +
		"\"testDuration\"    : \"60\"," + 
		"\"resultDirectory\" : \"C:/Runtime_4_0_1_Mirror/IVCTsut/hw_iosb/TS-HLA-Verification-2019\", " +
		"\"fomFiles\"        : [ " +
		"  { \"fileName\"    : \"C:/Runtime_4_0_1_Mirror/IVCTsut/hw_iosb/TS-HLA-Verification-2019/HelloWorld.xml\" } " +
		"], " +
		"\"somFiles\"        : [ " +
		"  { \"fileName\"    : \"C:/Runtime_4_0_1_Mirror/IVCTsut/hw_iosb/TS-HLA-Verification-2019/HelloWorldSOM.xml\" } " +
		"] " +
	  "}\""; 
	protected Logger runLogger = LoggerFactory.getLogger(TestTC_001_Files_Check.class);

	public void setUp(AbstractTestCase testCase) {
		// test case settings
		String tsName = "TS_HelloWord";
		String federationName = "HelloWorld";
		String sutName = "hw_iosb";
		String sutFederateName = "A";
		// MaK default
		//String settingsDesignator = "(setqb RTI_tcpPort 4000) (setqb RTI_tcpForwarderAddr \"rtiexec\")";
		// Pitch default
		// String settingsDesignator = "localhost";
		String settingsDesignator = "(setqb RTI_tcpPort 4000) (setqb RTI_tcpForwarderAddr \"rtiexec\")";

		testCase.setSettingsDesignator(settingsDesignator);
		testCase.setFederationName(federationName);
		testCase.setSutName(sutName);
		testCase.setSutFederateName(sutFederateName);			
		testCase.setTcName(TestTC_001_Files_Check.class.getName());
		testCase.setTsName(tsName);
		testCase.setSkipOperatorMsg(true);
	}
	
	@BeforeAll
	public static void startBroker() throws Exception {
		// configure the broker
		broker = new BrokerService();
		broker.addConnector("tcp://localhost:61616"); 
		broker.setPersistent(false);
		broker.start();
		
		// force Factory to re-initialize itself
		nato.ivct.commander.Factory.props = null;
		nato.ivct.commander.Factory.initialize();
	}

	@AfterAll
	public static void stopBroker() throws Exception {
		broker.stop();
	}

	@Test
	@EnabledIfEnvironmentVariable(named = "LRC_CLASSPATH", matches = ".*")
	void test() {
		LOGGER.info ("test SOM/FOM file check");
		IVCT_Verdict verdict;
		TC_001_Files_Check tc0001 = new TC_001_Files_Check();

		setUp(tc0001);
		verdict = tc0001.execute(tcParamJson, runLogger);
		runLogger.info("Test Case Verdict: {}", verdict);
		assertTrue(verdict.verdict == IVCT_Verdict.Verdict.PASSED);	
	}


}
