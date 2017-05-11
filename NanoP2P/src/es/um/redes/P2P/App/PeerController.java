package es.um.redes.P2P.App;

import java.net.InetSocketAddress;
import java.util.TreeMap;

import es.um.redes.P2P.PeerPeer.Client.Downloader;
import es.um.redes.P2P.PeerPeer.Server.Seeder;
import es.um.redes.P2P.PeerTracker.Client.Reporter;
import es.um.redes.P2P.PeerTracker.Message.*;
import es.um.redes.P2P.util.FileInfo;
import es.um.redes.P2P.util.PeerDatabase;

public class PeerController implements PeerControllerIface {
	/**
	 * The shell associated to this controller.
	 */
	private PeerShellIface shell;
	private byte currentCommand;
	private String[] currentArguments;
	private Reporter reporter;
	private Seeder seeder;
	private PeerDatabase peerDatabase;

	/**
	 * Puede que en vez de un TreeSet<FileInfo> sea mejor hacer una clase
	 * auxiliar que contenga lo siguiente: - FileInfo - Set de seed ordenado por
	 * menor clientes en uso
	 */
	private TreeMap<String, FileInfo> mapaFicheros;

	private short chunkSize;

	public PeerController(Reporter client, PeerDatabase peerDatabase) {
		currentArguments = new String[PeerShell.MAX_ARGS];
		shell = new PeerShell();
		reporter = client;
		this.peerDatabase = peerDatabase;
		this.mapaFicheros = new TreeMap<>();
	}

	public Reporter getReporter() {
		return reporter;
	}

	public byte getCurrentCommand() {
		return currentCommand;
	}

	public Seeder getSeeder() {
		return seeder;
	}

	public TreeMap<String, FileInfo> getMapaFicheros() {
		return mapaFicheros;
	}

	public short getChunkSize() {
		return chunkSize;
	}

	public void setCurrentCommand(byte command) {
		currentCommand = command;
	}

	public void readCommandFromShell() {
		shell.readCommand();
		setCurrentCommand(shell.getCommand());
		setCurrentCommandArguments(shell.getCommandArguments());
	}

	public void publishSharedFilesToTracker(Seeder seeder) {
		this.seeder = seeder;
		seeder.getAvailablePort(); // obtenemos el puerto por el que escuchara
		seeder.start();
		setCurrentCommand(PeerCommands.COM_ADDSEED);
		processCurrentCommand();
	}

	public void removeSharedFilesFromTracker() {
		setCurrentCommand(PeerCommands.COM_QUIT);
		processCurrentCommand();
	}

	public void getConfigFromTracker() {
		setCurrentCommand(PeerCommands.COM_CONFIG);
		processCurrentCommand();
	}

	public boolean shouldQuit() {
		return currentCommand == PeerCommands.COM_QUIT;
	}

	@Override
	public void setCurrentCommandArguments(String[] args) {
		// currentArguments = args;
		for (int i = 0; i < args.length; i++) {
			currentArguments[i] = args[i];
		}
	}

	@Override
	public void processCurrentCommand() {
		// esta fuera porque siempre se ejecutara
		Message m = createMessageFromCurrentCommand();
		Message response = null;

		// analisis de casos, PODEMOS BORARLO?
		switch (currentCommand) {
			case PeerCommands.COM_CONFIG:
			case PeerCommands.COM_ADDSEED:
			case PeerCommands.COM_QUERY:
			case PeerCommands.COM_DOWNLOAD:
			case PeerCommands.COM_QUIT:
				// m es null si no hemos puesto parametros correctos
				if (m != null) {
					response = reporter.conversationWithTracker(m);
					processMessageFromTracker(response);
				}
				break;

			case PeerCommands.COM_SHOW:
				// imprimimos la lista de ficheros que conocemos
				printQueryResult();
				break;

			case PeerCommands.COM_HELP:
				String ayuda = "La lista de comandos disponibles es:\n"
						+ "query [params] (consulta al tracker la lista de ficheros compartidos disponibles)\n"
						+ "\t-n  <substring> : Sirve para filtrar los resultados por subcadena en el nombre.\n"
						+ "\t-lt <bytes>: Sirve para filtrar los resultados a ficheros cuyo tamaño sea inferior"
						+ "a la cantidad expresada en <bytes>. Se pueden utilizar sufijos como KB, MB y GB para"
						+ "indicar, respectivamente, kilobytes, megabytes y gigabytes.\n"
						+ "\t-ge <bytes>: Sirve para obtener ficheros cuyo tamaño sea igual o "
						+ "superior a <bytes>. De nuevo se pueden emplear sufijos.\n"
						+ "download <hash> (descarga el fichero de otros peers)\n"
						+ "show (muestra la lista de )           \n"
						+ "help (muestra la lista de comandos disponibles)\n"
						+ "quit (cierra la conexión con el tracker y termina el programa)";
				System.out.println(ayuda);
				break;

			case PeerCommands.COM_ENTER:
				// no se hace nada
				break;
			case PeerCommands.COM_INVALID:
				System.err.println("COM_INVALID");
				break;

			default:
				System.err.println("comando desconocido");
				break;
		}
	}

	private String[] tratarArgumentosQuery() {
		byte filterType = 0;

		if ((currentArguments[0] == null) || (currentArguments[1] == null)) {
			System.err.println("wrong arguments for query");
			return null;
		}

		switch (currentArguments[0]) {
			case "-n":
				filterType = MessageQuery.FILTERTYPE_NAME;
				break;

			case "-lt":
				filterType = MessageQuery.FILTERTYPE_MAXSIZE;
				break;

			case "-ge":
				filterType = MessageQuery.FILTERTYPE_MINSIZE;
				break;
			default:
				System.err.println("wrong arguments for query");
				break;
		}

		String stringFilterType = Byte.toString(filterType);
		String[] array = { stringFilterType, currentArguments[1] };
		return array;
	}

	@Override
	public Message createMessageFromCurrentCommand() {
		Message control = null;

		// analisis de casos
		switch (currentCommand) {
			case PeerCommands.COM_CONFIG:
				control = (MessageControl) Message.makeGetConfRequest();
				break;

			case PeerCommands.COM_ADDSEED:
				FileInfo[] lista = peerDatabase.getLocalSharedFiles();
				control = (MessageFileInfo) Message.makeAddSeedRequest(seeder.getSeederPort(), lista);

				recordQueryResult(lista); // guardamos nuestros ficheros
				break;

			case PeerCommands.COM_QUERY:
				byte filterType = 0;
				String filter = "";
				String[] array = tratarArgumentosQuery();
				if (array != null) {
					filterType = Byte.valueOf(array[0]);
					filter = array[1];
					control = (MessageQuery) Message.makeQueryFilesRequest(filterType, filter);
				}
				break;

			case PeerCommands.COM_DOWNLOAD:
				if (currentArguments[0] != null) {
					FileInfo[] opcionesHash = lookupQueryResult(currentArguments[0]);

					int contador = 0;
					for (int i = 0; i < opcionesHash.length; i++) {
						if (opcionesHash[i] != null)
							contador++;
					}

					if (contador > 1) {
						System.out.println(
								"El hash introducido no es suficientemente exacto, estos son los ficheros que coinciden:");
						for (int i = 0; i < opcionesHash.length; i++)
							if (opcionesHash[i] != null)
								System.out.println(opcionesHash[i]);
					} else if (contador == 1 && opcionesHash[0] != null)
						control = (MessageSeedInfo) Message.makeGetSeedsRequest(opcionesHash[0].fileHash);
					else
						System.out.println("El hash introducido no coincide con ningun fichero");

				}
				break;

			case PeerCommands.COM_QUIT:
				control = (MessageFileInfo) Message.makeRemoveSeedRequest(seeder.getSeederPort(), new FileInfo[0]);
				seeder.quit();
				break;

			default:
				// si el comando actual no requiere uso de mensaje no hacemos
				// nada y return null
				break;
		}
		return control;
	}

	@Override
	public void processMessageFromTracker(Message response) {
		// analisis de casos
		switch (response.getOpCode()) {
			case Message.OP_SEND_CONF:
				chunkSize = ((MessageConf) response).getChunkSize();
				break;

			case Message.OP_ADD_SEED_ACK:
				// si solo enviamos un paquete y llegamos aqui correcto

				// si enviamos muchos contar el numero de paquetes que enviamos
				// y contar el numero de ack
				break;

			case Message.OP_FILE_LIST:
				FileInfo[] fileList = ((MessageFileInfo) response).getFileList();
				recordQueryResult(fileList);
				break;

			case Message.OP_SEED_LIST:
				InetSocketAddress[] seedList = ((MessageSeedInfo) response).getSeedList();
				String targetFileHash = ((MessageSeedInfo) response).getFileHash();
				downloadFileFromSeeds(seedList, targetFileHash);
				break;

			case Message.OP_REMOVE_SEED_ACK:
				// comprobar que has sido dado de baja
				// opcion 1, mandar un get_seed y comprobar que no estas
				break;

			case Message.INVALID_OPCODE:
				System.err.println("INVALID_OPCODE");
				processCurrentCommand(); // suponemos
				break;

			default:
				System.err.println("MAL!!");
				break;
		}
	}

	private boolean isLocal(String hash) {
		FileInfo[] locales = peerDatabase.getLocalSharedFiles();
		for (int i = 0; i < locales.length; i++) {
			if (locales[i].fileHash.equals(hash))
				return true;
		}
		return false;
	}

	/**
	 * Guarda la lista de archivos enviados por el rastreador en respuesta a una
	 * solicitud de consulta, excluyendo los archivos que ya están compartidos
	 * por este par. El resultado de cada consulta se utiliza para seleccionar
	 * el archivo que se va a descargar.
	 *
	 * @param fileList
	 *            Lista completa de archivos devueltos por el rastreador
	 *
	 */
	@Override
	public void recordQueryResult(FileInfo[] fileList) {
		for (int i = 0; i < fileList.length; i++) {
			if (!isLocal(fileList[i].fileHash)) {
				System.out.println(fileList[i]);
				mapaFicheros.put(fileList[i].fileHash, fileList[i]);
			}
		}
	}

	/**
	 * Imprime la lista de archivos obtenidos de la última consulta (previamente
	 * guardada por recordQueryResult) que están disponibles para su descarga
	 *
	 */
	@Override
	public void printQueryResult() {
		for (String hashes : mapaFicheros.keySet())
			System.out.println(mapaFicheros.get(hashes));
	}

	/**
	 * Busca la lista de archivos grabados desde la última consulta, buscando
	 * archivos cuyo hash contenga la subcadena dada. Se utiliza para buscar en
	 * el archivo para descargar entre los archivos devueltos por la última
	 * consulta.
	 *
	 * @param hashSubstr
	 *            String dada por el usuario al comando 'download'.
	 * @return Una lista de los archivos cuyo hash contiene la subcadena.
	 */
	@Override
	public FileInfo[] lookupQueryResult(String hashSubstr) {
		FileInfo[] listaFicherosValidos = new FileInfo[mapaFicheros.keySet().size()];
		int contador = 0;
		for (String hashes : mapaFicheros.keySet()) {
			if (hashes.contains(hashSubstr.toUpperCase())) {
				listaFicherosValidos[contador] = mapaFicheros.get(hashes);
				contador++;
			}
		}
		return listaFicherosValidos;
	}

	/**
	 * Descargue el archivo de la lista de semillas proporcionada, creando un
	 * objeto descargador para este archivo de destino identificado por su hash.
	 *
	 *
	 * @param inetSocketAddresses
	 *            La lista de pares que comparten actualmente el archivo.
	 *
	 * @param targetFileHash
	 *            El archivo de destino para descargar
	 *
	 */
	@Override
	public void downloadFileFromSeeds(InetSocketAddress[] seedList, String targetFileHash) {
		Downloader descarga = new Downloader(chunkSize, mapaFicheros.get(targetFileHash), this);
		seeder.setCurrentDownloader(descarga);
		descarga.downloadFile(seedList);
	}

}