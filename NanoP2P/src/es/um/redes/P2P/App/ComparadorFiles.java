package es.um.redes.P2P.App;

import java.util.Comparator;

public class ComparadorFiles implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		return o1.compareTo(o2);
	}

}
