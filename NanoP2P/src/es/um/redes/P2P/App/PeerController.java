package es.um.redes.P2P.App;

import java.net.InetSocketAddress;

import es.um.redes.P2P.PeerTracker.Client.Reporter;
import es.um.redes.P2P.PeerTracker.Message.*;
import es.um.redes.P2P.util.FileInfo;
import static es.um.redes.P2P.App.PeerCommands.*; //variables estaticas

public class PeerController implements PeerControllerIface {
	/**
	 * The shell associated to this controller.
	 */
	private PeerShellIface shell;

	private byte currentCommand;
	private Reporter reporter;

	public PeerController(Reporter client) {
		shell = new PeerShell();
		reporter = client;
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

		// analisis de casos
		switch (currentCommand) {
		case COM_CONFIG:

			break;
		case COM_ADDSEED:

			break;
		case COM_QUERY:

			break;
		case COM_DOWNLOAD:

			break;
		case COM_QUIT:

			break;
		case COM_SHOW:

			break;
		case COM_HELP:

			break;
		case COM_INVALID:

			break;
		default:
			break;
		}

		Message m = createMessageFromCurrentCommand();
		reporter.conversationWithTracker(m);
	}

	@Override
	public Message createMessageFromCurrentCommand() {
		Message control;
		//analisis de casos
		//si confiicuracion
		control = (MessageControl) Message.makeGetConfRequest();
		
		//si add_seed
		control = (MessageFileInfo) Message.makeAddSeedRequest(seederPort, Peer.db.getLocalSharedFiles())

		return control;
	}

	@Override
	public void processMessageFromTracker(Message response) {
		// TODO Auto-generated method stub

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