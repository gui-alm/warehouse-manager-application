package ggc.utils;

import java.util.Comparator;

import ggc.products.Batch;

public class BatchComparatorPrice  implements Comparator<Batch>{

    @Override
    public int compare(Batch o1, Batch o2) {
        
        Double price1 = o1.getPrice(), price2 = o2.getPrice();
        Double stock1 = o1.getStock(), stock2 = o2.getStock();

        String product1 = o1.getProductID(), product2 = o2.getProductID();
        String partner1 = o1.getPartnerID(), partner2 = o2.getPartnerID();

        int priceCompare = price1.compareTo(price2);
        int stockCompare = stock1.compareTo(stock2);

        if(product1.equalsIgnoreCase(product2) & partner1.equalsIgnoreCase(partner2)){

            return (priceCompare == 0) ? stockCompare : priceCompare;

        }

        return 0;

    }
    
}
