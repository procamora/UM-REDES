package es.um.redes.P2P.App;

import java.util.Comparator;

public class ComparadorFileInfoPeer implements Comparator<FileInfoPeer> {

	@Override
	public int compare(FileInfoPeer o1, FileInfoPeer o2) {
		int n1 = o1.getPeerSet().size();
		int n2 = o2.getPeerSet().size();
		if (n1 > n2)
			return 1;
		else if (n1 < n2)
			return -1;
		return 0;
	}

}
