package cs544;

import cs544.service.LambdaInvoker;
import cs544.service.SQSMessageListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LambdaInvokerTest {
    @Autowired
    private LambdaInvoker invoker;
    @Test
    void receiveAndProcessMessages() {
//        invoker.invokeLambdaFunction("testing");
    }
}