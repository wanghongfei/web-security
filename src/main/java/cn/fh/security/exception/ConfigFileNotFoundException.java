package cn.fh.security.exception;

/**
 * Created by whf on 3/4/16.
 */
public class ConfigFileNotFoundException extends RuntimeException {
    public ConfigFileNotFoundException(String msg) {
        super(msg);
    }
}
