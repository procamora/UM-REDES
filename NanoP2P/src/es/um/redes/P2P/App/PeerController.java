package es.um.redes.P2P.App;

import java.net.InetSocketAddress;
import java.util.Random;

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
	private Reporter reporter;
	private PeerDatabase peerDatabase;

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
		processCurrentCommand();

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

				break;
			case PeerCommands.COM_QUIT:

				break;
			case PeerCommands.COM_SHOW:
				// NO SABEMOS SI ESTO VA AQUI
				response = reporter.conversationWithTracker(m);
				processMessageFromTracker(response);
				break;

			case PeerCommands.COM_HELP:

				break;
			case PeerCommands.COM_INVALID:

				break;

			default:
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
				control = Message.makeAddSeedRequest(seederPort, lista);
				break;

			case PeerCommands.COM_QUERY:
				byte filterType = '0';
				String filter = ".sh";
				control = Message.makeQueryFilesRequest(filterType, filter);
				break;

			case PeerCommands.COM_DOWNLOAD:

				break;
			case PeerCommands.COM_QUIT:

				break;
			case PeerCommands.COM_SHOW:
				// NO SABEMOS SI ESTO VA AQUI
				control = Message.makeGetSeedsRequest("2DD14050D518B212AECCACEC8DB9B871B1D6A3A3");

				break;
			case PeerCommands.COM_HELP:
				// NO SABEMOS SI ESTO VA AQUI
				// ESTO NO ES CORRECTO, solo hay que poner un fichero, no el
				// path recursivo
				FileInfo[] lista1 = peerDatabase.getLocalSharedFiles();
				control = Message.makeRemoveSeedRequest(seederPort, lista1);

				break;
			case PeerCommands.COM_INVALID:

				break;

			default:
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
				System.out.println("correcto getchunk");
				byte codigo = response.getOpCode();
				System.out.println("codigo: " + codigo);

				short t = ((MessageConf) response).getChunkSize();
				System.out.println("tamaño chunks: " + t);
				break;

			case Message.OP_ADD_SEED_ACK:
				// si solo enviamos un paquete
				System.out.println("correcto addsed");
				// si enviamos muchos contrar el numero de paquetes que enviamos
				// y contar el numero de ack
				break;

			case Message.OP_FILE_LIST:
				System.out.println("correcto queryfile");
				break;

			case Message.OP_SEED_LIST:
				System.out.println("correcto GETSEED");
				break;

			case Message.OP_REMOVE_SEED_ACK:
				System.out.println("correcto OP_REMOVE_SEED_ACK");
				//comprobar que has sido dado de baja
				//opcion 1, mandar un get_seed y comprobar que no estas

				break;

			case Message.INVALID_OPCODE:
				processCurrentCommand(); // suponemos
				break;

			default:
				System.out.println("MAL!!");
				break;
		}

	}

	@Override
	public void recordQueryResult(FileInfo[] fileList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void printQueryResult() {
		// TODO Auto-generated method stub

	}

	@Override
	public FileInfo[] lookupQueryResult(String hashSubstr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void downloadFileFromSeeds(InetSocketAddress[] seedList, String targetFileHash) {
		// TODO Auto-generated method stub

	}

}