package ggc.exceptions;

public class UnknownPartnerKeyExceptionCore extends Exception {

    public UnknownPartnerKeyExceptionCore() {
        // do nothing
    }
    
    /**
    * @param description
    */
    public UnknownPartnerKeyExceptionCore(String description) {
        super(description);
    }
    
    /**
    * @param cause
    */
    public UnknownPartnerKeyExceptionCore(Exception cause) {
        super(cause);
    }
    
}