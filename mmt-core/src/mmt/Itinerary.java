package mmt;

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.Duration;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Itinerary implements Serializable, Comparator<Itinerary>, Comparable<Itinerary> {
	private LocalDate _data;
	private ArrayList<Segment> _segments = new ArrayList<Segment>();

	public Itinerary(LocalDate date) {
		_data = date;
	}

	public Itinerary() { }

	/** Após terminar procura itinerários possíveis verifica indivualmente se contém
	algum serviço que é repetido. */
	public boolean temServicosRepetidos() {
		for (int i = 0; i < _segments.size(); i++) {
			for (int j = i+1; j < _segments.size(); j++) {
				if (_segments.get(i).getService().equals(_segments.get(j).getService()))
					return true;
			}
		}
		return false;
	}

	public LocalDate getData() {
		return _data;
	}

	/** Os segmentos poderão ser gradualmente adicionados */
	public void addSegment(Segment segment) {
		_segments.add(segment);
	}

	/** Obtém o primeiro segmento da ArrayList */
	public Segment obtainFirstSegment() {
		return _segments.get(0);
	}

	/** Bem como o último */
	public Segment obtainLastSegment() {
		return _segments.get(_segments.size() - 1);
	}

	/** De modo a ajustar qualquer segmento que tenha continuado a iteração num itinerário
	que mais tarde foi descartado, a função coincide a estação de chegada de um segmento
	com o segmento seguinte. */
	public void setSegments(Passenger passenger) {
		for (int i = 0; i < (_segments.size()-1); i++) {
			TrainStop departure = _segments.get(i+1).getDeparture();
			Segment arrival = _segments.get(i);
			if (!arrival.getArrival().equals(departure)) {
				arrival.setArrival(departure);
				arrival.updateCost(passenger);		// Atualiza o valor do custo do segmento
			}

		}
	}

	/** Determina o tempo total do itinerário que tem inicio à hora de partida na estacão de
	origem e fim à hora de chegada na estação de destino */
	public long calculateTravelTime() {
		return Duration.between(_segments.get(0).getDeparture().getTime(),
			obtainLastSegment().getArrival().getTime()).toMinutes();
	}

	/** Tenta obter um novo itinerário com segmentos com os mesmos valores */
	public void copy(Itinerary itinerary) {
		for (Segment segment : _segments) {
			Segment s = new Segment(segment.getService(), segment.getDeparture(), segment.getArrival(), segment.getCost());
			itinerary.addSegment(s);
		}
	}

	/** Determina o custo total do segmento atual em iteração na TrainCompany (enquanto
	determina os possíveis itinerários) */
	public void updateLastSegment(Passenger passenger) {
		obtainLastSegment().updateCost(passenger);
	}

	/** Ordenação pelo data do itinerário */
	public int compare(Itinerary i1, Itinerary i2) {
		return (int)
			Duration.between(i2.getData().atStartOfDay(), i1.getData().atStartOfDay()).toDays();
	}

	/** Ordem crescente e hierarquica da hora de partida, da de chegada e o custo */
	public int compareTo(Itinerary other) {
		int time = obtainFirstSegment().getDeparture().getTime().compareTo(other.obtainFirstSegment().getDeparture().getTime());
		if (time != 0)		// Caso o tesmpo seja igual verifica para a hora de chegada
			return time;
		time = obtainLastSegment().getDeparture().getTime().compareTo(other.obtainLastSegment().getDeparture().getTime());
		if (time != 0)
			return time;
		return (calculateCost() - other.calculateCost() < 0) ? -1 : 1;
	}

	public double calculateCost() {
		double cost = 0;
		for (Segment segment : _segments)
			cost += segment.getCost();
		return cost;
	}

	public String toString() {
		String s = "";
		for (Segment segment : _segments)
			s += segment;
		return  s;
	}
}