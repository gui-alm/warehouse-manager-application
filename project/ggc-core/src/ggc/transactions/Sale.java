package ggc.transactions;

import ggc.utils.Visitor;

public class Sale extends Transaction {

    private int _deadline;
    private double _price;
    private double _basePrice;
    private boolean _simpleProduct;

    public Sale(int index, String partnerID, String productID, int amount, 
    int paymentDeadline, double basePrice, boolean simple) {
        super(index, partnerID, productID, amount);
        _deadline = paymentDeadline;
        _basePrice = basePrice;
        _simpleProduct = simple;
    }

    public int getDeadline(){
        return this._deadline;
    }

    public double getBasePrice(){
        return this._basePrice;
    }

    public double getPriceToPay(){
        return this._price;
    }

    public void setPriceToPay(double price){
        _price = price;
    }

    public int getProductN(){
        return _simpleProduct ? 5 : 3;
    }

    public String toString(){
        StringBuilder result = new StringBuilder(
            String.format("VENDA|%s|%s|%s|%s|%s|%s|%s", getIndex(), getPartnerID(), 
        getProductID(), getAmount(), (int) Math.round(getBasePrice()), 
        (int) Math.round(getPriceToPay()), getDeadline()));

        if(isPaid()){
            return result.append(String.format("|%s", getPaymentDate())).toString();
        } else {
            return result.toString();
        }
    }

    public String getTimePeriod(int date){
        if(_deadline - date >= getProductN()) { 
            return "P1";
        }

        if((0 <= (_deadline - date)) && ((_deadline - date) < getProductN())) { 
            return "P2";
        }

        if((0 < (date - _deadline)) && ((date - _deadline) <= getProductN())) { 
            return "P3";
        } else { 
            return "P4";
        }
    }

    @Override
    public void calculatePrice(int date, String status){

        if(isPaid())
            return;

        int daysAfterDeadline = date - _deadline;

        if(getTimePeriod(date).equals("P1")) { 
            setPriceToPay(0.90 * getBasePrice());
            return;
        }

        if(getTimePeriod(date).equals("P2")) {
            
            switch(status){
                case "NORMAL":
                    setPriceToPay(getBasePrice());
                    break;
                case "SELECTION":
                    if(daysAfterDeadline > -2) { 
                        setPriceToPay(getBasePrice());
                    } else { 
                        setPriceToPay(0.95 * getBasePrice());
                    }
                    break;
                case "ELITE":
                    setPriceToPay(0.90 * getBasePrice());
                    break;
            }
            return;
        }

        if(getTimePeriod(date).equals("P3")){

            switch(status){
                case "NORMAL":
                    setPriceToPay(getBasePrice() + (getBasePrice() * 
                    (0.05 * daysAfterDeadline)));
                    break;
                case "SELECTION":
                    if(daysAfterDeadline > 1) { 
                        setPriceToPay(getBasePrice() + getBasePrice() * 
                        (0.02 * daysAfterDeadline));
                    } else { 
                        setPriceToPay(getBasePrice());
                    }
                    break;
                case "ELITE":
                    setPriceToPay(0.95 * getBasePrice());
                    break;
            }
            return;
        }

        if(getTimePeriod(date).equals("P4")){
            switch(status){
                case "NORMAL":
                    setPriceToPay(getBasePrice() + (getBasePrice() * 
                    (0.10 * daysAfterDeadline)));
                    break;
                case "SELECTION":
                    setPriceToPay(getBasePrice() + (getBasePrice() * 
                    (0.05 * daysAfterDeadline)));
                    break;
                case "ELITE":
                    setPriceToPay(getBasePrice());
                    break;
            }
        }

    }

    @Override
    public String accept(Visitor visitor) {
        return visitor.visit(this);
    }
    
}
