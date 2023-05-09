package luckyclient.tool.playsound;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SpeechSynthesizerRestfulDemo {
	private static String accessToken;
	private String appkey;
	private static Logger logger = LoggerFactory.getLogger(SpeechSynthesizerRestfulDemo.class);
	private static Long runTime;

    public SpeechSynthesizerRestfulDemo(String appkey, String token) {
        this.appkey = appkey;
        this.accessToken = token;        
    }

    /**
     * HTTPS GET璇锋眰
     */
    public String processGETRequet(String text, String audioSaveFile, String format, int sampleRate, String voice) {
        /**
         * 璁剧疆HTTPS GET璇锋眰锟�?
         * 1.浣跨敤HTTPS鍗忚
         * 2.璇煶璇嗗埆鏈嶅姟鍩熷悕锛歯ls-gateway-cn-shanghai.aliyuncs.com
         * 3.璇煶璇嗗埆鎺ュ彛璇锋眰璺緞锟�?/stream/v1/tts
         * 4.璁剧疆蹇呴』璇锋眰鍙傛暟锛歛ppkey銆乼oken銆乼ext銆乫ormat銆乻ample_rate
         * 5.璁剧疆鍙拷?锟借姹傚弬鏁帮細voice銆乿olume銆乻peech_rate銆乸itch_rate
         */
        String url = "https://nls-gateway-cn-shanghai.aliyuncs.com/stream/v1/tts";
        url = url + "?appkey=" + appkey;
        url = url + "&token=" + accessToken;
        url = url + "&text=" + text;
        url = url + "&format=" + format;
        url = url + "&voice=" + voice;
        url = url + "&sample_rate=" + String.valueOf(sampleRate);
        // voice 鍙戦煶浜猴紝鍙拷?锟斤紝榛樿鏄痻iaoyun锟�?
        // url = url + "&voice=" + "xiaoyun";
        // volume 闊抽噺锛岃寖鍥存槸0~100锛屽彲閫夛紝榛樿50锟�?
        // url = url + "&volume=" + String.valueOf(50);
        // speech_rate 璇拷?锟斤紝鑼冨洿锟�?-500~500锛屽彲閫夛紝榛樿锟�?0锟�?
        // url = url + "&speech_rate=" + String.valueOf(0);
        // pitch_rate 璇皟锛岃寖鍥存槸-500~500锛屽彲閫夛紝榛樿锟�?0锟�?
        // url = url + "&pitch_rate=" + String.valueOf(0);
        System.out.println("URL: " + url);
        /**
         * HTTPS GET璇锋眰锛屽鐞嗘湇鍔＄鐨勫搷搴�
         */
        Request request = new Request.Builder().url(url).get().build();
        try {
            long start = System.currentTimeMillis();
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();            
            String contentType = response.header("Content-Type");
            if ("audio/mpeg".equals(contentType)) {
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
    /**
     * HTTPS POST璇锋眰
     */
    public void processPOSTRequest(String text, String audioSaveFile, String format, int sampleRate, String voice) {
        /**
         * 璁剧疆HTTPS POST璇锋眰锟�?
         * 1.浣跨敤HTTPS鍗忚
         * 2.璇煶鍚堟垚鏈嶅姟鍩熷悕锛歯ls-gateway-cn-shanghai.aliyuncs.com
         * 3.璇煶鍚堟垚鎺ュ彛璇锋眰璺緞锟�?/stream/v1/tts
         * 4.璁剧疆蹇呴』璇锋眰鍙傛暟锛歛ppkey銆乼oken銆乼ext銆乫ormat銆乻ample_rate
         * 5.璁剧疆鍙拷?锟借姹傚弬鏁帮細voice銆乿olume銆乻peech_rate銆乸itch_rate
         */
        String url = "https://nls-gateway-cn-shanghai.aliyuncs.com/stream/v1/tts";
        JSONObject taskObject = new JSONObject();
        taskObject.put("appkey", appkey);
        taskObject.put("token", accessToken);
        taskObject.put("text", text);
        taskObject.put("format", format);
        taskObject.put("voice", voice);
        taskObject.put("sample_rate", sampleRate);
        // voice 鍙戦煶浜猴紝鍙拷?锟斤紝榛樿鏄痻iaoyun锟�?
        // taskObject.put("voice", "xiaoyun");
        // volume 闊抽噺锛岃寖鍥存槸0~100锛屽彲閫夛紝榛樿50锟�?
        // taskObject.put("volume", 50);
        // speech_rate 璇拷?锟斤紝鑼冨洿锟�?-500~500锛屽彲閫夛紝榛樿锟�?0锟�?
        // taskObject.put("speech_rate", 0);
        // pitch_rate 璇皟锛岃寖鍥存槸-500~500锛屽彲閫夛紝榛樿锟�?0锟�?
        // taskObject.put("pitch_rate", 0);
        String bodyContent = taskObject.toJSONString();
        System.out.println("POST Body Content: " + bodyContent);
        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json"), bodyContent);
        Request request = new Request.Builder()
            .url(url)
            .header("Content-Type", "application/json")
            .post(reqBody)
            .build();
        try {
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            String contentType = response.header("Content-Type");
            if ("audio/mpeg".equals(contentType)) {
                File f = new File(audioSaveFile);
                FileOutputStream fout = new FileOutputStream(f);
                fout.write(response.body().bytes());
                fout.close();
                System.out.println("The POST request succeed!");
            }
            else {
                // ContentType  null 鎴栦负 "application/json"
                String errorMessage = response.body().string();
                System.out.println("The POST request failed: " + errorMessage);
            }
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getVoice(String text) { 
    	timer();
    	String token = accessToken;
        String appkey = "rfs83bFVLgV9opST";
        SpeechSynthesizerRestfulDemo demo = new SpeechSynthesizerRestfulDemo(appkey, token);
        
        String textUrlEncode = text;
        try {
            textUrlEncode = URLEncoder.encode(textUrlEncode, "UTF-8")
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }        
        String audioSaveFile = "syAudio.wav";
        String format = "wav";
        int sampleRate = 16000;
        String url = demo.processGETRequet(textUrlEncode, audioSaveFile, format, sampleRate, "siyue");
        //demo.processPOSTRequest(text, audioSaveFile, format, sampleRate, "siyue");        
		return url;
    }
    
    public static void timer() {       
        // 获取当前时间        
        long currentTime = System.currentTimeMillis();

        // 计算下一个每天的请求时间点
        long interval = 24 * 60 * 60 * 1000; // 一天的毫秒数
        long delay = currentTime - runTime;
        
        if (delay > interval) {
        	String newToken = getTokenFromServer();
        	accessToken = newToken;
        }

    }
    
    public static Long runTime() {
    	// 获取当前时间
    	runTime = System.currentTimeMillis();
        return runTime;
	}

    static class MyTask extends TimerTask {
        public void run() {
            // 在这里编写需要执行的代码
            String newToken = getTokenFromServer();
            if (newToken != null) {
            	accessToken = newToken;
            }
        }
    }

    public static String getTokenFromServer() {
        // 在这里编写从服务端获取token的代码
    	try {
    		accessToken = TokenDemo.getToken();
    		return accessToken;
		} catch (ClientException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();			
	        return null;
		}
        
    }
    
   
}