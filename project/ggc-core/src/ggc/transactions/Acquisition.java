package ggc.transactions;


import ggc.utils.Visitor;

public class Acquisition extends Transaction {
    
    private double _paidValue;

    public Acquisition(int index, String partnerID, String productID, 
    int amount, double price, int date){
        super(index, partnerID, productID, amount);
        _paidValue = price;
        setPaid(date);
    }

    private double getPaidValue(){
        return _paidValue;
    }

    public String toString(){
        return String.format("COMPRA|%s|%s|%s|%s|%s|%s", getIndex(), 
        getPartnerID(), getProductID(), getAmount(), 
        (int) Math.round(getPaidValue()), getPaymentDate());
    }

    @Override
    public String accept(Visitor visitor) {
        return visitor.visit(this);
    }

}

