package yu.shen.pocboot.common.exceptions;

/**
 * Created by sheyu on 7/17/2018.
 */
public class EntityNotFoundException extends ServiceException {
    public static final String CODE = "entity_not_found";

    public EntityNotFoundException(Class clazz, String idOrName) {
        super(String.format("Entity %s with id %s is not found",clazz.getCanonicalName(), idOrName), CODE);
    }
}
