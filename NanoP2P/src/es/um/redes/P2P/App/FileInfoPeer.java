package es.um.redes.P2P.App;

import es.um.redes.P2P.util.FileInfo;

import java.net.InetSocketAddress;
import java.util.HashSet;


public class FileInfoPeer {
	private FileInfo fileInfo;
	private HashSet<InetSocketAddress> peerSet;

	public FileInfoPeer(FileInfo fileInfo) {
		peerSet = new HashSet<InetSocketAddress>();
		this.fileInfo = fileInfo;
	}

	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public HashSet<InetSocketAddress> getPeerSet() {
		return peerSet;
	}

	public void addPeer(InetSocketAddress[] peer) {
		for(int i =0; i< peer.length; i++)
			if(peer[i] != null)
				peerSet.add(peer[i]);
	}

	@Override
	public String toString() {
		return "FileInfoPeer{" + "fileInfo = " + fileInfo.toString() + ", peerSet = {" + peerSet.toString() + "}}";
	}
}
