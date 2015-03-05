package org.datalorax.populace.field.visitor;

/**
 * @author datalorax - 04/03/2015.
 */
public class FieldAccessException extends RuntimeException {
    public FieldAccessException() {
        super();
    }

    public FieldAccessException(final String message) {
        super(message);
    }

    public FieldAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FieldAccessException(final Throwable cause) {
        super(cause);
    }

    protected FieldAccessException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
