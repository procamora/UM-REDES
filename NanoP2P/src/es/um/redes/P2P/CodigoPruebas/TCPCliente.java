package es.um.redes.P2P.CodigoPruebas;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import es.um.redes.P2P.PeerPeer.Client.*;
import es.um.redes.P2P.util.FileInfo;

public class TCPCliente {

	public static void main(String[] args) {
		FileInfo targetFile = new FileInfo();
		//targetFile.fileHash = "134DCA1B35CF275F816D9689A0EF50A0A831786C"; //jflap
		targetFile.fileHash = "D63A50B05908A11BCFE4EAF9C4B6B04905ABA4E5"; //arr
		//targetFile.fileHash = "1E6DFBDBE31C610FA72245677E4B6A244786144D"; //lago
		//targetFile.fileHash = "87C43258EB02FD2D077B6AC9B4D520D28B49EB04"; //pkt
		targetFile.fileName = "arrr";
		targetFile.fileSize = 58;

		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			InetSocketAddress so = new InetSocketAddress(addr, 20000);
			Downloader d = new Downloader((short) 4096, targetFile, null);
			new DownloaderThread(d, so).start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}
}
