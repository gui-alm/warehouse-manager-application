package ggc.exceptions;

public class UnknownProductKeyExceptionCore extends Exception {

    private String _productID;

    public UnknownProductKeyExceptionCore(){
        // do nothing
    }

    public UnknownProductKeyExceptionCore(String productID) {
        _productID = productID;
    }
    
    public String getProduct(){
        return _productID;
    }
    
    /**
    * @param cause
    */
    public UnknownProductKeyExceptionCore(Exception cause) {
        super(cause);
    }
    
}