package es.um.redes.P2P.App;

import java.net.InetSocketAddress;

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
	private Reporter reporter;
	private PeerDatabase peerDatabase;

	public PeerController(Reporter client, PeerDatabase peerDatabase) {
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

		// analisis de casos
		switch (currentCommand) {
			case PeerCommands.COM_CONFIG:
				Message respuesta = reporter.conversationWithTracker(m);
				processMessageFromTracker(respuesta);
				break;
			case PeerCommands.COM_ADDSEED:
				Message response =  reporter.conversationWithTracker(m);
//				if (response.getOpCode() == Message.OP_ADD_SEED_ACK) 
//					return response;
				break;
			case PeerCommands.COM_QUERY:

				break;
			case PeerCommands.COM_DOWNLOAD:

				break;
			case PeerCommands.COM_QUIT:

				break;
			case PeerCommands.COM_SHOW:

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
				FileInfo[] lista = peerDatabase.getLocalSharedFiles();
				control = Message.makeAddSeedRequest(6325, lista);		// El numero de puerto es inventado


				break;
			case PeerCommands.COM_QUERY:

				break;
			case PeerCommands.COM_DOWNLOAD:

				break;
			case PeerCommands.COM_QUIT:

				break;
			case PeerCommands.COM_SHOW:

				break;
			case PeerCommands.COM_HELP:

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
		switch (currentCommand) {
			case PeerCommands.COM_CONFIG:
				byte codigo = response.getOpCode();
				System.out.println("codigo: " + codigo);

				short t = ((MessageConf) response).getChunkSize();
				System.out.println("tamaño chunks: " + t);

				break;
			case PeerCommands.COM_ADDSEED:
				
				
				break;
			case PeerCommands.COM_QUERY:

				break;
			case PeerCommands.COM_DOWNLOAD:

				break;
			case PeerCommands.COM_QUIT:

				break;
			case PeerCommands.COM_SHOW:

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