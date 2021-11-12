package ggc.products;

import java.io.Serializable;

public class Batch implements Serializable {

    private String _productID;
    private String _partnerID;

    private double _price;
    private double _stock;

    public Batch(String productID, String partnerID, double price, double stock){
        this._productID = productID;
        this._partnerID = partnerID;
        this._price = price;
        this._stock = stock;
    }

    public String getProductID(){
        return this._productID;
    }

    public String getPartnerID(){
        return this._partnerID;
    }

    public double getPrice(){
        return this._price;
    }

    public double getStock(){
        return this._stock;
    }

    public void addStock(double amount){
        _stock += amount;
    }

    public String buildAttributesString(){
        return String.format("%s|%s|%s|%s", getProductID(), 
        getPartnerID(), Math.round(getPrice()), Math.round(getStock()));
    }

    public String toString(){
        return this.buildAttributesString();
    }
}
