package ggc.exceptions;

public class UnknownTransactionKeyExceptionCore extends Exception {

    public UnknownTransactionKeyExceptionCore() {
        // do nothing
    }
    
    /**
    * @param description
    */
    public UnknownTransactionKeyExceptionCore(String description) {
        super(description);
    }
    
    /**
    * @param cause
    */
    public UnknownTransactionKeyExceptionCore(Exception cause) {
        super(cause);
    }
    
}