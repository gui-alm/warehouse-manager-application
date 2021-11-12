package ggc.utils;

public interface Subject {
    
    public void registerObserver(Observer newObserver);
    public void unregisterObserver(Observer removeObserver);
    public void notifyObserver(String notificationType, double price);

}
