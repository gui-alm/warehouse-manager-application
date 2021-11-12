package ggc.products;

public class ComplexProduct extends Product {

    private Recipe _recipe;

    private double _aggravation;

    public ComplexProduct(String id, String supplier, double price, 
    double stock, double aggravation, Recipe recipe) {
        super(id, supplier, price, stock);
        this._aggravation = aggravation;
        this._recipe = recipe;
    }    
    
    @Override
    public Recipe getRecipe(){
        return this._recipe;
    }

    public double getAggravation(){
        return this._aggravation;
    }

    @Override
    public boolean isComplex(){
        return true;
    }

    @Override
    public String buildAttributesString(){
        return String.format("%s|%s|%s|%s|%s", getID(), Math.round(getPrice()), 
        Math.round(getStock()), getAggravation(), getRecipe().toString());
    }
}
