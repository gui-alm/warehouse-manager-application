package ggc.exceptions;


public class InvalidDateExceptionCore extends Exception {

    public InvalidDateExceptionCore() {
        // do nothing
    }
    
    /**
    * @param description
    */
    public InvalidDateExceptionCore(String description) {
        super(description);
    }
    
    /**
    * @param cause
    */
    public InvalidDateExceptionCore(Exception cause) {
        super(cause);
    }
    
}
