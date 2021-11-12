package ggc.exceptions;


public class DuplicatePartnerKeyExceptionCore extends Exception {

    public DuplicatePartnerKeyExceptionCore() {
        // do nothing
    }
    
    /**
    * @param description
    */
    public DuplicatePartnerKeyExceptionCore(String description) {
        super(description);
    }
    
    /**
    * @param cause
    */
    public DuplicatePartnerKeyExceptionCore(Exception cause) {
        super(cause);
    }
    
}
