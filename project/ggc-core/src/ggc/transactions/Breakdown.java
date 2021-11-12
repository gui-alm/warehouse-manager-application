package ggc.transactions;

import ggc.utils.Visitor;

public class Breakdown extends Transaction {

    private double _saleValue = 0;
    private double _baseValue = 0;

    private double _pricePaid = 0;

    private String _description;

    public Breakdown(int index, String partnerID, String productID, int amount) {
        super(index, partnerID, productID, amount);
        
    }

    public double getSaleValue(){
        return this._saleValue;
    }

    public void setSaleValue(double amount){
        this._saleValue = amount;
    }

    public void calculateBasePrice(double componentsPrice){
        this._baseValue = (getSaleValue() - componentsPrice);
    }

    public void calculatePricePaid(int date){
        if(_baseValue < 0){
            _pricePaid = 0;
        } else {
            _pricePaid = _baseValue;
        }

        setPaid(date);
    }

    public double getPricePaid(){
        return _pricePaid;
    }

    public double getBaseValue(){
        return this._baseValue;
    }

    public String getDescription(){
        return _description;
    }

    public void setDescription(String result){
        _description = result;
    }

    public String toString(){
        return String.format("DESAGREGAÇÃO|%s|%s|%s|%s|%s|%s|%s|%s", 
        getIndex(), getPartnerID(), getProductID(), getAmount(), (int) Math.round(getBaseValue()), 
        (int) Math.round(getPricePaid()), getPaymentDate(), getDescription());
    }

    @Override
    public String accept(Visitor visitor) {
        return visitor.visit(this);
    }
    
}
