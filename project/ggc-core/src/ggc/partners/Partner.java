package ggc.partners;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import ggc.states.NormalState;

import ggc.states.State;
import ggc.utils.Display;
import ggc.utils.Observer;

public class Partner implements Serializable, Observer, Display {
    
    private String _id;
    private String _name;
    private String _address;
    private String _delivery;

    private State _state;

    private double _points; 
    private double _purchasesValue = 0;
    private double _performedSalesValue = 0;
    private double _paidSalesValue = 0;

    private final double POINTS_TO_SELECTION = 2000;
    private final double POINTS_TO_ELITE = 25000;

    private List<String> _notifications = new LinkedList<>();

    public Partner(String id, String name, String address){
        this._id = id;
        this._name = name;
        this._address = address;
        this._points = 0;
        this._state = new NormalState(this);
    }

    public void setDeliveryMethod(String delivery){
        this._delivery = delivery;
    }

    public String getID(){
        return this._id;
    }

    public String getName(){
        return this._name;
    }

    public String getAddress(){
        return this._address;
    }

    public double getPoints(){
        return this._points;
    }

    public double getPurchasesValue(){
        return this._purchasesValue;
    }

    public void addPurchasesValue(double amount){
        this._purchasesValue += amount;
    }

    public double getPerformedSalesValue(){
        return this._performedSalesValue;
    }

    public void addPerformedSalesValue(double amount){
        this._performedSalesValue += amount;
    }

    public double getPaidSalesValue(){
        return this._paidSalesValue;
    }

    public void addPaidSalesValue(double amount){
        this._paidSalesValue += amount;
    }

    public List<String> getNotifications(){
        return this._notifications;
    }

    public String state(){
        return this._state.state();
    }

    public void setState(State state){
        this._state = state;
    }

    public void addPoints(double points){
        this._points += points;
    }

    public void removePoints(double points){
        this._points -= points;
    }

    @Override
    public void update(String productName, String notificationType, double price) {
        String newNotification = String.format("%s|%s|%s", notificationType, 
        productName, (int) Math.round(price));
        _notifications.add(newNotification);
    }

    public void clearNotifications(){
        this._notifications.clear();
    }

    public void checkStatus(int time, double price){

        if(time >= 0) {
            addPoints(price * 10);
            
            if(state().equals("NORMAL") && getPoints() > POINTS_TO_SELECTION) {
                _state.upgrade();
            }

            if(state().equals("SELECTION") && getPoints() > POINTS_TO_ELITE) {
                _state.upgrade();
            }

            return;
        }

        if(state().equals("NORMAL") && time < 0){
            removePoints(getPoints());
            return;
        }

        if(state().equals("SELECTION") && time < -2) {
            removePoints(0.90 * getPoints());
            _state.downgrade();
            return;
        }

        if(state().equals("ELITE") && time < -15) {
            removePoints(0.75 * getPoints());
            _state.downgrade();
            return;
        }

    }

    public String buildAttributesString(){
        return String.format("%s|%s|%s|%s|%s|%s|%s|%s", getID(), 
        getName(), getAddress(), this._state.state(), (int) getPoints(), 
        (int) Math.round(getPurchasesValue()), (int) Math.round(getPerformedSalesValue()), 
        (int) Math.round(getPaidSalesValue()));
    }

    public String display(){
        StringBuilder toDisplay = new StringBuilder();
        if(_delivery.equals("")) {
            for(String n : _notifications) {
                toDisplay.append(n);
                toDisplay.insert(0, _delivery + "\n");
                toDisplay.append("\n");
            }
        }
        return toDisplay.toString();
    }

}
