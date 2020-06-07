package mmt;

import java.time.LocalTime;
import java.io.Serializable;

public class TrainStop implements Serializable {
	private LocalTime _timeTrainStop;
	private Station _station;

	public void setStation(Station n) {
		_station = n;
	}

	public Station getStation() {
		return _station;
	}

	public void setTime(LocalTime t) {
		_timeTrainStop = t;
	}

	public LocalTime getTime() {
		return _timeTrainStop;
	}

	public boolean equals(TrainStop trainStop) {
		return _station.equals(trainStop.getStation());
		// Não precisa de verificas as horas dado que é verificado para trainStops do mesmo serviço
	}

	public String toString() {
		return getTime() + " " + _station + "\n";
	}
}