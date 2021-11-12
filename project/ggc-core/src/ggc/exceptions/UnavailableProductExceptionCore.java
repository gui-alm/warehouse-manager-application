package ggc.exceptions;

public class UnavailableProductExceptionCore extends Exception {

    private String _productID;
    private int _requested;
    private int _available;

    /**
    * @param description
    */
    public UnavailableProductExceptionCore(String productID, int requested, int available) {
        _productID = productID;
        _requested = requested;
        _available = available;
    }
    
    public String getProductKey(){
        return _productID;
    }

    public int getRequestedAmount(){
        return _requested;
    }

    public int getAvailableAmount(){
        return _available;
    }

    /**
    * @param cause
    */
    public UnavailableProductExceptionCore(Exception cause) {
        super(cause);
    }
    
}