package luckyclient.tool.playsound;

import java.io.File;
import java.io.FileOutputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TalkinggenieTTS {
	 /**
     * HTTPS GET
     */
    public static String processGETRequet(String text) {
        String url = "https://dds.dui.ai/runtime/v1/synthesize";
        url = url + "?voiceId=" + "xijunm";        
        url = url + "&text=" + text;
        url = url + "&speed=" + "1";        
        url = url + "&volume=" + "50";
        url = url + "&audioType=" + "wav";
       
        System.out.println("URL: " + url);
        
        Request request = new Request.Builder().url(url).get().build();
        try {
            long start = System.currentTimeMillis();
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();            
            String contentType = response.header("Content-Type");
            String audioSaveFile = "syAudio.wav";
            if ("audio/wav".equals(contentType)) {
                File f = new File(audioSaveFile);
                FileOutputStream fout = new FileOutputStream(f);
                fout.write(response.body().bytes());
                fout.close();                
            }
            else {
                // ContentType null 鎴栦负 "application/json"
                String errorMessage = response.body().string();
                System.out.println("The GET request failed: " + errorMessage);
            }
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
  
		return url;
    }
}
