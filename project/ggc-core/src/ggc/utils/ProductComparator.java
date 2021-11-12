package ggc.utils;

import java.util.Comparator;

import ggc.products.Product;

public class ProductComparator implements Comparator<Product> {

    @Override
    public int compare(Product o1, Product o2) {
        return o1.getID().compareToIgnoreCase(o2.getID());
    }

    
    
}
