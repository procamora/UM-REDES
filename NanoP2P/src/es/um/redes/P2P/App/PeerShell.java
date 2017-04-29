package es.um.redes.P2P.App;

import java.util.Scanner;

public class PeerShell implements PeerShellIface {

	public static final int MAX_ARGS = 5;
	private String comando;
	private String[] argumentos;
	private Scanner scanner;

	public PeerShell() {
		scanner = new Scanner(System.in);
		argumentos = new String[MAX_ARGS];// max 5 argumentos
		comando = new String();
	}

	@Override
	public byte getCommand() {
		switch (comando) {
			case "query":
				return PeerCommands.COM_QUERY;

			case "download":
				return PeerCommands.COM_DOWNLOAD;

			case "quit":
				return PeerCommands.COM_QUIT;

			case "show":
				return PeerCommands.COM_SHOW;

			case "help":
				return PeerCommands.COM_HELP;

			case "":
				return PeerCommands.COM_ENTER;

			default:
				return PeerCommands.COM_INVALID;
		}
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

		for (int i = 0; i < parsea.length; i++) {
			if (i == 0) // comando
				comando = parsea[i];
			// argumentos i-1 para que empiece en 0, ya que el i=0 es el comando
			else
				argumentos[i - 1] = parsea[i];
		}
	}
}
