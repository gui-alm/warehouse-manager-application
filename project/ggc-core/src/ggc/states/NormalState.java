package ggc.states;

import ggc.partners.Partner;

public class NormalState extends State {

    public NormalState(Partner partner){
        super(partner,"NORMAL");
    }

    public void upgrade(){
        setState(new SelectionState(getPartner()));
    }
    public void downgrade(){}
    
}
