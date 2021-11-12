package ggc.utils;

import ggc.transactions.Acquisition;
import ggc.transactions.Breakdown;
import ggc.transactions.Sale;

public interface Visitor {
    
    public String visit(Acquisition acq);
    public String visit(Sale sale);
    public String visit(Breakdown breakdown);

}
