package ggc.app.main;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import java.io.FileNotFoundException;
import java.io.IOException;

import ggc.WarehouseManager;
//FIXME import classes
import ggc.exceptions.MissingFileAssociationException;

/**
 * Save current state to file under current name (if unnamed, query for name).
 */
class DoSaveFile extends Command<WarehouseManager> {

  /** @param receiver */
  DoSaveFile(WarehouseManager receiver) {
    super(Label.SAVE, receiver);
  }

  @Override
  public final void execute() throws CommandException {

    if(_receiver.hasNoFile()){
      try {
        _receiver.saveAs(Form.requestString(Prompt.newSaveAs()));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (MissingFileAssociationException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      try {
        _receiver.save();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (MissingFileAssociationException e) {
        e.printStackTrace();
      }
    }

  }

}
