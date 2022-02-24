package tc_hla_declaration;

import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.tc_lib_if.OperatorService;
import de.fraunhofer.iosb.tc_lib_if.TcInconclusiveIf;

public class TestOperatorService implements OperatorService {
	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestOperatorService.class);

    @Override
    public OperatorService initialize(String sutName, String testSuiteId, String tc, String tcLabel) {
        LOGGER.trace("TestOperatorService for ({}, {}, {}, {})",sutName, testSuiteId, tc, tcLabel);
        return null;
    }

    @Override
    public void sendOperatorMsgAndWaitConfirmation(String text) throws TcInconclusiveIf {
        LOGGER.warn("TestOperatorService: {}", text);
        LOGGER.warn("suspend test execution for 5 sec");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.warn("continue test execution");
    }

    @Override
    public void sendTcStatus(String status, int percent) {
        LOGGER.info("TestOperatorService status: {} at {}", status, percent);
    }
    
}
