package ggc.states;

import ggc.partners.Partner;

public class EliteState extends State {

    public EliteState(Partner partner){
        super(partner, "ELITE");
    }

    public void upgrade(){}
    
    public void downgrade(){
        setState(new SelectionState(getPartner()));
    }
    
}
