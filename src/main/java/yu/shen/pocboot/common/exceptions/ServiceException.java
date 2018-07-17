package yu.shen.pocboot.common.exceptions;

/**
 * Created by sheyu on 7/17/2018.
 */
public class ServiceException extends RuntimeException {
    private String code;

    public ServiceException(String message, Throwable cause, String code) {
        super(message, cause);
        this.code = code;
    }

    public ServiceException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
