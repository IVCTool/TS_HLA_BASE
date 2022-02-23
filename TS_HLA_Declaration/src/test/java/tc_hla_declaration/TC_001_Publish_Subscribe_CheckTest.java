package tc_hla_declaration;

import nato.ivct.etc.fr.tc_hla_declaration.TC_001_Publish_Subscribe_Check;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.Assert.assertTrue;

import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib_if.IVCT_Verdict;

public class TC_001_Publish_Subscribe_CheckTest {
	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TC_001_Publish_Subscribe_CheckTest.class);

	private static BrokerService broker = null;
	protected static String tcParamJson = "{ 	" +
		"\"testDuration\"    : \"60\"," + 
		"\"resultDirectory\" : \"C:/Runtime_4_0_1_Mirror/IVCTsut/hw_iosb/TS-HLA-Declaration-2019\", " +
		"\"fomFiles\"        : [ " +
		"  { \"fileName\"    : \"C:/Runtime_4_0_1_Mirror/IVCTsut/hw_iosb/TS-HLA-Declaration-2019/HelloWorld.xml\" } " +
		"], " +
		"\"somFiles\"        : [ " +
		"  { \"fileName\"    : \"C:/Runtime_4_0_1_Mirror/IVCTsut/hw_iosb/TS-HLA-Declaration-2019/HelloWorldSOM.xml\" } " +
		"] " +
	  "}\""; 
	protected Logger runLogger = LoggerFactory.getLogger(TC_001_Publish_Subscribe_CheckTest.class);

	public void setUp(AbstractTestCase testCase) {
		// test case settings
		String tsName = "TS_HelloWord";
		String federationName = "HelloWorld";
		String sutName = "hw_iosb";
		String sutFederateName = "A";
		// MaK default
		// String settingsDesignator = "(setqb RTI_tcpPort 4000) (setqb RTI_tcpForwarderAddr \"rtiexec\")";
		// Pitch default
		// String settingsDesignator = "crcAddress=localhost:8989";
		String settingsDesignator = "crcAddress=192.168.178.45:8989";

		testCase.setSettingsDesignator(settingsDesignator);
		testCase.setFederationName(federationName);
		testCase.setSutName(sutName);
		testCase.setSutFederateName(sutFederateName);			
		testCase.setTcName(TC_001_Publish_Subscribe_CheckTest.class.getName());
		testCase.setTsName(tsName);
		testCase.setTcParam(tcParamJson);
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
    void testLogTestPurpose() {

    }

    @Test
	@EnabledIfEnvironmentVariable(named = "LRC_CLASSPATH", matches = ".*")
    void testPerformTest() {
		LOGGER.info ("test HLA declaration check");
		IVCT_Verdict verdict;
		TC_001_Publish_Subscribe_Check tc0001 = new TC_001_Publish_Subscribe_Check();

		setUp(tc0001);
		verdict = tc0001.execute(runLogger);
		runLogger.info("Test Case Verdict: {}", verdict);
		assertTrue(verdict.verdict == IVCT_Verdict.Verdict.PASSED);	
    }

    @Test
    void testPostambleAction() {

    }

    @Test
    void testPreambleAction() {

    }
}
