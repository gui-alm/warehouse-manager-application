package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.app.exceptions.UnavailableProductException;
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.app.exceptions.UnknownProductKeyException;
import ggc.exceptions.UnavailableProductExceptionCore;
import ggc.exceptions.UnknownPartnerKeyExceptionCore;
import ggc.exceptions.UnknownProductKeyExceptionCore;

/**
 * 
 */
public class DoRegisterSaleTransaction extends Command<WarehouseManager> {

  public DoRegisterSaleTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_SALE_TRANSACTION, receiver);
    addStringField("partner", Prompt.partnerKey());
    addIntegerField("deadline", Prompt.paymentDeadline());
    addStringField("product", Prompt.productKey());
    addIntegerField("amount", Prompt.amount());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.registerSale(stringField("partner"), stringField("product"), 
      integerField("deadline"), integerField("amount"));
    } catch (UnavailableProductExceptionCore e) {
      throw new UnavailableProductException(e.getProductKey(), 
      e.getRequestedAmount(), e.getAvailableAmount());
    } catch (UnknownProductKeyExceptionCore e){
      throw new UnknownProductKeyException(stringField("product"));
    } catch (UnknownPartnerKeyExceptionCore e){
      throw new UnknownPartnerKeyException(stringField("partner"));
    }
  }

}
