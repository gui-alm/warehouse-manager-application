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
 * Register order.
 */
public class DoRegisterBreakdownTransaction extends Command<WarehouseManager> {

  public DoRegisterBreakdownTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_BREAKDOWN_TRANSACTION, receiver);
    addStringField("partner", Prompt.partnerKey());
    addStringField("product", Prompt.productKey());
    addIntegerField("amount", Prompt.amount());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.registerBreakdown(stringField("partner"), 
      stringField("product"), integerField("amount"));
    } catch (UnavailableProductExceptionCore e) {
      throw new UnavailableProductException(e.getProductKey(), 
      e.getRequestedAmount(), e.getAvailableAmount());
    } catch (UnknownPartnerKeyExceptionCore e) {
      throw new UnknownPartnerKeyException(stringField("partner"));
    } catch (UnknownProductKeyExceptionCore e) {
      throw new UnknownProductKeyException(stringField("product"));
    }
  }

}
