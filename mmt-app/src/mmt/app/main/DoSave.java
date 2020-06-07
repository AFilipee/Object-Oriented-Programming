package mmt.app.main;

import java.io.IOException;
import java.io.InvalidClassException;

import mmt.TicketOffice;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;
import pt.tecnico.po.ui.DialogException;
import java.io.IOException;

/**
 * §3.1.1. Save to file under current name (if unnamed, query for name).
 */
public class DoSave extends Command<TicketOffice> {
  
  Input<String> _file;

  /**
   * @param receiver
   */
  public DoSave(TicketOffice receiver) {
    super(Label.SAVE, receiver);
    _file = _form.addStringInput(Message.newSaveAs());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() {
    try {
      if (_receiver.ficheiroJaExiste()) {
        _receiver.save(_receiver.getFile());
      }

      else {
        _form.parse();
        _receiver.save(_file.value());
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}