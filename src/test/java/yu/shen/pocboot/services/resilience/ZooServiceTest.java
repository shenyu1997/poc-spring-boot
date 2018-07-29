package yu.shen.pocboot.services.resilience;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.ExhaustedRetryException;
import yu.shen.pocboot.IntegrationTest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ZooServiceTest extends IntegrationTest {

    @Autowired
    private ZooService zooService;

    @After
    public void after() {
        zooService.reset();
    }

    /**
     * there is ok after retry 2 times
     */
    @Test
    public void retryOK() {
        zooService.zoo1(1, new RemoteAccessException(""));
    }

    /**
     * this is be failed after retry 3 times
     */
    @Test(expected = RemoteAccessException.class)
    public void retryMaxAccountException() {
        zooService.zoo1(0, new RemoteAccessException(""));
    }

    @Test
    public void retryAfterMaxAccountException() {
        try {
            zooService.zoo1(0, new RemoteAccessException(""));
        } catch (Exception e) {}

        zooService.zoo1(0, new RemoteAccessException(""));
    }

    /**
     * this is be failed after retry 2 times, because exception is not allowed in retry policy
     */
    @Test(expected = HystrixBadRequestException.class)
    public void retryUnexpectedException() {
        zooService.zoo1(1, new HystrixBadRequestException(""));
    }

    @Test(expected = ExhaustedRetryException.class)
    public void circuitOK() {
        zooService.zoo2(1, new RemoteAccessException(""));
    }

    @Test
    public void circuitNotOpenOK() {
        try {
            zooService.zoo2(1, new RemoteAccessException(""));
        } catch (Exception e) {}
        zooService.zoo2(3, new RemoteAccessException(""));
    }


    @Test(expected = ExhaustedRetryException.class)
    public void circuitOpenOK() {
        try {
            zooService.zoo2(1, new RemoteAccessException(""));
        } catch (Exception e) {}
        try {
            zooService.zoo2(1, new RemoteAccessException(""));
        } catch (Exception e) {}
        zooService.zoo2(3, new RemoteAccessException(""));
    }

    @Test
    public void circuitOpenAndClose() {
        try {
            zooService.zoo2(1, new RemoteAccessException(""));
        } catch (Exception e) {}
        try {
            zooService.zoo2(1, new RemoteAccessException(""));
        } catch (Exception e) {}

        try {
            Thread.sleep(20000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        zooService.zoo2(3, new RemoteAccessException(""));
    }

    @Test(expected = ExhaustedRetryException.class)
    public void circuitOpenAfterLastFailure() {
        try {
            zooService.zoo2(1, new RemoteAccessException(""));
        } catch (Exception e) {
            System.out.println("==========================> 1" + e.getMessage());
        }
        try {
            zooService.zoo2(1, new RemoteAccessException(""));
        } catch (Exception e) {
            System.out.println("==========================> 2" + e.getMessage());
        }

        //failure call in 20000L ms open window,
        try {
            Thread.sleep(5000L);
            zooService.zoo2(3, new RemoteAccessException(""));
        } catch (Exception e) {
            System.out.println("==========================> 3" + e.getMessage());
        }

        //then, after 15000L ms, call will be failure because the circuit is in open still.
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        zooService.zoo2(3, new RemoteAccessException(""));
        System.out.println("==========================> 4");
    }

    @Test
    public void circuitOpenAndCloseInFailureCall() {
        try {
            zooService.zoo2(1, new RemoteAccessException(""));
        } catch (Exception e) {
            System.out.println("==========================> 1" + e.getMessage());
        }
        try {
            zooService.zoo2(1, new RemoteAccessException(""));
        } catch (Exception e) {
            System.out.println("==========================> 2" + e.getMessage());
        }

        //failure call in 20000L ms open window,
        try {
            Thread.sleep(5000L);
            zooService.zoo2(3, new RemoteAccessException(""));
        } catch (Exception e) {
            System.out.println("==========================> 3" + e.getMessage());
        }

        //then, after 15000L ms, call will be failure because the circuit is in open still.
        try {
            Thread.sleep(10000L);
            zooService.zoo2(3, new RemoteAccessException(""));
        } catch (Exception e) {
            System.out.println("==========================> 4 " + e);
        }

        //then, after 5000L ms, call will be successful because the circuit is in close (total 20).
        try {
            Thread.sleep(5000L);
        } catch (Exception e) {
        }
        zooService.zoo2(3, new RemoteAccessException(""));
        System.out.println("==========================> 5 " );
    }


    @Test
    public void circuitHalfOpenFailureCall() {
        try {
            zooService.zoo2(1, new RemoteAccessException(""));
        } catch (Exception e) {
            System.out.println("==========================> 1" + e.getMessage());
        }
        try {
            zooService.zoo2(1, new RemoteAccessException(""));
        } catch (Exception e) {
            System.out.println("==========================> 2" + e.getMessage());
        }

        //failure call in 20000L ms open window,
        try {
            Thread.sleep(5000L);
            zooService.zoo2(3, new RemoteAccessException(""));
        } catch (Exception e) {
            System.out.println("==========================> 3" + e.getMessage());
        }

        //then, after 15000L ms, call will be failure because the circuit is in open still.
        try {
            Thread.sleep(10000L);
            zooService.zoo2(3, new RemoteAccessException(""));
        } catch (Exception e) {
            System.out.println("==========================> 4 " + e);
        }

        //then, after 5000L ms, call with failure, and circuit open other 20s.
        try {
            Thread.sleep(5000L);
            zooService.zoo2(1, new RemoteAccessException(""));
        } catch (Exception e) {
            System.out.println("==========================> 5 "  + e);
        }

        //after 19000L ms, call will be failure because the circuit is still close (total 20).
        try {
            Thread.sleep(19000L);

        } catch (Exception e) {
            System.out.println("==========================> 5 "  + e);
        }
        zooService.zoo2(3, new RemoteAccessException(""));
    }


    @Test
    public void circuitOpenTimeoutOK() {
        try {
            zooService.zoo2(1, new RemoteAccessException(""));
        } catch (Exception e) {}

        try {
            Thread.sleep(5000L);
            zooService.zoo2(1, new RemoteAccessException(""));
        } catch (Exception e) {}

        zooService.zoo2(3, new RemoteAccessException(""));
    }

    @Test
    public void zoo3() {
        String result = zooService.zoo3(new RuntimeException("lalal"));
        assertThat(result, equalTo("lalal"));
    }

    @Test
    public void zoo3Open() {
        String result = null;
        System.out.println("caller thread id:" + Thread.currentThread().getId());
        for(int i = 0; i<100; i++) {
            result = zooService.zoo3(new RuntimeException(String.valueOf(i)));
        }

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i = 100; i<200; i++) {
            result = zooService.zoo3(new RuntimeException(String.valueOf(i)));
        }

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        zooService.zoo3(null);  //open circuit
        for(int i = 200; i<300; i++) {
            result = zooService.zoo3(new RuntimeException(String.valueOf(i)));
        }

        assertThat(result, equalTo("299"));
    }

    @Test(expected = RuntimeException.class)
    public void zoo3Throw() {
        System.out.println("caller thread id:" + Thread.currentThread().getId());
        String result = zooService.zoo3(new HystrixBadRequestException("HystrixBadRequestException", new RuntimeException("this")));
        System.out.println(result);

    }

    @Test
    public void circuitRetry() {
        zooService.restCall();
    }

}
