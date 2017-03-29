package es.um.redes.P2P.pruebas;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class shell {

	public static void main(String[] args) {

		String comando = null;
		String[] argumentos = new String[5];

		String[] parsea = "query -ne 1000".split(" ");
		for (int i = 0; i < parsea.length; i++) {
			if (i == 0) // comando
				comando = parsea[i];
			else { // argumentos

				argumentos[i - 1] = parsea[i];
				// i-1 para que empiece en 0, ya que el i=0 es el comando
			}
		}

		/*
		 * System.out.println(argumentos);
		 * 
		 * System.out.println("inicio comando");
		 * System.out.println("comando: " + comando);
		 * for (int i = 0; i < argumentos.length; i++)
		 * if (argumentos[i] != null)
		 * System.out.printf("argumento %d: %s\n", i, argumentos[i]);
		 * System.out.println("fin comando");
		 */

		for (int i = 0; i < 10; i++) {
			Random r = new Random();
			System.out.print(r.ints(10, (20 + 1)).findFirst().getAsInt() + " ");
		}
		System.out.println();
		for (int i = 0; i < 10; i++) {
			Random r = new Random();
			System.out.print(ThreadLocalRandom.current().nextInt(10, 20 + 1) + " ");
		}
	}

}
