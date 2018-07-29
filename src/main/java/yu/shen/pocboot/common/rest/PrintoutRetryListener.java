package yu.shen.pocboot.common.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class PrintoutRetryListener  extends RetryListenerSupport implements RetryListener {
    private Logger logger = LoggerFactory.getLogger("yu.shen.poc.common.rest");

    @Override
    public <T, E extends Throwable> boolean open(RetryContext retryContext, RetryCallback<T, E> retryCallback) {
        logger.debug("Retry is opened, retryContext:{}, retryCallback:{}", retryContext, retryCallback);
        return super.open(retryContext, retryCallback);
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext retryContext, RetryCallback<T, E> retryCallback, Throwable throwable) {
        logger.debug("Retry is closed, retryContext:{}, retryCallback:{}, throwable:{}", retryContext, retryCallback, throwable);
        super.close(retryContext, retryCallback, throwable);
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        logger.debug("Retry is on error, retryContext:{}, retryCallback:{}, throwable:{}", context, callback, throwable);
        super.onError(context, callback, throwable);
    }
}
