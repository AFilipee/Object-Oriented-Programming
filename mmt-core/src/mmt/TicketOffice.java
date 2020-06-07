package mmt;

import mmt.exceptions.BadDateSpecificationException;
import mmt.exceptions.BadTimeSpecificationException;
import mmt.exceptions.ImportFileException;
import mmt.exceptions.InvalidPassengerNameException;
import mmt.exceptions.MissingFileAssociationException;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.exceptions.NoSuchServiceIdException;
import mmt.exceptions.NoSuchStationNameException;
import mmt.exceptions.NoSuchItineraryChoiceException;
import mmt.exceptions.NonUniquePassengerNameException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;


/**
 * Façade for handling persistence and other functions.
 */
public class TicketOffice {

  /** The object doing most of the actual work. */
  private TrainCompany _trainCompany;

  public TicketOffice() {
    _trainCompany = new TrainCompany();
    _checkExisteFicheiro = false;
  }

  public TrainCompany getTrainCompany() {
    return _trainCompany;
  }

  public void reset() {
    _trainCompany.resetClear();
  }

  /**---- IO: ----*/

  /** Verifica se já foi guardado algum ficheiro anteriormente */
  private boolean _checkExisteFicheiro;

  /** Nome do documento quando é guardado */
  private String _file;

  public boolean ficheiroJaExiste() {
    return _checkExisteFicheiro;
  }

  public String getFile() {
    return _file;
  }

  public void save(String filename) throws IOException {
    if (!_trainCompany.houveAlteracoes())
      return;      // Não necessita de guardar novo ficheiro caso não tenha ocorrido alterações

    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));

    if (!_checkExisteFicheiro) {
      _checkExisteFicheiro = true;
      _file = filename;
    }

    out.writeObject(_trainCompany);
    out.close();
    _trainCompany.alteracoesGuardadas();
  }

  public void load(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {
    ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
    _trainCompany = (TrainCompany) in.readObject();
    in.close();

    _checkExisteFicheiro = true;
    _file = filename;
  }

  public void importFile(String datafile) throws ImportFileException {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(datafile));
      String line;
      while ((line = reader.readLine()) != null) {
        String[] fields = line.split("\\|");
        if (fields[0].equals("SERVICE"))
          _trainCompany.addService(fields);
        else if (fields[0].equals("ITINERARY"))
          _trainCompany.addItinerary(fields);
        else {
          try {
            _trainCompany.registerPassenger(fields[1]);
          }
          catch (NonUniquePassengerNameException e) {
            /** Na verdade, este erro nunca é apanhado dado que se assume que os ficheiros a importar
            não têm entradas mal-formadas. Alternativamente, poder-se-ia criar um método
            registerPassenger dedicado unicamente a este método importFile. */
          }
        }
      }
      reader.close();
    }
    catch (FileNotFoundException e) { }
    catch (IOException e)           { }
  }

  /**---- Services: ----*/
  public String showAllServices() {
    return _trainCompany.showServices();
  }

  public String showServiceByNumber(int number) throws NoSuchServiceIdException {
    return _trainCompany.showService(number);
  }

  public String showServicesDepartingFromStation(String station) throws NoSuchStationNameException {
    return _trainCompany.showServicesByDeparture(station);
  }

  public String showServicesArrivingAtStation(String station) throws NoSuchStationNameException {
    return _trainCompany.showServicesByArrival(station);
  }

  /**---- Passengers: ----*/
  public String showAllPassengers() {
    return _trainCompany.showPassengers();
  }

  public String showPassengerById(int id) throws NoSuchPassengerIdException {
    return _trainCompany.showPassenger(id);
  }

  public Passenger registerPassengerByName(String name) throws NonUniquePassengerNameException {
    return _trainCompany.registerPassenger(name);
  }

  public void changerPassengerName(int id, String name) throws NonUniquePassengerNameException {
    _trainCompany.changePassenger(id, name);
  }

  /**--- Itineraries: ---*/
  public String showAllItineraries() {
    return _trainCompany.showItineraries();
  }

  public String showPassengerItineraries(int id) throws NoSuchPassengerIdException {
    return _trainCompany.showItinerary(id);
  }

  public String searchItineraries(int id, String departureStation, String arrivalStation,
    String date, String time) throws NoSuchPassengerIdException, NoSuchStationNameException,
    BadDateSpecificationException, BadTimeSpecificationException {
    return _trainCompany.searchItineraries(id, departureStation, arrivalStation, date, time);
  }

  public void commitItinerary(int id, int i) throws NoSuchItineraryChoiceException {
    _trainCompany.commitItinerary(id, i);
  }
}
