package es.um.redes.P2P.App;

import java.util.Scanner;

public class PeerShell implements PeerShellIface {

	public static final int MAX_ARGS = 2;
	private String comando;
	private String[] argumentos;
	private Scanner scanner;

	public PeerShell() {
		scanner = new Scanner(System.in);
		argumentos = new String[MAX_ARGS];// max 2 argumentos
		comando = new String();
	}

	@Override
	public byte getCommand() {
		return PeerCommands.stringToCommand(comando);
	}

	@Override
	public String[] getCommandArguments() {
		return argumentos;
	}

	@Override
	public void readCommand() {
		System.out.print("[procamora@p2p]# ");
		String linea = scanner.nextLine();
		String[] parsea = linea.split(" ");
		// reinicio para que no se guarden en posteriores consultas
		argumentos[0] = null;
		argumentos[1] = null;

		for (int i = 0; i < parsea.length; i++) {
			if (i == 0) // comando
				comando = parsea[i];
			// argumentos i-1 para que empiece en 0, ya que el i=0 es el comando
			else
				argumentos[i - 1] = parsea[i];
		}
	}
}
