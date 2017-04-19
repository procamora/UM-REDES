
package es.um.redes.P2P.PeerPeer.Message;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import es.um.redes.P2P.util.FileDigest;

/**
 * Abstract class that models peer-peer messages without a specific format
 *
 * @author procamora
 *
 */

public abstract class Message {
	/**
	 * Size of "opcode" field: byte (1 bytes)
	 */
	protected static final int FIELD_OPCODE_BYTES = 1;

	/**
	 * Size of "list-len" field: short (2 bytes)
	 */
	protected static final int FIELD_LONGLIST_BYTES = Short.SIZE / 8;
	/**
	 * Size of "hash" field(s) used by subclasses (160 bits in SHA-1, 20 bytes)
	 */
	protected static final int FIELD_FILEHASH_BYTES = FileDigest.getFileDigestSize();
	/**
	 * Size of "chunksize" field: short (2 bytes)
	 */
	protected static final int FIELD_CHUNKSIZE_BYTES = Short.SIZE / 8;
	/**
	 * Size of "trans_id" field: 1 byte
	 */
	protected static final byte FIELD_TRANSID_BYTES = 1;

	/**
	 * Opcodes in the peer-tracker protocol of nanoP2P
	 */
	public static final byte INVALID_OPCODE = 0;
	public static final byte OP_GET_CHUNK = 1;
	public static final byte OP_GET_CHUNK_ACK = 2;
	public static final byte OP_CHUNK = 3;
	public static final byte OP_CHUNK_ACK = 4;

	/**
	 * Message opcode.
	 */
	private byte opCode;

	/**
	 * Message transaction ID.
	 */
	private byte transId;

	/**
	 * Current transaction ID.
	 */
	private static byte nextTransId = 0;

	/*
	 * Validity flag used for correctness check (asserts)
	 */
	protected boolean valid; // Flag set by fromByteArray upon success

	/*
	 * Abstract methods whose implementation depends on the message format.
	 */
	protected abstract boolean fromByteArray(byte[] buf);

	public abstract byte[] toByteArray();

	public abstract String toString();

	/**
	 * @return The total number of fragments the original message was split
	 *         into.
	 */
	public abstract int getTotalFragments();

	/**
	 * Reassembles a set of fragments into one of the fragments, obtaining the
	 * original message before it was split.
	 * 
	 * @param fragments
	 *            All the fragments of the message, including this object
	 */
	public abstract void reassemble(Vector<Message> fragments);

	/**
	 * @return True if this message consists of several fragments.
	 */
	public abstract boolean fragmented();

	/**
	 * Default class constructor, creates "empty" message in invalid state
	 */
	public Message() {
		opCode = INVALID_OPCODE;
		valid = false;
	}

	private final void sanityCheck() {
		if (!valid)
			throw new RuntimeException("Message object accessed before correct initialization");
	}

	public final byte getOpCode() {
		sanityCheck();
		return opCode;
	}

	public static synchronized byte fetchAndIncrementTransId() {
		if (nextTransId + 1 < nextTransId) {
			System.err.println("Rollover occured in TransId counter");
		}
		return nextTransId++;
	}

	public final byte getTransId() {
		sanityCheck();
		return transId;
	}

	public final String getOpCodeString() {
		sanityCheck();
		switch (opCode) {
			case OP_GET_CHUNK:
				return "GET_CHUNK";
			case OP_GET_CHUNK_ACK:
				return "GET_CHUNK_ACK";
			case OP_CHUNK:
				return "CHUNK";
			case OP_CHUNK_ACK:
				return "CHUNK_ACK";
			default:
				return "INVALID_TYPE";
		}
	}

	/**
	 * @param opCode
	 */

	protected final void setOpCode(byte opCode) {
		assert (!valid);
		_check_opcode(opCode);

		this.opCode = opCode;
	}

	protected final void setTransId(byte id) {
		this.transId = id;
	}

	public final void setNewTransId() {
		this.transId = fetchAndIncrementTransId();
	}

	public static Message makeGetChunkRequest(String hash, short numChunks) {
		byte requestOpcode = OP_GET_CHUNK;
		byte tid = fetchAndIncrementTransId();
		return new MessageCQuery(requestOpcode, tid, hash, numChunks);
	}

	public static Message makeGetChunkResponseRequest(short numChunk, byte[] datos, short chunkSize) {
		byte tid = fetchAndIncrementTransId();
		return new MessageCQueryACK(OP_GET_CHUNK_ACK, tid, numChunk, datos, chunkSize);
	}

	public static Message makeChunkRequest(String hash, short numChunks) {
		byte requestOpcode = OP_CHUNK;
		byte tid = fetchAndIncrementTransId();
		return new MessageCQuery(requestOpcode, tid, hash, numChunks);
	}

	public static Message makeChunkResponseRequest(short numChunk, byte[] datos, short chunkSize) {
		byte tid = fetchAndIncrementTransId();
		return new MessageCQueryACK(OP_CHUNK_ACK, tid, numChunk, datos, chunkSize);
	}

	/**
	 * Class method to parse a request message received by the tracker
	 * 
	 * @param buf
	 *            The byte array of the received packet
	 * @return A message of the appropriate format representing this request
	 */
	public static Message parseRequest(byte[] buf) {
		if (buf.length < FIELD_OPCODE_BYTES + FIELD_TRANSID_BYTES) {
			throw new IllegalArgumentException("Failed to parse request: byte[] argument has length " + buf.length);
		}
		byte reqOpcode = buf[0];
		// byte transId = buf[1];
		switch (reqOpcode) {
			case OP_GET_CHUNK:
			case OP_CHUNK:
				return new MessageCQuery(buf);
			case OP_GET_CHUNK_ACK:
			case OP_CHUNK_ACK:
				return new MessageCQueryACK(buf);
			default:
				throw new IllegalArgumentException("Invalid request opcode: " + reqOpcode);
		}
	}

	/**
	 * Class method to parse a response message received by the client
	 * 
	 * @param buf
	 *            The byte array of the packet received from the tracker
	 * @return A message of the appropriate format representing this response
	 */
	public static Message parseResponse(byte[] buf) {
		if (buf.length < FIELD_OPCODE_BYTES + FIELD_TRANSID_BYTES) {
			throw new IllegalArgumentException("Failed to parse response: buffer has length " + buf.length);
		}
		byte respOpcode = buf[0];
		switch (respOpcode) {
			case OP_GET_CHUNK:
				
			case OP_CHUNK:
				return new MessageChunkQuery(buf);
			case OP_GET_CHUNK_ACK:
				
			case OP_CHUNK_ACK:
				return new MessageChunkQueryACK(buf);
			default:
				throw new IllegalArgumentException("Failed to parse message: Invalid response opcode " + respOpcode);
		}
	}

	public byte getResponseOpcode() {
		assert (valid);
		if (opCode == OP_GET_CHUNK) {
			return OP_GET_CHUNK_ACK;
		} else if (opCode == OP_CHUNK) {
			return OP_CHUNK_ACK;
		} else {
			throw new RuntimeException("Opcode " + opCode + " is not a valid request code or it has no response.");
		}
	}

	/* To check opcode validity */
	private static final Byte[] _valid_opcodes = { OP_GET_CHUNK, OP_GET_CHUNK_ACK, OP_CHUNK, OP_CHUNK_ACK };
	private static final Set<Byte> valid_opcodes = Collections
			.unmodifiableSet(new HashSet<Byte>(Arrays.asList(_valid_opcodes)));

	// Protected to allow overriding in subclasses
	protected void _check_opcode(byte opcode) {
		if (!valid_opcodes.contains(opcode))
			throw new RuntimeException("Opcode " + opcode + " no es válido.");
	}
}
