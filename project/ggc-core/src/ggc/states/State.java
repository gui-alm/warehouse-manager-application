package ggc.states;

import java.io.Serializable;

import ggc.partners.Partner;

public abstract class State implements Serializable {

    private Partner _partner;
    private String _state;

    public State(Partner partner, String state){
        _partner = partner;
        _state = state;
    }

    public Partner getPartner(){
        return _partner;
    }

    public void setState(State state){
        _partner.setState(state);
    }

    public abstract void upgrade();
    public abstract void downgrade();

    public String state(){
        return _state;
    }
    
}
