package ggc.transactions;

import java.io.Serializable;

import ggc.utils.Visitor;

public abstract class Transaction implements Serializable {
    
    private int _index;
    private int _paymentDate;
    private int _amount;

    private String _partnerID;
    private String _productID;

    private boolean _paid;

    public Transaction(int index, String partnerID, String productID, int amount){
        _index = index;
        _partnerID = partnerID;
        _productID = productID;
        _amount = amount;
    }

    public int getIndex(){
        return _index;
    }

    public int getPaymentDate(){
        return _paymentDate;
    }

    private void setPaymentDate(int paymentDate){
        this._paymentDate = paymentDate;
    }

    public void setPaid(int paymentDate){
        setPaymentDate(paymentDate);
        _paid = true;
    }

    public boolean isPaid(){
        return _paid;
    }

    public String getPartnerID(){
        return _partnerID;
    }

    public int getAmount(){
        return _amount;
    }

    public String getProductID(){
        return _productID;
    }

    public void calculatePrice(int date, String status){};

    public abstract String accept(Visitor visitor);
}   
