package mmt;

import java.time.LocalTime;
import java.util.ArrayList;
import java.io.Serializable;

public class Station implements Serializable {
	private String _name;

	public Station(String name) {
		_name = name;
	}

	public String getName() {
		return _name;
	}

	public boolean equals(Station station) {
		return _name.equals(station.getName());
	}

	public String toString() {
		return getName();
	}
}