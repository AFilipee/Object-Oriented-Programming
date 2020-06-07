package mmt.app.main;

import mmt.TicketOffice;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * §3.1.1. Open existing document.
 */
public class DoOpen extends Command<TicketOffice> {

  Input<String> _file;

  /**
   * @param receiver
   */
  public DoOpen(TicketOffice receiver) {
    super(Label.OPEN, receiver);
    _file = _form.addStringInput(Message.openFile());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() {
    try {
      _form.parse();
      _receiver.load(_file.value());
    }
    catch (FileNotFoundException fnfe) {
      _display.popup(Message.fileNotFound());
    }
    catch (ClassNotFoundException | IOException e) {
      // shouldn't happen in a controlled test setup
      e.printStackTrace();
    }
  }
}