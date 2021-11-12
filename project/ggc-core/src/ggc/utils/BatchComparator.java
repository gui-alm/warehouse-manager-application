package ggc.utils;

import java.util.Comparator;

import ggc.products.Batch;

public class BatchComparator implements Comparator<Batch> {

    @Override
    public int compare(Batch o1, Batch o2) {

        int productCompare = o1.getProductID().compareToIgnoreCase(o2.getProductID());
        int partnerCompare = o1.getPartnerID().compareToIgnoreCase(o2.getPartnerID());

        if(productCompare == 0){
            return partnerCompare;
        } else {
            return productCompare;
        }
    }
    
}
