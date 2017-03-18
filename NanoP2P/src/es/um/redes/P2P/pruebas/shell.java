package es.um.redes.P2P.pruebas;

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

		/*System.out.println(argumentos);

		System.out.println("inicio comando");
		System.out.println("comando: " + comando);
		for (int i = 0; i < argumentos.length; i++)
			if (argumentos[i] != null)
				System.out.printf("argumento %d: %s\n", i, argumentos[i]);
		System.out.println("fin comando");*/
		
	

		System.out.println(argumentos[0]);
		switch (argumentos[0]) {
			case ("-ne"):
				System.out.println("ne");
				break;

			default:
				break;
		}
	}

}
