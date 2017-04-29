package es.um.redes.P2P.PeerPeer.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class MessageChunkQueryResponse extends Message {

	/**
	 * @author procamora
	 * 
	 *         Peer-tracker protocol data message, format "ChunkACK"
	 * 
	 *              1b   20 B       5B 
	 *         +--------------+------------+ 
	 *         |Type   | Hash |  Num_Chunk | 
	 *         +------+-------+------------+
	 */

	/**
	 * Size of "chunksize" field: short (2 bytes)
	 */
	// private static final int FIELD_CHUNKSIZE_BYTES = Short.SIZE / 8;

	/**
	 * Message opcodes that use the Chunk format
	 */
	private static final Byte[] _conf_opcodes = { OP_GET_CHUNK_ACK, OP_CHUNK_ACK };

	/**
	 * The chunk size.
	 */
	// private short chunkSize;
	private short numChunk;
	private byte[] datos;
	private short chunkSize;

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
	public MessageChunkQueryResponse(byte opCode, byte tid, short numChunk, byte[] datos, short chunkSize) {
		setOpCode(opCode);
		setTransId(tid);
		this.numChunk = numChunk;
		this.datos = datos;
		this.chunkSize = chunkSize;
		valid = true;
	}

	/**
	 * Constructor used by client when creating message response after receiving
	 * 
	 * @param buf
	 */
	public MessageChunkQueryResponse(DataInputStream dis, byte respOpcode) {
		if (fromDataInputStream(dis, respOpcode) == false) {
			throw new RuntimeException("Failed to parse message: format is not ChunkACK.");
		} else {
			// assert(valid);
			valid = true;
		}
	}

	/**
	 * Creates a byte array ready for sending a datagram packet, from a valid
	 * message of ChunkACK format
	 */
	public byte[] toByteArray() {
		int byteBufferLength = FIELD_OPCODE_BYTES + FIELD_TRANSID_BYTES + FIELD_CHUNKSIZE_BYTES + datos.length;

		ByteBuffer buf = ByteBuffer.allocate(byteBufferLength);

		// Opcode
		buf.put((byte) this.getOpCode());

		// Trans id
		buf.put((byte) this.getTransId());

		// Num Chunk
		buf.putShort((short) this.getNumChunk());

		// File Datos
		buf.put((byte[]) this.getDatos());

		return buf.array();
	}

	/**
	 * Creates a valid message of ChunkACK format, from the byte array of the
	 * received packet
	 */
	@Override
	protected boolean fromDataInputStream(DataInputStream dis, byte respOpcode) {

		try {
			// Opcode
			setOpCode(respOpcode);

			// Trans id
			setTransId(dis.readByte());

			// Num Chunk
			this.numChunk = dis.readShort();

			// File Datos
			chunkSize = 4096;// FIXME cambiar
			byte[] dat = new byte[chunkSize];
			int tam = dis.read(dat);
			setDatos(dat, tam);

			valid = true;

		} catch (IOException e) {
			e.printStackTrace();
			assert (valid == false);
		}
		return valid;
	}

	/**
	 * metodo para establecer el datos con el tamaño exato de bytes que tiene
	 * necesario para ficheros menores que el tamaño de chunk
	 */
	private void setDatos(byte[] aux, int tam) {
		datos = new byte[tam];
		datos = Arrays.copyOf(aux, tam);
	}

	public short getNumChunk() {
		return numChunk;
	}

	public byte[] getDatos() {
		return datos;
	}

	public String getDatosString() {
		String s = new String(datos);
		return s;
	}

	// obtienes el array de chunk del que dispone el peer
	public short[] getDatosChunk() {

		short[] s = desconcatenaArratBytesDatos();

		// imprimo la lista de chunk de la que dispone el peer
		for (int i = 0; i < s.length; i++)
			System.out.println(s[i]);
		return s;

	}

	/**
	 * Metodo privado usado por OP_GET_CHUNK_ACK, lee el array de bytes y lo
	 * convierte a un array de short
	 */
	private short[] desconcatenaArratBytesDatos() {
		if (getOpCode() != OP_GET_CHUNK_ACK)
			throw new IllegalStateException("Esta funcion solo la puede hacer OP_GET_CHUNK_ACK");

		ByteBuffer buf = ByteBuffer.wrap(datos);
		short[] a = new short[numChunk];
		for (int i = 0; i < numChunk; i++)
			a[i] = buf.getShort();

		return a;
	}

	public String toString() {
		assert (valid);
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("Type:" + this.getOpCodeString());
		strBuf.append(" TransId:" + this.getTransId());
		strBuf.append(" NumChunk:" + this.getNumChunk());
		strBuf.append(" Datos:" + this.getDatos());
		return strBuf.toString();
	}

	/**
	 * For checking opcode validity.
	 */
	private static final Set<Byte> conf_opcodes = Collections
			.unmodifiableSet(new HashSet<Byte>(Arrays.asList(_conf_opcodes)));

	protected void _check_opcode(byte opcode) {
		if (!conf_opcodes.contains(opcode))
			throw new RuntimeException("Opcode " + opcode + " no es de tipo ChunkACK.");
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

	/**
	 * Metodo estatico que recibe arrays de short y los concatena retornando un
	 * array de bytes
	 */
	public static byte[] concatenateByteArrays(short... arraysChunks) {

		ByteBuffer buf = ByteBuffer.allocate(arraysChunks.length * FIELD_CHUNKSIZE_BYTES);
		for (short cs : arraysChunks)
			buf.putShort((short) cs);

		return buf.array();
	}

}