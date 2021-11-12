package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.app.exceptions.UnknownProductKeyException;

import ggc.exceptions.UnknownPartnerKeyExceptionCore;
import ggc.exceptions.UnknownProductKeyExceptionCore;

/**
 * Toggle product-related notifications.
 */
class DoToggleProductNotifications extends Command<WarehouseManager> {

  DoToggleProductNotifications(WarehouseManager receiver) {
    super(Label.TOGGLE_PRODUCT_NOTIFICATIONS, receiver);
    addStringField("partner", Prompt.partnerKey());
    addStringField("product", Prompt.productKey());
  }

  @Override
  public void execute() throws CommandException {
    try {
      _receiver.toggleProductNotifications(stringField("product"), stringField("partner"));
    } catch (UnknownProductKeyExceptionCore e) {
      throw new UnknownProductKeyException(stringField("product"));
    } catch (UnknownPartnerKeyExceptionCore e) {
      throw new UnknownPartnerKeyException(stringField("partner"));
    }
  }

}
