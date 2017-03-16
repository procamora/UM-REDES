package es.um.redes.P2P.App;

import java.util.Comparator;

import es.um.redes.P2P.util.FileInfo;

public class ComparadorFiles2 implements Comparator<FileInfo> {

	@Override
	public int compare(FileInfo o1, FileInfo o2) {
		return o1.fileHash.compareTo(o2.fileHash);
	}

}
