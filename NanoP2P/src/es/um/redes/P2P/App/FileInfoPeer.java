package es.um.redes.P2P.App;

import es.um.redes.P2P.util.FileInfo;

import java.util.HashSet;


public class FileInfoPeer {
	private FileInfo fileInfo;
	private HashSet<String> peerSet;

	public FileInfoPeer(FileInfo fileInfo) {
		peerSet = new HashSet<String>();
		this.fileInfo = fileInfo;
	}

	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public HashSet<String> getPeerSet() {
		return peerSet;
	}

	public boolean anadirPeer(String peer) {
		return peerSet.add(peer);
	}

	@Override
	public String toString() {
		return "FileInfoPeer{" + "fileInfo = " + fileInfo.toString() + ", peerSet = {" + peerSet.toString() + "}}";
	}
}
