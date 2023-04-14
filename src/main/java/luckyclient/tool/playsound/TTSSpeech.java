package luckyclient.tool.playsound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.nls.client.AccessToken;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.OutputFormatEnum;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizer;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizerListener;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizerResponse;
public class TTSSpeech {
	private static final Logger logger = LoggerFactory.getLogger(TTSSpeech.class);
    private static long startTime;
    private String appKey;
    NlsClient client;
    public TTSSpeech(String appKey, String accessKeyId, String accessKeySecret) {
        this.appKey = appKey;
        //应用全局创建�?个NlsClient实例，默认服务地�?为阿里云线上服务地址�?
        //获取token，使用时注意在accessToken.getExpireTime()过期前再次获取�??
        AccessToken accessToken = new AccessToken(accessKeyId, accessKeySecret);
        try {
            accessToken.apply();
            System.out.println("get token: " + accessToken.getToken() + ", expire time: " + accessToken.getExpireTime());
            client = new NlsClient(accessToken.getToken());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public TTSSpeech(String appKey, String accessKeyId, String accessKeySecret, String url) {
        this.appKey = appKey;
        AccessToken accessToken = new AccessToken(accessKeyId, accessKeySecret);
        try {
            accessToken.apply();
            System.out.println("get token: " + accessToken.getToken() + ", expire time: " + accessToken.getExpireTime());
            if(url.isEmpty()) {
                client = new NlsClient(accessToken.getToken());
            }else {
                client = new NlsClient(url, accessToken.getToken());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static SpeechSynthesizerListener getSynthesizerListener() {
        SpeechSynthesizerListener listener = null;
        try {
            	listener = new SpeechSynthesizerListener() {
                File f=new File("tts_test.wav");
                FileOutputStream fout = new FileOutputStream(f);
                private boolean firstRecvBinary = true;
              //语音合成结束
                @Override
                public void onComplete(SpeechSynthesizerResponse response) {
                	//调用onComplete时表示所有TTS数据已接收完成，因此为整个合成数据的延迟。该延迟可能较大，不�?定满足实时场景�??
                    System.out.println("name: " + response.getName() +
                        ", status: " + response.getStatus()+
                        ", output file :"+f.getAbsolutePath()
                    );
                }
              //语音合成的语音二进制数据
                @Override
                public void onMessage(ByteBuffer message) {
                    try {
                        if(firstRecvBinary) {
                            //计算首包语音流的延迟，收到第�?包语音流时，即可以进行语音播放，以提升响应�?�度（特别是实时交互场景下）�?
                            firstRecvBinary = false;
                            long now = System.currentTimeMillis();
                            logger.info("tts first latency : " + (now - TTSSpeech.startTime) + " ms");
                        }
                        byte[] bytesArray = new byte[message.remaining()];
                        message.get(bytesArray, 0, bytesArray.length);
                        fout.write(bytesArray);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFail(SpeechSynthesizerResponse response){
                	//task_id是调用方和服务端通信的唯�?标识，当遇到问题时需要提供task_id以便排查�?
                    System.out.println(
                        "task_id: " + response.getTaskId() +
                            //状�?�码 20000000 表示识别成功
                            ", status: " + response.getStatus() +
                            //错误信息
                            ", status_text: " + response.getStatusText());
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listener;
    }
    public void process(String text) {
        SpeechSynthesizer synthesizer = null;
        try {
        	//创建实例，建立连接�??
            synthesizer = new SpeechSynthesizer(client, getSynthesizerListener());
            synthesizer.setAppKey(appKey);
            //设置返回音频的编码格�?
            synthesizer.setFormat(OutputFormatEnum.WAV);
            //设置返回音频的采样率
            synthesizer.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            //发音�?
//            synthesizer.setVoice("siyue");
            synthesizer.setPitchRate(100);
            //语�?�，范围�?-500~500，默认是0�?
            synthesizer.setSpeechRate(100);
            //设置用于语音合成的文�?
            synthesizer.setText(text);
            // 是否�?启字幕功能（返回相应文本的时间戳），默认不开启，�?要注意并非所有发音人都支持该参数�?
            synthesizer.addCustomedParam("enable_subtitle", false);
          //此方法将以上参数设置序列化为JSON格式发�?�给服务端，并等待服务端确认�?
            long start = System.currentTimeMillis();
            synthesizer.start();
            logger.info("tts start latency " + (System.currentTimeMillis() - start) + " ms");
            TTSSpeech.startTime = System.currentTimeMillis();
          //等待语音合成结束
            synthesizer.waitForComplete();
            logger.info("tts stop latency " + (System.currentTimeMillis() - start) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	//关闭连接
            if (null != synthesizer) {
                synthesizer.close();
            }
        }
    }
    public void shutdown() {
        client.shutdown();
    }
      
    public static void produceWavFile(String text) throws Exception {        
        String appKey = "rfs83bFVLgV9opST";
        String id = "qHBdEsPxGd3409fY";
        String secret = "wasqwfiNX55lQuspubS7E4hvLURcEg";
        String url = ""; //默认值：wss://nls-gateway.cn-shanghai.aliyuncs.com/ws/v1

        TTSSpeech demo = new TTSSpeech(appKey, id, secret, url);
        demo.process(text);
        demo.shutdown();
    }    
   
    public void copyFile(String oldPath, String newPath) { 
    	try { 
    		int bytesum = 0; 
    		int byteread = 0; 
    		File oldfile = new File(oldPath); 
    		if (oldfile.exists()) { //文件存在�? 
    			InputStream inStream = new FileInputStream(oldPath); //读入原文�? 
    			FileOutputStream fs = new FileOutputStream(newPath); 
    			byte[] buffer = new byte[1444]; 
    			int length; 
    			while ( (byteread = inStream.read(buffer)) != -1) { 
    				bytesum += byteread; //字节�? 文件大小 
    				System.out.println(bytesum); 
    				fs.write(buffer, 0, byteread); 
    			} 
    			inStream.close(); 
    		} 
    	} 
    	catch (Exception e) { 
    		System.out.println("复制单个文件操作出错"); 
    		e.printStackTrace(); 

    	} 

    } 
    
    public static String getOtherProjectRootPath() throws IOException {        
        // 加载配置文件
    	Properties properties = new Properties();
        FileInputStream fis = new FileInputStream("otherProject.properties");
        properties.load(fis);
        fis.close();

        // 获取 project.root 属�?��??
        String otherProjectRootPath = properties.getProperty("project.root");
        System.out.println("Other Project Root Path: " + otherProjectRootPath);
        return otherProjectRootPath;     
       
		
		
    }
    
    public static void main(String[] args) throws Exception {
        String appKey = "rfs83bFVLgV9opST";
        String id = "qHBdEsPxGd3409fY";
        String secret = "wasqwfiNX55lQuspubS7E4hvLURcEg";
        String url = ""; //默认值：wss://nls-gateway.cn-shanghai.aliyuncs.com/ws/v1
//        if (args.length == 3) {
//            appKey   = args[0];
//            id       = args[1];
//            secret   = args[2];
//        } else if (args.length == 4) {
//            appKey   = args[0];
//            id       = args[1];
//            secret   = args[2];
//            url      = args[3];
//        } else {
//            System.err.println("run error, need params(url is optional): " + "<app-key> <AccessKeyId> <AccessKeySecret> [url]");
//            System.exit(-1);
//        }
        TTSSpeech demo = new TTSSpeech(appKey, id, secret, url);
        demo.process("打开etop智能协同平台并且进入运营值班�? ");
        demo.shutdown();
    }
}