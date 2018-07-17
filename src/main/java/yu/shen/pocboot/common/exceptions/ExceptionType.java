package yu.shen.pocboot.common.exceptions;

public enum ExceptionType {
    ENTITY_NOT_FOUND("Entity Not Found Error");


    private String description;

    ExceptionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
