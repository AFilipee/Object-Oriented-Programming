package mmt;

import java.time.LocalTime;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.io.Serializable;

public class Passenger implements Serializable {
	private int _id;							
	private String _name;
	private Category _category;
	private ArrayList<Itinerary> _itineraries = new ArrayList<Itinerary>();

	public Passenger(int id, String n) {
		_id = id;
		_name = n;
	}

	public int getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String calculateTotalTravelTime() {
		/** LocalTime foi optado por não ser utilizado dado que, por exemplo, o valor máximo
		das horas é de 24 e pretendia-se um número até 99 */
		long time = 0;
		for (Itinerary itinerary : _itineraries)
			time += itinerary.calculateTravelTime();
		int hours = (int) time / 60;
		int minutes = (int) time - hours * 60;
		return String.format("%02d", hours) + ":" + String.format("%02d", minutes);
	}

	public void addItinerary(Itinerary itinerary) {
		_itineraries.add(itinerary);
	}

	public String getItineraries() {
		String i = "";
		if (_itineraries.size() != 0) {
			i += "== Passageiro " + _id + ": " + _name + " ==\n";
			int cont = 1;						// Cont corresponde apenas ao contador da
												// ordem de apresentação dos itinerários
			Collections.sort(_itineraries, new Itinerary()); // Ordena pela data antes de a apresentar
			for (Itinerary itinerary : _itineraries) {
				i += "\nItinerário " + cont++ + " para " + itinerary.getData() +  " @ " +
					new DecimalFormat("#,###0.00").format(itinerary.calculateCost());
				i += "\n" + itinerary;
			}
		}
		return i;
	}

	public double totalCost() {
		double cost = 0;
		for (Itinerary itinerary : _itineraries)
			cost += itinerary.calculateCost();
		return cost;
	}

	public void checkCategory() {
		double total = 0;
		int size = _itineraries.size();
		int i = (size >= 10) ? 10 - size : 0;	// Indice inicial para obter, no máximo,
		for (; i < size; i++)					// o preço das 10 últimas viagens
			total += _itineraries.get(i).calculateCost();
		if (total > 2500)
			_category = new Especial();
		else if (total > 250)
			_category = new Frequente();
		else
			_category = new Normal();
	}

	public String getCategory() {
		/** Substring permite omitir o valor os 4 primeiros caracteres que correspondem
		a 'mmt.' */
		checkCategory();
		return _category.getClass().getName().substring(4).toUpperCase();
	}

	public double discount() {
		checkCategory();
		return _category.discount();
	}

	public String toString() {
		return getId() + "|" + getName() + "|" + getCategory() + "|" + _itineraries.size()
		+ "|" + new DecimalFormat("#,###0.00").format(totalCost()) + "|" +
		calculateTotalTravelTime() + "\n";
	}
}