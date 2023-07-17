package cs544;

import cs544.service.LambdaInvoker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LambdaInvokerTest {
    @Autowired
    private LambdaInvoker invoker;
    @Test
    void receiveAndProcessMessages() throws InterruptedException {
//        invoker.sendMessage("testing");
//        Thread.sleep(5000);
    }
}