package es.um.redes.P2P.PeerPeer.Client;

import java.util.concurrent.Semaphore;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

// https://stackoverflow.com/questions/1001290/console-based-progress-in-java

public class ProgressBar extends Thread {
	private double contador;
	private Semaphore continua = new Semaphore(0);
	private long total;

	public ProgressBar(long total) {
		this.total = total;
		contador = 0;
	}

	public void printProgress(long startTime, long total, double current) {
		long eta = (long) (current == 0 ? 0 : (total - current) * (System.currentTimeMillis() - startTime) / current);

		String etaHms = current == 0 ? "N/A"
				: String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
						TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
						TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

		StringBuilder string = new StringBuilder(140);
		int percent = (int) (current * 100 / total);
		string.append('\r')
				.append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
				.append(String.format(" %d%% [", percent)).append(String.join("", Collections.nCopies(percent, "=")))
				.append('>').append(String.join("", Collections.nCopies(100 - percent, " "))).append(']')
				.append(String.join("",
						Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
				.append(String.format(" %.0f/%d, ETA: %s", current, total, etaHms));

		System.out.print(string);
	}

	public void next(double aumento) {
		contador += aumento;
		continua.release();
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();

		while (total >= contador) {
			try {
				continua.acquire();
				printProgress(startTime, total, contador);
			} catch (InterruptedException e) {
			}
		}
	}
}
