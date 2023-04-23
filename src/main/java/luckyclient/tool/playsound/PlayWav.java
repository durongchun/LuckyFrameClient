package luckyclient.tool.playsound;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Target;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.jayway.jsonpath.internal.Path;
import com.mysql.cj.jdbc.Driver;

import cn.hutool.db.Session;
import springboot.RunService;

public class PlayWav {

	public static void playWav(String file) {
		try {
			// read wav file to audio stream
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(file));
			// read audio format from stream
			AudioFormat audioFormat = audioInputStream.getFormat();
			System.out.println("采样率：" + audioFormat.getSampleRate());
			System.out.println("总帧数：" + audioInputStream.getFrameLength());
			System.out.println("时长（秒）：" + audioInputStream.getFrameLength() / audioFormat.getSampleRate());
			// SourceDataLine info
			Info dataLineInfo = new Info(SourceDataLine.class, audioFormat);

			SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			byte[] b = new byte[1024];
			int len = 0;
			sourceDataLine.open(audioFormat, 1024);
			sourceDataLine.start();
			while ((len = audioInputStream.read(b)) > 0) {
				sourceDataLine.write(b, 0, len);
			}

			audioInputStream.close();
			sourceDataLine.drain();
			sourceDataLine.close();

		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}

	}

	public static void playFileVoice(String url) throws UnsupportedAudioFileException, IOException {
		URL audioUrl = new URL(url);
		InputStream inputStream = audioUrl.openStream();
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		AudioInputStream sound = AudioSystem.getAudioInputStream(bis);
		AudioFormat format = sound.getFormat();
		SourceDataLine line;

	}
	
	//保存文件到本地
    @SuppressWarnings("unused")
	private static void saveFile(String audioUrl, String targetPath) throws IOException {
        URL url = new URL(audioUrl );
        InputStream inputStream = url.openStream();
        BufferedInputStream in = new BufferedInputStream(inputStream) ;
        BufferedOutputStream out =new BufferedOutputStream(new FileOutputStream(targetPath)) ;
        byte[] bytes = new byte[1024];
        int len=0;
        while((len=in.read(bytes))!=-1){
            out.write(bytes,0,len);
        }
        out.close();
        in.close();
    }


	public static void playVoice(String text, WebDriver driver) {
		try {
			driver.manage().getCookies();
			// text to voice
			// produceVoice(text);
			String filePath = System.getProperty("user.dir") + "tts_test.wav";
			try {
				saveFile(SpeechSynthesizerRestfulDemo.getVoice(text), filePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Play sound		
			File file = new File(filePath);
			if (file.exists()) {
				playWav(filePath);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}

	public static void copyVoice(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	public static void produceVoice(String text) {
		try {
			TTSSpeech.produceWavFile(text);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

//    public static void main(String[] args) {
//        new PlayWav("C:\\Users\\LucyDu\\Desktop\\Lucy\\automationspace\\workspace\\LuckyFrameClient\\src\\main\\Resources\\tts_test.wav");
//
//    }
}