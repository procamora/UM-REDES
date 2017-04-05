package es.um.redes.P2P.PeerPeer.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
//import es.um.redes.P2P.PeerPeer.Message.PeerMessage;

/**
 * @author rtitos
 * 
 * Threads of this class handle the download of file chunks from a given seed
 * through a TCP socket established to the seed socket address provided 
 * to the constructor.
 */
public class DownloaderThread  extends Thread {
	private Downloader downloader; 
	private Socket downloadSocket;
	protected DataOutputStream dos;  //FIXME USAR, ES UNA MEJORA DE STREAM
	protected DataInputStream dis;
	private int numChunksDownloaded;

	public DownloaderThread(Downloader dl, InetSocketAddress seed) {
		downloader = dl;
		try {
			downloadSocket = new Socket(seed.getAddress(), seed.getPort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//It receives a message containing a chunk and it is stored in the file
	private void receiveAndWriteChunk() {
    }

	//It receives a message containing a chunk and it is stored in the file
	private void receiveAndProcessChunkList() {
    }
		
	//Number of chunks already downloaded by this thread
    public int getNumChunksDownloaded() {
    	return numChunksDownloaded;
    }

    //Main code to request chunk lists and chunks
    public void run() {
    	//pide lista trozos o un trozo
    	//es un bucle, mientras que no te bajes el fichero
    	//si es un fichero se comprueba si nos ehemos bajado los byes del fichero
    	//si hay varios thread hay que coordinar con variable compartida, que sera la instancia this
    	//MASTER, servir trozos que te estas bajando, opcional
    	System.out.println("send download");
    	String str = downloader.getTargetFile().fileHash;
		try {
			downloadSocket.getOutputStream().write(str.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("lectura download");
    	byte[] buffer = new byte[40];
		try {
			InputStream is = downloadSocket.getInputStream();
			dis = new DataInputStream(is);
			dis.read(buffer);
			String s = new String(buffer, 0, 40);
			System.out.println(s);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
