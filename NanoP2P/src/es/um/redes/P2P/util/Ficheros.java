package es.um.redes.P2P.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Ficheros {

	/**
	 * 
	 * @param fichero
	 *            nombre del fichero a leer
	 * @param CHUNK_SIZE
	 *            tamaño del trozo
	 * @param pos
	 *            posición dentro del fichero
	 * @return array byes leidos
	 */
	public static byte[] lectura(String fichero, int CHUNK_SIZE, int pos) {

		byte chunk[] = new byte[CHUNK_SIZE];
		File file = new File(fichero);
		int leidos = 0;
		if (!file.exists())
			throw new IllegalStateException("No existe el fichero: " + fichero);

		try {
			RandomAccessFile rfi = new RandomAccessFile(file, "r");
			do {
				// Nos situamos en la posición inical + los bytes leidos
				rfi.seek(pos + leidos);
				leidos = rfi.read(chunk); // Leemos el trozo
			} while (leidos != -1 && leidos != CHUNK_SIZE && (long) leidos != file.length());
			rfi.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// este array tiene exactamente el tamaño de lo leido, no tiene
		// caracteres null
		byte[] buffer = Arrays.copyOfRange(chunk, 0, leidos);
		System.out.println("slice " + buffer.length);

		return buffer;
	}

	/**
	 * 
	 * @param fichero
	 *            nombre del fichero
	 * @param datos
	 *            array de byes a escribir
	 * @param pos
	 *            posición dentro del fichero
	 */
	public static void escritura(String fichero, byte[] datos, int pos) {
		File file = new File(fichero);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("tamaño length escribir " + datos.length);

		try {
			RandomAccessFile rfo = new RandomAccessFile(file, "rw");
			rfo.seek(pos);
			rfo.write(datos, 0, datos.length);// puede que lenght -1
			rfo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String f = "pkt.png";
		byte[] array = lectura("/mnt/WD_BLACK/pablojoserocamora@gmail.com/Universidad/REDES/UM-REDES/comp/peer2/" + f,
				4096, 0);
		String s = new String(array);
		System.out.println("tam " + s.length());

		escritura("/tmp/s", array, 0);

	}

}
