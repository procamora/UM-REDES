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
		// TODO Auto-generated method stub
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

			default:
				return PeerCommands.COM_INVALID;
		}
	}

	@Override
	public String[] getCommandArguments() {
		// TODO Auto-generated method stub
		return argumentos;
	}

	@Override
	public void readCommand() {
		// TODO Auto-generated method stub
		System.out.print("[procamora@p2p]# ");
		String linea = scanner.nextLine();
		String[] parsea = linea.split(" ");

		if (parsea.length == 1 && parsea[0].equalsIgnoreCase(""))
			return;
		
		//FIXME ENTER NO ES COMAND INVALID

		for (int i = 0; i < parsea.length; i++) {
			if (i == 0) // comando
				comando = parsea[i];
			else { // argumentos
					// i-1 para que empiece en 0, ya que el i=0 es el comando
				argumentos[i - 1] = parsea[i];
			}
		}

		// imprimeComando();
	}

	private void imprimeComando() {
		System.out.println("inicio comando");
		System.out.println("comando: " + comando);
		for (int i = 0; i < argumentos.length; i++)
			if (argumentos[i] != null)
				System.out.printf("argumento %d: %s\n", i, argumentos[i]);
		System.out.println("fin comando");
	}
}
