package es.um.redes.P2P.PeerPeer.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import es.um.redes.P2P.util.FileDigest;

public class MessageChunkQuery extends Message {

	/**
	 * @author procamora
	 * 
	 *         Peer-tracker protocol data message, format "Conf"
		    1b	  20 B		5B
		 +--------------+------------+
		 |Type  | Hash  |  Num_Chunk |
		 +------+-------+------------+
	 */

	/**
	 * Size of "chunksize" field: short (2 bytes)
	 */
	// private static final int FIELD_CHUNKSIZE_BYTES = Short.SIZE / 8;

	/**
	 * Message opcodes that use the Chunk format
	 */
	private static final Byte[] _conf_opcodes = { OP_GET_CHUNK, OP_CHUNK };

	/**
	 * The chunk size.
	 */
	// private short chunkSize;
	private String fileHash;
	private long numChunk;

	/**
	 * Constructor used by tracker
	 * 
	 * @param opCode
	 *            Message type
	 * @param tid
	 * @param fileHash
	 *            The chunk size
	 * @param numChunk
	 */
	public MessageChunkQuery(byte opCode, byte tid, String fileHash, long numChunk) {
		setOpCode(opCode);
		setTransId(tid);
		this.fileHash = fileHash;
		this.numChunk = numChunk;
		valid = true;
	}

	/**
	 * Constructor used by client when creating message response after receiving
	 * 
	 * @param buf
	 */
	public MessageChunkQuery(DataInputStream dis, byte respOpcode) {
		if (fromDataInputStream(dis, respOpcode) == false) {
			throw new RuntimeException("Failed to parse message: format is not Chunk.");
		} else {
			// assert(valid);
			valid = true;
		}
	}

	/**
	 * Creates a byte array ready for sending a datagram packet, from a valid
	 * message of Chunk format
	 */
	public byte[] toByteArray() {
		int byteBufferLength = FIELD_OPCODE_BYTES + FIELD_TRANSID_BYTES + FIELD_FILEHASH_BYTES + FIELD_NUMCHUNKSIZE;

		ByteBuffer buf = ByteBuffer.allocate(byteBufferLength);

		// Opcode
		buf.put((byte) this.getOpCode());

		// Trans id
		buf.put((byte) this.getTransId());

		// File hash
		buf.put(FileDigest.getDigestFromHexString(this.getFileHash()));

		// Num Chunk
		buf.putLong((long) this.getNumChunk());

		return buf.array();
	}

	/**
	 * Creates a valid message of Conf format, from the byte array of the
	 * received packet
	 */
	@Override
	protected boolean fromDataInputStream(DataInputStream dis, byte respOpcode) {

		try {
			// Opcode
			setOpCode(respOpcode);

			// Trans id
			setTransId(dis.readByte());

			// File hash
			byte[] b = new byte[FIELD_FILEHASH_BYTES];
			dis.read(b, 0, b.length);
			this.fileHash = new String(FileDigest.getChecksumHexString(b));

			// Num Chunk
			this.numChunk = dis.readLong();

			valid = true;
			
		} catch (IOException e) {
			e.printStackTrace();
			assert (valid == false);
		}
		return valid;
	}

	public long getNumChunk() {
		return numChunk;
	}

	public String getFileHash() {
		return fileHash;
	}

	public String toString() {
		assert (valid);
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("Type:" + this.getOpCodeString());
		strBuf.append(" TransId:" + this.getTransId());
		strBuf.append(" Hash:" + this.getFileHash());
		strBuf.append(" NumChunk:" + this.getNumChunk());
		return strBuf.toString();
	}

	/**
	 * For checking opcode validity.
	 */
	private static final Set<Byte> conf_opcodes = Collections
			.unmodifiableSet(new HashSet<Byte>(Arrays.asList(_conf_opcodes)));

	protected void _check_opcode(byte opcode) {
		if (!conf_opcodes.contains(opcode))
			throw new RuntimeException("Opcode " + opcode + " no es de tipo Chunk.");
	}

	@Override
	public int getTotalFragments() {
		return 1;
	}

	@Override
	public void reassemble(Vector<Message> fragments) {
	}

	@Override
	public boolean fragmented() {
		return false;
	}
}