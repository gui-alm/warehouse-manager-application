package ggc.app.transactions;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import java.util.LinkedHashMap;

import ggc.WarehouseManager;
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.app.exceptions.UnknownProductKeyException;
import ggc.exceptions.UnknownPartnerKeyExceptionCore;
import ggc.exceptions.UnknownProductKeyExceptionCore;

/**
 * Register order.
 */
public class DoRegisterAcquisitionTransaction extends Command<WarehouseManager> {

  public DoRegisterAcquisitionTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_ACQUISITION_TRANSACTION, receiver);
    addStringField("partner", Prompt.partnerKey());
    addStringField("product", Prompt.productKey());
    addRealField("price", Prompt.price());
    addIntegerField("amount", Prompt.amount());
  }

  @Override
  public final void execute() throws CommandException {

    try {
      if(!_receiver.productIsKnown(stringField("product"))){
        if(Form.confirm(Prompt.addRecipe())){
          LinkedHashMap<String, Integer> recipe = new LinkedHashMap<>();
          int numberOfComponents = Form.requestInteger(Prompt.numberOfComponents());
          double aggravation = Form.requestReal(Prompt.alpha());
  
          for(int i = 0; i < numberOfComponents; i++){
            recipe.put(Form.requestString(Prompt.productKey()), 
            Form.requestInteger(Prompt.amount()));
          }
  
          _receiver.registerComplexAcquisition(stringField("partner"), 
          stringField("product"), realField("price"), 
          integerField("amount"), recipe, aggravation);
        } else {
          _receiver.registerSimpleAcquisition(stringField("partner"), 
          stringField("product"), realField("price"), integerField("amount"));
        }
      } else {
        _receiver.registerSimpleAcquisition(stringField("partner"), 
        stringField("product"), realField("price"), integerField("amount"));
      }

    } catch (UnknownPartnerKeyExceptionCore e){
      throw new UnknownPartnerKeyException(stringField("partner"));
    } catch (UnknownProductKeyExceptionCore e) {
      throw new UnknownProductKeyException(e.getProduct());
    }
  } 

}
