package ggc.products;

import java.io.Serializable;
import java.util.ArrayList;

import ggc.utils.Observer;
import ggc.utils.Subject;

public class Product implements Serializable, Subject {

    private String _id;
    private String _supplier;

    private double _price;
    private double _stock;

    private ArrayList<Observer> _observers = new ArrayList<>();

    public Product(String id, String supplier, double price, double stock){
        this._id = id;
        this._supplier = supplier;
        this._price = price;
        this._stock = stock;
    }

    public String getID(){
        return this._id;
    }

    public String getSupplier(){
        return this._supplier;
    }

    public double getPrice(){
        return this._price;
    }

    public double getStock(){
        return this._stock;
    }

    public void setStock(int amount){
        this._stock = amount;
    }

    public void setPrice(double price){
        this._price = price;
    }

    public boolean isComplex(){
        return false;
    }

    public Recipe getRecipe(){
        return null;
    }

    public void addStock(double amount){

        if(getStock() == 0){
            _stock += amount;
            notifyObserver("NEW", _price);
        } else {
            _stock += amount;
        }
    }

    @Override
    public void registerObserver(Observer newObserver) {
        this._observers.add(newObserver);
    }

    @Override
    public void unregisterObserver(Observer removeObserver) {
        int index = this._observers.indexOf(removeObserver);
        _observers.remove(index);
    }

    @Override
    public void notifyObserver(String notificationType, double price) {
        for(Observer obs : _observers){
            obs.update(_id, notificationType, (int) Math.round(price));
        }
    }

    public boolean hasObserver(Observer partner){
        return _observers.contains(partner);
    }

    public String buildAttributesString(){
        return String.format("%s|%s|%s", getID(), Math.round(getPrice()), 
        Math.round(getStock()));
    }

    
}