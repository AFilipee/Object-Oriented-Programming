package mmt.app.itineraries;

import mmt.TicketOffice;
import mmt.app.exceptions.BadDateException;
import mmt.app.exceptions.BadTimeException;
import mmt.app.exceptions.NoSuchItineraryException;
import mmt.app.exceptions.NoSuchPassengerException;
import mmt.app.exceptions.NoSuchStationException;
import mmt.exceptions.BadDateSpecificationException;
import mmt.exceptions.BadTimeSpecificationException;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.exceptions.NoSuchStationNameException;
import mmt.exceptions.NoSuchItineraryChoiceException;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

/**
 * §3.4.3. Add new itinerary.
 */
public class DoRegisterItinerary extends Command<TicketOffice> {

  Input<Integer> _id;
  Input<String> _departureStation;
  Input<String> _arrivalStation;
  Input<String> _date;
  Input<String> _time;
  Input<Integer> _itinerary;

  /**
   * @param receiver
   */
  public DoRegisterItinerary(TicketOffice receiver) {
    super(Label.REGISTER_ITINERARY, receiver);
    setValues();
  }

  public final void setValues() {
    _form.clear();
    _id = _form.addIntegerInput(Message.requestPassengerId());
    _departureStation = _form.addStringInput(Message.requestDepartureStationName());
    _arrivalStation = _form.addStringInput(Message.requestArrivalStationName());
    _date = _form.addStringInput(Message.requestDepartureDate());
    _time = _form.addStringInput(Message.requestDepartureTime());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    try {
      if (_id.value() != null)
        setValues();
      _form.parse();
      String itineraries = _receiver.searchItineraries(_id.value(), _departureStation.value(),
        _arrivalStation.value(), _date.value(), _time.value());
      _display.popup(itineraries);
      if (itineraries.equals("")) return;
      _form.clear();
      _itinerary = _form.addIntegerInput(Message.requestItineraryChoice());
      _form.parse();
      _receiver.commitItinerary(_id.value(), _itinerary.value());
    } catch (NoSuchPassengerIdException e) {
      throw new NoSuchPassengerException(e.getId());
    } catch (NoSuchStationNameException e) {
      throw new NoSuchStationException(e.getName());
    } catch (NoSuchItineraryChoiceException e) {
      throw new NoSuchItineraryException(e.getPassengerId(), e.getItineraryId());
    } catch (BadDateSpecificationException e) {
      throw new BadDateException(e.getDate());
    } catch (BadTimeSpecificationException e) {
      throw new BadTimeException(e.getTime());
    }
  }
}