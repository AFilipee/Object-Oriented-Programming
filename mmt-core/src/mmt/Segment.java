package mmt;

import java.io.Serializable;
import java.time.LocalTime;
import java.text.DecimalFormat;

public class Segment implements Serializable {
	private Service _service;
	private TrainStop _departure;
	private TrainStop _arrival;
	private double _cost;

	public Segment(Service service, TrainStop departure, TrainStop arrival, double cost) {
		_service = service;
		_departure = departure;
		_arrival = arrival;
		_cost = cost;
	}

	public Segment(Service service, TrainStop departure, TrainStop arrival) {
		_service = service;
		_departure = departure;
		_arrival = arrival;
	}

	public Segment(Service service, TrainStop departure) {
		_service = service;
		_departure = departure;
	}

	public void updateCost(Passenger passenger) {
		// Service calculará o valor do custo do segmento no respetivo serviço
		_cost = _service.updateCost(_departure, _arrival, passenger);
	}

	public void setArrival(TrainStop arrival) {
		_arrival = arrival;
	}

	public void setCost(double cost) {
		_cost = cost;
	}

	public Service getService() {
		return _service;
	}

	public TrainStop getDeparture() {
		return _departure;
	}

	public TrainStop getArrival() {
		return _arrival;
	}

	public double getCost() {
		return _cost;
	}

	public String toString() {
		return "Serviço #" + _service.getId() + " @ " +
		new DecimalFormat("#,###0.00").format(_cost) +
		"\n" + _service.calculateStations(_departure, _arrival);
	}
}