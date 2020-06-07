package mmt;

import mmt.exceptions.BadDateSpecificationException;
import mmt.exceptions.BadEntryException;
import mmt.exceptions.BadTimeSpecificationException;
import mmt.exceptions.InvalidPassengerNameException;
import mmt.exceptions.NoSuchDepartureException;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.exceptions.NoSuchServiceIdException;
import mmt.exceptions.NoSuchStationNameException;
import mmt.exceptions.NoSuchItineraryChoiceException;
import mmt.exceptions.NonUniquePassengerNameException;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.time.LocalTime;
import java.time.LocalDate;
import java.text.DecimalFormat;

/**
 * A train company has schedules (services) for its trains and passengers that
 * acquire itineraries based on those schedules.
 */
public class TrainCompany implements Serializable {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 201708301010L;

  private TreeMap<Integer, Service> _services = new TreeMap<Integer, Service>();
  private ArrayList<Station> _stations = new ArrayList<Station>();
  private ArrayList<Passenger> _passengers = new ArrayList<Passenger>();
  private LinkedList<Itinerary> _itineraries = new LinkedList<Itinerary>();

  /** Id counter. */
  private int _idNumber = 0;

  /** Variável que indica se houve alterações desde a última vez que o estado atual da aplicação
  foi guardado. Tem o valor "True" caso tenha havido alguma modificação. */
  private boolean _checkSave;

  public TrainCompany() {
    _checkSave = true;
  }

  public boolean houveAlteracoes() {
    return _checkSave;
  }

  public void alteracoesGuardadas() {
    _checkSave = false;
  }

  public void resetClear() {
    _passengers.clear();
    _idNumber = 0;
  }

  /**--- Serviços: ---*/
  public void addService(String[] service) {
    int numberService = Integer.parseInt(service[1]);

    Service s = new Service(numberService, Float.parseFloat(service[2]));

    for (int i = 3; i < service.length; i += 2) {
      TrainStop trainStop = new TrainStop(); 
      trainStop.setTime(LocalTime.parse(service[i]));

      boolean existeEstacao = false;

      for (Station station : _stations) {
        if (station.getName().equals(service[i+1])) {
          trainStop.setStation(station);
          s.addTrainStop(trainStop);
          existeEstacao = true;
          break;
        }
      }
      if (existeEstacao) continue;

      Station station = new Station(service[i+1]);
      _stations.add(station);
      trainStop.setStation(station);
      s.addTrainStop(trainStop);
    }
    _services.put(numberService, s);
  }

  public String showServices() {
    String s = "";
    for (Service service : _services.values())
      s += service;
    return s;
  }

  public String showService(int id) throws NoSuchServiceIdException {
    if (_services.containsKey(id))
      return _services.get(id).toString();

    throw new NoSuchServiceIdException(id);
  }

  public String showServicesByDeparture(String station) throws NoSuchStationNameException {
    return showServicesByStation(station, true);
  }

  public String showServicesByArrival(String station) throws NoSuchStationNameException {
    return showServicesByStation(station, false);
  }

  public String showServicesByStation(String station, boolean v) throws NoSuchStationNameException {
    String s = "";
    LinkedList<Service> services = new LinkedList<Service>();

    if (v) {
      for (Service service : _services.values()) {
        if (service.getFirstTrainStop().getStation().getName().equals(station))
          services.add(service);
      }
    }
    else {
      for (Service service : _services.values()) {
        if (service.getLastTrainStop().getStation().getName().equals(station))
          services.add(service);
      }
    }
    if (services.size() == 0) {
      for (Station st : _stations) {
        if (st.getName().equals(station))
          return s;
      }
      throw new NoSuchStationNameException(station);
    }
    if (v)  Collections.sort(services);
    else    Collections.sort(services, new Service());
    for (Service service : services)
      s += service;
    return s;
  }

  /**--- Passageiros: ---*/
  public String showPassengers() {
    String p = "";
    for (Passenger passenger : _passengers)
      p += passenger;
    return p;
  }

  public String showPassenger(int id) throws NoSuchPassengerIdException {
    if (_idNumber > id && id >= 0)
      return _passengers.get(id).toString();

    throw new NoSuchPassengerIdException(id);
  }

  public Passenger registerPassenger(String name) throws NonUniquePassengerNameException {
    for (Passenger p : _passengers)
      if (p.getName().equals(name))
        throw new NonUniquePassengerNameException(name);

    Passenger passenger = new Passenger(_idNumber++, name);
    _passengers.add(passenger);

    _checkSave = true;

    return passenger;
  }

  public void changePassenger(int id, String name) throws NonUniquePassengerNameException {
    for (Passenger p : _passengers) {
      if ((p.getName()).equals(name))
        throw new NonUniquePassengerNameException(name);
    }
    _passengers.get(id).setName(name);

    _checkSave = true;
  }

  /**--- Itineraries: ---*/
  public void addItinerary(String[] itinerary) {

    Passenger p = _passengers.get(Integer.parseInt(itinerary[1]));
    Itinerary i = new Itinerary(LocalDate.parse(itinerary[2]));

    for (int ind = 3; ind < itinerary.length; ind++) {
      String[] service = itinerary[ind].split("\\/");
      _services.get(Integer.parseInt(service[0])).createSegment(p, i, service[1], service[2]);
    }
    p.addItinerary(i);
  }

  public String showItineraries() {
    String i = "";
    for (Passenger passenger : _passengers)
      i += passenger.getItineraries();
    return i;
  }

  public String showItinerary(int id) throws NoSuchPassengerIdException {
    if (_idNumber <= id || id < 0)
      throw new NoSuchPassengerIdException(id);

    return _passengers.get(id).getItineraries();
  }

  public String searchItineraries(int id, String departureStation, String arrivalStation,
    String date, String time) throws NoSuchPassengerIdException, NoSuchStationNameException,
    BadDateSpecificationException, BadTimeSpecificationException {

    // Verifica se é um número válido para o passageiro (não poderá ter indice igual ou superior
    // ao atual nem ser negativo)
    if (_idNumber <= id || id < 0)
      throw new NoSuchPassengerIdException(id);
    Passenger passenger = _passengers.get(id);

    // A string contendo a data deverá ter tamanho 10 (8 números e 2 símbolos '-')
    if (date.length() != 10)
      throw new BadDateSpecificationException(date);
    for (int n = 0; n < 10; n++) {
      if (n==4 || n==7) {               // Os índ. 4 e 7 deverão corresponder ao '-'
        if (date.charAt(n) != '-')      // charAt() retorna um tipo primitivo
          throw new BadDateSpecificationException(date);
      }
      else {
        if (!Character.isDigit(date.charAt(n)))
          throw new BadDateSpecificationException(date);
      }
    }                                    // A mesma lógica é aplicada para a string 'tempo'
    if (time.length() != 5)
      throw new BadTimeSpecificationException(time);
    for (int n = 0; n < 5; n++) {
      if (!Character.isDigit(time.charAt(n))) {
        if (!(n == 2 && time.charAt(n) == ':'))
          throw new BadTimeSpecificationException(time);
      }
    }
    /* Obtém-se as estações de comboio cujo nome correspondem respetivamente à estação de
    partida e à de chegada.
    Procura-se percorrer todas as estações até encontrar as estações. Caso não existam,
    devolverá erro posteriormente. */
    Station partida = null, chegada = null;
    boolean departure = false, arrival = false;
    for (Station station : _stations) {
      if (!departure && station.getName().equals(departureStation)) {
        partida = station;
        departure = true;
        if (arrival) break;   // Evita continuar pesquisa caso o outro já tenha sido encontrado
      }
      if (!arrival && station.getName().equals(arrivalStation)) {
        chegada = station;
        arrival = true;
        if (departure) break;
      }
    }
    if (!departure) throw new NoSuchStationNameException(departureStation);
    if (!arrival)   throw new NoSuchStationNameException(arrivalStation);

    for (Service service : _services.values()) {
      // Para cada serviço verifica se têm Train Stop que passa pela estação 'partida'
      // e a horas posteriores
      TrainStop estPartida = service.pesquisaPartida(partida, LocalTime.parse(time));
      if (estPartida == null) continue;
      // Se não tem avança para a seguinte iteração

      // Cria um itinerário a verificar
      LocalDate data = LocalDate.parse(date);
      Itinerary itinerary = new Itinerary(data);
      _itineraries.add(itinerary);

      Segment segment = service.pesquisaChegada(passenger, estPartida, chegada);
      if (segment != null) {
        itinerary.addSegment(segment);  // Encontrou um itinerário simples
        continue;                       // Verifica por mais trainStops
      }
      TrainStop proxima = service.proximaParagem(estPartida);
      if (proxima == null) {
        _itineraries.remove(itinerary);
        continue;
      }
      segment = new Segment(service, estPartida);
      itinerary.addSegment(segment);
      // A função recursiva avança uma estação a frente pelos percursos possíveis
      // validando se é um itinerário possível
      pesquisaItinerariosPossiveis(proxima, chegada, data, service, passenger, itinerary);
    }
    for (Itinerary itinerary : _itineraries) {
      if(itinerary.temServicosRepetidos())
        _itineraries.remove(itinerary);
    }

    String i = "";
    if (_itineraries.size() != 0) {
      int cont = 1;                   // Apresenta por ordem os itinerários encontrados
      Collections.sort(_itineraries);
      for (Itinerary itinerary : _itineraries) {
        itinerary.setSegments(passenger);
        i += "\nItinerário " + cont++ + " para " + itinerary.getData() +  " @ " +
          new DecimalFormat("#,###0.00").format(itinerary.calculateCost());
        i += "\n" + itinerary;
      }
    }
    return i;
  }

  protected void pesquisaItinerariosPossiveis(TrainStop partida, Station chegada,
    LocalDate data, Service service, Passenger passenger, Itinerary i1) {
    for (Service s : _services.values()) {          // Percorre cada serviço
      if (!s.equals(service)) {                     // Caso não seja o anterior
        TrainStop estPartida = s.pesquisaPartida(partida); 
        if (estPartida == null) continue;           // E passa pela trainStop mais tarde
        Itinerary i2 = new Itinerary(data);         // Então obtem um novo possível itinerário
        _itineraries.add(i2);
        i1.copy(i2);                                // Onde os segmentos do novo segmento são os anteriores já calculadas
        i2.updateLastSegment(passenger);            // Determina o valor do segmento anterior

        Segment segment = s.pesquisaChegada(passenger, estPartida, chegada);
        if (segment != null) {
          i2.addSegment(segment);                   // Caso o novo serviço tenha a estação de chegada
          break;                                    // procura, adiociona esse segmento para e termina a iteração
        }
        else {
          TrainStop prox = s.proximaParagem(estPartida);
          if (prox == null)                          // Caso contrário obtem a seguinte TrainStop
            _itineraries.remove(i2);
          else {
            segment = new Segment(s, estPartida);
            pesquisaItinerariosPossiveis(prox, chegada, data, s, passenger, i2);  
          }
        }
      }
      else {                                        // Caso se mantenha no mesmo serviço , determina a próxima paragem ()
        TrainStop prox = service.proximaParagem(partida);
        if (prox == null)                           // Remove o itinerário caso tenha chegado ao fim
          _itineraries.remove(i1);
        else {
          i1.obtainLastSegment().setArrival(partida); // Atualiza valor da TrainStop _arrival
          pesquisaItinerariosPossiveis(prox, chegada, data, service, passenger, i1);
        }
      }
    }
  }

  public void commitItinerary(int id, int i) throws NoSuchItineraryChoiceException {
    if (i == 0) {
      _itineraries.clear();
      return;
    }
    if(i < 0 || _itineraries.size() < i) {
      _itineraries.clear();
      throw new NoSuchItineraryChoiceException(id, i);
    }
    Itinerary itinerary = _itineraries.get(i-1);
    Passenger passenger = _passengers.get(id);
    itinerary.setSegments(passenger);
    passenger.addItinerary(itinerary);
    _itineraries.clear();
  }
}