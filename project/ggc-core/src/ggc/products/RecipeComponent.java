package ggc.products;

import java.io.Serializable;

public class RecipeComponent implements Serializable {

    private String _product;
    private int _quantity;

    protected RecipeComponent(String product, int quantity){
        _product = product;
        _quantity = quantity;
    }

    public String getProduct(){ return _product;}
    public int getQuantity(){ return _quantity;}
    
}
