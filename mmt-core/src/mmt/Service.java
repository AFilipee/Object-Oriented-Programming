package mmt;

import java.time.LocalTime;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Comparator;
import java.time.LocalTime;
import java.time.Duration;
import java.io.Serializable;

public class Service implements Serializable, Comparator<Service>, Comparable<Service> {
	private int _id;
	private double _cost;
	private LinkedList<TrainStop> _trainStops = new LinkedList<TrainStop>();

	public Service(int id, double cost) {
		_id = id;
		_cost = cost;
	}

	public Service() { }

	public void addTrainStop(TrainStop ts) {
		_trainStops.add(ts);
	}

	public void setId(int id) {
		_id = id;
	}

	public int getId() {
		return _id;
	}

	public double getCost() {
		return _cost;
	}

	public TrainStop getFirstTrainStop() {
		return _trainStops.getFirst();
	}
	public TrainStop getLastTrainStop() {
		return _trainStops.getLast();
	}

	public void createSegment(Passenger passenger, Itinerary itinerary, String departure, String arrival) {
		/** A função obtém a duração de toda o serviço, a duração do itinerário no serviço,
		e assim calcular o custo corresponde ao itinerário neste serviço. Poder-se-ia utilizar
		o método seguinte, calculaCost(), porém dá-se proveito ao iterador que percorre
		as TrainStop's e verifica quais são a estação de ínicio e fim do segmento */
		ListIterator itr = _trainStops.listIterator();
		LocalTime inicio = ((TrainStop) itr.next()).getTime();
		itr.previous();

		while (!((TrainStop) itr.next()).getStation().getName().equals(departure)) ;

		itr.previous();
		Segment segment = new Segment(this, (TrainStop) itr.next());
		itr.previous();
		LocalTime partida = ((TrainStop) itr.next()).getTime();

		while (!((TrainStop)itr.next()).getStation().getName().equals(arrival)) ;

		itr.previous();
		segment.setArrival((TrainStop) itr.next());
		itr.previous();
		double cost = _cost * Duration.between(partida, ((TrainStop) itr.next()).getTime()).toMinutes();

		while (itr.hasNext()) itr.next();

		itr.previous();
		segment.setCost(passenger.discount() * cost / Duration.between(inicio, ((TrainStop) itr.next()).getTime()).toMinutes());
		itinerary.addSegment(segment);
	}

	public double calculateCost() {
		return _cost / Duration.between(_trainStops.getFirst().getTime(),
			_trainStops.getLast().getTime()).toMinutes();
	}

	public String calculateStations(TrainStop departure, TrainStop arrival) {
		/** Método que devolve uma string para apresentar as estações
		intermediárias de cada segmento */
		int itr = -1;
		while (!_trainStops.get(++itr).equals(departure)) ;

		String s = _trainStops.get(itr++).toString();

		while (!_trainStops.get(itr).equals(arrival))
			s += _trainStops.get(itr++);

		return s + _trainStops.get(itr);
	}

	public TrainStop pesquisaPartida(TrainStop trainStop) {
		/** Ontem a trainStop, caso exista, que passa pela indicada como argumento,
		a horas posteriores. */
		for (TrainStop ts : _trainStops) {
			if (ts.equals(trainStop) && ts.getTime().isAfter(trainStop.getTime()))
				return ts;
		}
		return null;
	}

	public TrainStop pesquisaPartida(Station partida, LocalTime horas) {
		/** Com o mesmo objetivo, no ínico da procura por itinerários possíveis */
		for (TrainStop trainStop : _trainStops) {
			if (trainStop.getStation().equals(partida) && trainStop.getTime().isAfter(horas))
				return trainStop;
		}
		return null;
	}

	public Segment pesquisaChegada(Passenger passenger, TrainStop partida, Station chegada) {
		/** Verifica, quando altera de serviço, se este possui a estação de chegada procurada.
		Em caso positivo, constrói e devolve o segmento correspondente a esse percurso. */
		for (TrainStop ts : _trainStops) {
			if (ts.getStation().equals(chegada) && partida.getTime().isBefore(ts.getTime()))
				return new Segment(this, partida, ts, passenger.discount() * calculateCost() *
					Duration.between(partida.getTime(), ts.getTime()).toMinutes());
		}
		return null;
	}

	public TrainStop proximaParagem(TrainStop paragem) {
		// Determina a seguinte trainStop caso esta não seja a última
		int index = _trainStops.indexOf(paragem) + 1;
		return (index < _trainStops.size()) ? _trainStops.get(index) : null;
	}

	public double updateCost(TrainStop departure, TrainStop arrival, Passenger passenger) {
		ListIterator itr = _trainStops.listIterator();
		LocalTime inicio = ((TrainStop) itr.next()).getTime();
		itr.previous();

		while (!((TrainStop) itr.next()).equals(departure)) ;

		itr.previous();
		LocalTime partida = ((TrainStop) itr.next()).getTime();

		while (!((TrainStop)itr.next()).equals(arrival)) ;

		itr.previous();
		double cost = _cost * Duration.between(partida, ((TrainStop) itr.next()).getTime()).toMinutes();

		while (itr.hasNext()) itr.next();

		itr.previous();
		return passenger.discount() * cost / Duration.between(inicio, ((TrainStop) itr.next()).getTime()).toMinutes();
	}

	public boolean equals(Service service) {
		return _id == service.getId();
	}

	/** Método que servirá para ordenar os serviços com inicio numa estação dada */
	public int compareTo(Service other) {
		return _trainStops.getFirst().getTime().compareTo(other.getFirstTrainStop().getTime());
	}

	/** Do mesmo modo obtem para serviços com término numa estação dada */
	public int compare(Service s1, Service s2) {
		return (int) Duration.between(s2.getLastTrainStop().getTime(),
			s1.getLastTrainStop().getTime()).toMinutes();
	}

	public String toString() {
		String s;
		s = "Serviço #" + getId() + " @ " + new DecimalFormat("#,###0.00").format(getCost()) + "\n";
		for (TrainStop trainStop : _trainStops)
			s += trainStop;
		return s;
	}
}