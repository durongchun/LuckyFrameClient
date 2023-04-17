package luckyclient.tool.playsound;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
	private String accessToken;
	private String appkey;
	private static Logger logger = LoggerFactory.getLogger(SpeechSynthesizerRestfulDemo.class);

    public SpeechSynthesizerRestfulDemo(String appkey, String token) {
        this.appkey = appkey;
        this.accessToken = token;
    }

    /**
     * HTTPS GET请求
     */
    public String processGETRequet(String text, String audioSaveFile, String format, int sampleRate, String voice) {
        /**
         * 设置HTTPS GET请求�?
         * 1.使用HTTPS协议
         * 2.语音识别服务域名：nls-gateway-cn-shanghai.aliyuncs.com
         * 3.语音识别接口请求路径�?/stream/v1/tts
         * 4.设置必须请求参数：appkey、token、text、format、sample_rate
         * 5.设置可�?�请求参数：voice、volume、speech_rate、pitch_rate
         */
        String url = "https://nls-gateway-cn-shanghai.aliyuncs.com/stream/v1/tts";
        url = url + "?appkey=" + appkey;
        url = url + "&token=" + accessToken;
        url = url + "&text=" + text;
        url = url + "&format=" + format;
        url = url + "&voice=" + voice;
        url = url + "&sample_rate=" + String.valueOf(sampleRate);
        // voice 发音人，可�?�，默认是xiaoyun�?
        // url = url + "&voice=" + "xiaoyun";
        // volume 音量，范围是0~100，可选，默认50�?
        // url = url + "&volume=" + String.valueOf(50);
        // speech_rate 语�?�，范围�?-500~500，可选，默认�?0�?
        // url = url + "&speech_rate=" + String.valueOf(0);
        // pitch_rate 语调，范围是-500~500，可选，默认�?0�?
        // url = url + "&pitch_rate=" + String.valueOf(0);
        System.out.println("URL: " + url);
        /**
         * HTTPS GET请求，处理服务端的响应
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
                // ContentType null 或为 "application/json"
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
     * HTTPS POST请求
     */
    public void processPOSTRequest(String text, String audioSaveFile, String format, int sampleRate, String voice) {
        /**
         * 设置HTTPS POST请求�?
         * 1.使用HTTPS协议
         * 2.语音合成服务域名：nls-gateway-cn-shanghai.aliyuncs.com
         * 3.语音合成接口请求路径�?/stream/v1/tts
         * 4.设置必须请求参数：appkey、token、text、format、sample_rate
         * 5.设置可�?�请求参数：voice、volume、speech_rate、pitch_rate
         */
        String url = "https://nls-gateway-cn-shanghai.aliyuncs.com/stream/v1/tts";
        JSONObject taskObject = new JSONObject();
        taskObject.put("appkey", appkey);
        taskObject.put("token", accessToken);
        taskObject.put("text", text);
        taskObject.put("format", format);
        taskObject.put("voice", voice);
        taskObject.put("sample_rate", sampleRate);
        // voice 发音人，可�?�，默认是xiaoyun�?
        // taskObject.put("voice", "xiaoyun");
        // volume 音量，范围是0~100，可选，默认50�?
        // taskObject.put("volume", 50);
        // speech_rate 语�?�，范围�?-500~500，可选，默认�?0�?
        // taskObject.put("speech_rate", 0);
        // pitch_rate 语调，范围是-500~500，可选，默认�?0�?
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
                // ContentType  null 或为 "application/json"
                String errorMessage = response.body().string();
                System.out.println("The POST request failed: " + errorMessage);
            }
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getVoice(String text) {
        String token = null;
		try {
			token = TokenDemo.getToken();
		} catch (ClientException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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

}