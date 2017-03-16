package es.um.redes.P2P.App;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.TreeSet;
import java.util.TreeMap;

import es.um.redes.P2P.PeerTracker.Client.Reporter;
import es.um.redes.P2P.PeerTracker.Message.*;
import es.um.redes.P2P.util.FileInfo;
import es.um.redes.P2P.util.PeerDatabase;

public class PeerController implements PeerControllerIface {
	private static final int RANGO_INICIAL = 10000;
	private static final int RANGO_FINAL = 30000;
	/**
	 * The shell associated to this controller.
	 */
	private PeerShellIface shell;

	private byte currentCommand;
	private String[] currentArguments;
	private Reporter reporter;
	private PeerDatabase peerDatabase;
	// private HashSet<FileInfo> listaFicheros;
	/**
	 * Puede que en vez de un TreeSet<FileInfo> sea mejor hacer una clase
	 * auxiliar que contenga lo siguiente:
	 * - FileInfo
	 * - Set de seed ordenado por menor clientes en uso
	 */
	private TreeMap<String, TreeSet<FileInfo>> mapaFicheros;

	private short chunkSize;

	/**
	 * puerto del seed generado aleatoriamente en el rango [10000-30000]
	 */
	private int seederPort;

	public PeerController(Reporter client, PeerDatabase peerDatabase) {
		Random ran = new Random();
		seederPort = ran.nextInt(RANGO_FINAL) + RANGO_INICIAL;

		shell = new PeerShell();
		reporter = client;
		this.peerDatabase = peerDatabase;
		this.mapaFicheros = new TreeMap<String, TreeSet<FileInfo>>(new ComparadorFiles());
	}

	public byte getCurrentCommand() {
		return currentCommand;
	}

	public void setCurrentCommand(byte command) {
		currentCommand = command;
	}

	public void readCommandFromShell() {
		shell.readCommand();
		setCurrentCommand(shell.getCommand());
		setCurrentCommandArguments(shell.getCommandArguments());
	}

	public void publishSharedFilesToTracker() {
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
		// TODO Auto-generated method stub
		currentArguments = args;

	}

	@Override
	public void processCurrentCommand() {
		// TODO Auto-generated method stub

		// esta fuera porque siempre se ejecutara
		Message m = createMessageFromCurrentCommand();
		Message response = null;

		// analisis de casos, PODEMOS BORARLO?
		switch (currentCommand) {
			case PeerCommands.COM_CONFIG:
				response = reporter.conversationWithTracker(m);
				processMessageFromTracker(response);
				break;

			case PeerCommands.COM_ADDSEED:
				response = reporter.conversationWithTracker(m);
				processMessageFromTracker(response);
				break;

			case PeerCommands.COM_QUERY:
				response = reporter.conversationWithTracker(m);
				processMessageFromTracker(response);
				break;

			case PeerCommands.COM_DOWNLOAD:
				System.out.println("descarga (en proceso)");
				break;

			case PeerCommands.COM_QUIT:
				System.out.println("procesando salida...");
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

			case PeerCommands.COM_INVALID:
				System.err.println("COM_INVALID");
				break;

			default:
				System.err.println("comando desconocido");
				break;
		}

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
				System.out.println("random port: " + seederPort);
				FileInfo[] lista = peerDatabase.getLocalSharedFiles();
				control = (MessageFileInfo) Message.makeAddSeedRequest(seederPort, lista);

				recordQueryResult(lista); // guardamos nuestros ficheros
				break;

			case PeerCommands.COM_QUERY:
				byte filterType = MessageQuery.FILTERTYPE_ALL;
				String filter = "08_xv6_lazy_page_allocation.pdf";
				control = (MessageQuery) Message.makeQueryFilesRequest(filterType, filter);
				break;

			case PeerCommands.COM_DOWNLOAD:
				System.out.println("por implementar");

				break;

			case PeerCommands.COM_QUIT:
				// mandamos una lista vacia para darnos de baja como seed
				// DA UN FALLO NullPointerException, NO SE COMO MANDAR LISTA
				// VACIA
				FileInfo[] listaVacia = new FileInfo[5];
				control = (MessageFileInfo) Message.makeRemoveSeedRequest(seederPort, listaVacia);
				break;

			default:
				// si el comando actual no requiere uso de mensaje
				// no hacemos nada y return null
				break;
		}

		return control;
	}

	@Override
	public void processMessageFromTracker(Message response) {
		// TODO Auto-generated method stub

		// analisis de casos
		switch (response.getOpCode()) {
			case Message.OP_SEND_CONF:
				chunkSize = ((MessageConf) response).getChunkSize();
				System.out.println("tamaño chunks: " + chunkSize);
				break;

			case Message.OP_ADD_SEED_ACK:
				// si solo enviamos un paquete
				System.out.println("correcto OP_ADD_SEED_ACK");
				// si enviamos muchos contrar el numero de paquetes que enviamos
				// y contar el numero de ack
				break;

			case Message.OP_FILE_LIST:
				System.out.println("correcto OP_FILE_LIST");
				FileInfo[] filelist = ((MessageFileInfo) response).getFileList();
				recordQueryResult(filelist);
				break;

			case Message.OP_SEED_LIST:
				System.out.println("correcto OP_SEED_LIST");
				break;

			case Message.OP_REMOVE_SEED_ACK:
				System.out.println("correcto OP_REMOVE_SEED_ACK");
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

	/**
	 * Guarde la lista de archivos enviados por el rastreador en respuesta a una
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
		// TODO Auto-generated method stub

		for (int i = 0; i < fileList.length; i++) {
			if (!mapaFicheros.containsKey(fileList[i].fileHash)) {
				TreeSet<FileInfo> conjuntoFileInfo = new TreeSet<>(new ComparadorFiles2());
				conjuntoFileInfo.add(fileList[i]);
				mapaFicheros.put(fileList[i].fileHash, conjuntoFileInfo);
			} else {
				TreeSet<FileInfo> conjuntoFileInfo = mapaFicheros.get(fileList[i].fileHash);
				conjuntoFileInfo.add(fileList[i]); // modificado aliasing
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
		// TODO Auto-generated method stub
		for (String hashes : mapaFicheros.keySet()) {
			System.out.println(mapaFicheros.get(hashes));

		}
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
		// TODO Auto-generated method stub
		FileInfo[] listaFicherosValidos = null;
		int contador = 0;
		for (String hashes : mapaFicheros.keySet()) {
			if (hashes.contains(hashSubstr)) {
				TreeSet<FileInfo> conjuntoFileInfo = mapaFicheros.get(hashes);
				listaFicherosValidos[contador] = conjuntoFileInfo.first();
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
		// TODO Auto-generated method stub

	}

}