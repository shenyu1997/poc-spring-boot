package yu.shen.pocboot.common.exceptions;

public class ExceptionDTO {
    private String corrolationId, service, error, code, message;
    private long timestamp;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCorrolationId() {
        return corrolationId;
    }

    public void setCorrolationId(String corrolationId) {
        this.corrolationId = corrolationId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ExceptionDTO{" +
                "corrolationId='" + corrolationId + '\'' +
                ", service='" + service + '\'' +
                ", error='" + error + '\'' +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
