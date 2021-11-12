package ggc.app.main;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.app.exceptions.InvalidDateException;
import ggc.exceptions.InvalidDateExceptionCore;

/**
 * Advance current date.
 */
class DoAdvanceDate extends Command<WarehouseManager> {

  DoAdvanceDate(WarehouseManager receiver) {
    super(Label.ADVANCE_DATE, receiver);
    addIntegerField("amount", Prompt.daysToAdvance());
  }

  @Override
  public final void execute() throws CommandException {
    try{
      _receiver.advanceDate(integerField("amount"));
    } catch(InvalidDateExceptionCore e){
      throw new InvalidDateException(integerField("amount"));
    }
  }
}
