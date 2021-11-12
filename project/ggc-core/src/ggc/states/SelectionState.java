package ggc.states;

import ggc.partners.Partner;

public class SelectionState extends State {
    public SelectionState(Partner partner){
        super(partner,"SELECTION");
    }

    public void upgrade(){
        setState(new EliteState(getPartner()));
    }
    
    public void downgrade(){
        setState(new NormalState(getPartner()));
    }
}
