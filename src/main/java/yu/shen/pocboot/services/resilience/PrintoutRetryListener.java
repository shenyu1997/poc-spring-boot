package yu.shen.pocboot.services.resilience;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class PrintoutRetryListener  extends RetryListenerSupport implements RetryListener {
    @Override
    public <T, E extends Throwable> boolean open(RetryContext retryContext, RetryCallback<T, E> retryCallback) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> open " + retryContext);
        return super.open(retryContext, retryCallback);
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext retryContext, RetryCallback<T, E> retryCallback, Throwable throwable) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> close" + retryContext);
        super.close(retryContext, retryCallback, throwable);
    }

}
