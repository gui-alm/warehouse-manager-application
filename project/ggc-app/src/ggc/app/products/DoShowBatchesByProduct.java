package ggc.app.products;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.app.exceptions.UnknownProductKeyException;
import ggc.exceptions.UnknownProductKeyExceptionCore;

/**
 * Show all products.
 */
class DoShowBatchesByProduct extends Command<WarehouseManager> {

  DoShowBatchesByProduct(WarehouseManager receiver) {
    super(Label.SHOW_BATCHES_BY_PRODUCT, receiver);
    addStringField("productName", Prompt.productKey());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _display.popup(_receiver.getBatchesByProduct(stringField("productName")));
    } catch (UnknownProductKeyExceptionCore e) {
      throw new UnknownProductKeyException(stringField("productName"));
    }
  } 
}
