package luckyclient.tool.playsound;

import javax.sound.sampled.*;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import luckyclient.execution.webdriver.BaseWebDrive;

import java.io.IOException;
import java.net.URL;

public class URLAudioPlayer {

	/*
	 * public static void main(String[] args) {
	 * 
	 * // Replace "YOUR_AUDIO_URL" with the actual URL of the audio file you want to
	 * play
	 * 
	 * String audioUrl =
	 * "https://nls-gateway-cn-shanghai.aliyuncs.com/stream/v1/tts?appkey=rfs83bFVLgV9opST&token=91c5ce6f3a9d47379e99928bfdd0a2be&text=%E7%82%B9%E5%87%BB%E7%99%BB%E5%BD%95&format=wav&voice=siyue&sample_rate=16000";
	 * 
	 * 
	 * 
	 * try { // Create an AudioInputStream from the URL AudioInputStream
	 * audioInputStream = AudioSystem.getAudioInputStream(new URL(audioUrl));
	 * 
	 * // Get the AudioFormat of the audio data AudioFormat format =
	 * audioInputStream.getFormat();
	 * 
	 * // Create a DataLine.Info object for the SourceDataLine (speakers)
	 * DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
	 * 
	 * // Check if the AudioSystem supports the specified DataLine.Info if
	 * (!AudioSystem.isLineSupported(info)) {
	 * System.out.println("Line not supported"); return; }
	 * 
	 * // Open the SourceDataLine SourceDataLine line = (SourceDataLine)
	 * AudioSystem.getLine(info); line.open(format); line.start();
	 * 
	 * // Buffer to read audio data from the AudioInputStream byte[] buffer = new
	 * byte[4096]; int bytesRead;
	 * 
	 * // Read audio data from the AudioInputStream and write it to the
	 * SourceDataLine while ((bytesRead = audioInputStream.read(buffer, 0,
	 * buffer.length)) != -1) {
	 * 
	 * line.write(buffer, 0, bytesRead); }
	 * 
	 * // Wait until all data is played line.drain();
	 * 
	 * // Close the SourceDataLine and AudioInputStream line.close();
	 * audioInputStream.close(); } catch (UnsupportedAudioFileException |
	 * LineUnavailableException | IOException e) { e.printStackTrace(); } }
	 */

	public static void playVoice(WebDriver driver, String text) {
		// »ñÈ¡ä¯ÀÀÆ÷µÄSession×´Ì¬
        boolean isSessionActive = (boolean) ((JavascriptExecutor) driver).executeScript(
                "return window.navigator.cookieEnabled;");
		if (isSessionActive) {			
			// Replace "YOUR_AUDIO_URL" with the actual URL of the audio file you want to play
			String audioUrl = SpeechSynthesizerRestfulDemo.getVoice(text);

			// Play the audio in a separate thread
			new Thread(() -> playAudio(audioUrl)).start();
		}

	}

	private static void playAudio(String audioUrl) {
		try {
			// Create an AudioInputStream from the URL
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new URL(audioUrl));

			// Get the AudioFormat of the audio data
			AudioFormat format = audioInputStream.getFormat();

			// Create a DataLine.Info object for the SourceDataLine (speakers)
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

			// Check if the AudioSystem supports the specified DataLine.Info
			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("Line not supported");
				return;
			}

			// Open the SourceDataLine
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();

			// Buffer to read audio data from the AudioInputStream
			byte[] buffer = new byte[4096];
			int bytesRead;
			int i = 0;

			// Read audio data from the AudioInputStream and write it to the SourceDataLine
			while ((bytesRead = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
				if (i < 2) {
					BaseWebDrive.waitForSeconds("1");					
				}
				i = i + 1;
				line.write(buffer, 0, bytesRead);

			}

			// Wait until all data is played
			line.drain();

			// Close the SourceDataLine and AudioInputStream
			line.close();
			audioInputStream.close();
		} catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
	}
}
