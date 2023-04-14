package luckyclient.tool.playsound;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.netty.handler.codec.http.HttpHeaders;
import org.asynchttpclient.AsyncHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 姝ょず渚嬫紨�?轰簡
 *      1. TTS鐨凴ESTFul鎺ュ彛璋冪敤
 *      2. 鍚敤http chunked鏈哄埗鐨勫鐞嗘柟寮�?(娴佸紡杩斿洖)
 *      3. �?挎枃鏈殑鍒嗘鍚堟垚鍙婃嫾鎺�
 */
public class SpeechSynthesizerRestfulChunkedWithLongTextDemo {
    private static Logger logger = LoggerFactory.getLogger(SpeechSynthesizerRestfulChunkedWithLongTextDemo.class);

    private String accessToken;
    private String appkey;
    int totalSize = 0;

    public SpeechSynthesizerRestfulChunkedWithLongTextDemo(String appkey, String token) {
        this.appkey = appkey;
        this.accessToken = token;
    }

    public void processGETRequet(String text, final FileOutputStream fout, String format, int sampleRate, String voice, boolean chunked) {
        String url = "https://nls-gateway.cn-shanghai.aliyuncs.com/stream/v1/tts";
        url = url + "?appkey=" + appkey;
        url = url + "&token=" + accessToken;
        url = url + "&text=" + text;
        url = url + "&format=" + format;
        url = url + "&voice=" + voice;
        url = url + "&sample_rate=" + String.valueOf(sampleRate);
        url = url + "&chunk=" + String.valueOf(chunked);
        System.out.println("URL: " + url);

        try {
            AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder()
                .setConnectTimeout(5000)
                .setKeepAlive(true)
                .setReadTimeout(10000)
                .setRequestTimeout(50000)
                .setMaxConnections(1000)
                .setMaxConnectionsPerHost(200)
                .setPooledConnectionIdleTimeout(-1)
                .build();
            AsyncHttpClient httpClient = new DefaultAsyncHttpClient(config);

            final CountDownLatch latch = new CountDownLatch(1);
            AsyncHandler<org.asynchttpclient.Response> handler = new AsyncHandler<org.asynchttpclient.Response>() {
                boolean firstRecvBinary = true;
                long startTime = System.currentTimeMillis();
                int httpCode = 200;

                @Override
                public State onStatusReceived(HttpResponseStatus httpResponseStatus) throws Exception {
                    logger.info("onStatusReceived status {}", httpResponseStatus);
                    httpCode = httpResponseStatus.getStatusCode();
                    if (httpResponseStatus.getStatusCode() != 200) {
                        logger.error("request error " +  httpResponseStatus.toString());
                    }
                    return null;
                }

                @Override
                public State onHeadersReceived(HttpHeaders httpHeaders) throws Exception {
                    return null;
                }

                @Override
                public State onBodyPartReceived(HttpResponseBodyPart httpResponseBodyPart) throws Exception {
                    // TODO 閲嶈鎻愮ず锛氭澶勪竴鏃︽帴鏀跺埌鏁版嵁娴侊紝鍗冲彲鍚戠敤鎴锋挱鏀炬垨鑰呯敤浜庡叾浠栧鐞嗭紝浠ユ彁鍗囧搷搴旈�熷�?
                    // TODO 閲嶈鎻愮ず锛氳涓嶈鍦ㄦ鍥炶皟鎺ュ彛涓墽琛岃�楁椂鎿嶄綔锛屽彲浠ヤ互寮傛鎴栬�呴槦鍒楀舰寮忓皢浜岃繘鍒禩TS璇煶娴佹帹閫佸埌鍙︿竴绾跨▼涓�?
                    if(httpCode != 200) {
                        System.err.write(httpResponseBodyPart.getBodyPartBytes());
                    }

                    if (firstRecvBinary) {
                        firstRecvBinary = false;
                        // TODO 缁熻绗竴鍖呮暟鎹殑鎺ユ敹寤惰繜锛屽疄闄呬笂鎺ユ敹鍒扮涓�鍖呮暟鎹悗灏卞彲浠ヨ繘琛屼笟鍔″鐞嗕簡锛屾瘮濡傛挱鏀炬垨鑰呭彂閫佺粰璋冪敤鏂癸紝娉ㄦ剰锛氳繖閲岀殑棣栧寘寤惰繜涔熷寘鎷簡缃戠粶寤虹珛閾炬帴鐨勬椂闂�?
                        logger.info("tts first latency " + (System.currentTimeMillis() - startTime) + " ms");
                    }
                    // TODO 閲嶈鎻愮ず锛氭澶勪粎涓轰妇渚嬶紝灏嗚闊虫祦淇濆瓨鍒版枃浠朵�?
                    fout.write(httpResponseBodyPart.getBodyPartBytes());
                    totalSize += httpResponseBodyPart.getBodyPartBytes().length;
                    return null;
                }

                @Override
                public void onThrowable(Throwable throwable) {
                    logger.error("throwable {}", throwable);
                    latch.countDown();
                }

                @Override
                public org.asynchttpclient.Response onCompleted() throws Exception {
                    logger.info("tts total latency " + (System.currentTimeMillis() - startTime) + " ms");
                    latch.countDown();
                    return null;
                }
            };

            httpClient.prepareGet(url).execute(handler);
            // 绛夊緟鍚堟垚瀹屾�?
            latch.await();
            httpClient.close();
        }catch (Exception e) {
        }
    }

    /**
     * 灏嗛暱鏂囨湰鍒囧垎涓烘瘡鍙ュ瓧鏁颁笉澶т簬size鏁扮洰鐨勭煭鍙�
     * @param text
     * @param size
     * @return
     */
    public static List<String> splitLongText(String text, int size) {
        //鍏堟寜鏍囩偣绗﹀彿鍒囧垎
        String[] texts = text.split("[銆侊紝銆傦紱锛燂�?,!\\?]");
        StringBuilder textPart = new StringBuilder();
        List<String> result = new ArrayList<String>();
        int len = 0;
        //鍐嶆寜size merge,閬垮厤鏍囩偣绗﹀彿鍒囧垎鍑烘潵鐨勫お鐭�
        for (int i = 0; i < texts.length; i++) {
            if (textPart.length() + texts[i].length() + 1 > size) {
                result.add(textPart.toString());
                textPart.delete(0, textPart.length());

            }
            textPart.append(texts[i]);
            len += texts[i].length();
            if(len<text.length()){
                //System.out.println("at " + text.charAt(len));
                textPart.append(text.charAt(len));
                len += 1;
            }

        }
        if (textPart.length() > 0) {
            result.add(textPart.toString());
        }

        return result;

    }

    public void process(final String longText, final FileOutputStream fout) {
        List<String> textArr = splitLongText(longText, 100);
        try {
            for (int i = 0; i < textArr.size(); i++) {
                //璁剧疆鐢ㄤ簬璇煶鍚堟垚鐨勬枃鏈�?
                String text = textArr.get(i);
                System.out.println("try process [" + text + "]");
                // 閲囩敤RFC 3986瑙勮寖杩涜urlencode缂栫�?
                String textUrlEncode = text;
                try {
                    textUrlEncode = URLEncoder.encode(textUrlEncode, "UTF-8")
                        .replace("+", "%20")
                        .replace("*", "%2A")
                        .replace("%7E", "~");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println(textUrlEncode);
                // TODO 璇存�? 鏈�鍚庝竴涓弬鏁颁负true琛ㄧず浣跨敤http chunked鏈哄�?
                processGETRequet(textUrlEncode, fout, "pcm", 16000, "siyue", true);
                System.out.println("==== " + totalSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length < 2) {
            System.err.println("SpeechSynthesizerRestfulDemo need params: <token> <app-key>");
            System.exit(-1);
        }
        String token = args[0];
        String appkey = args[1];

        String ttsTextLong = "鐧捐崏鍫備笌涓夊懗涔�?�? 椴佽�? \n" +
            "鎴戝鐨勫悗闈㈡湁涓�涓緢澶х殑鍥紝鐩镐紶鍙綔鐧捐崏鍥�傜幇鍦ㄦ槸鏃╁凡骞跺眿瀛愪竴璧峰崠缁欐湵鏂囧叕鐨勫瓙�?�欎簡锛岃繛閭ｆ渶鏈鐨勭浉瑙佷篃宸茬粡闅斾簡涓冨叓骞达紝鍏朵腑浼间箮纭嚳鍙湁涓�浜涢噹鑽夛紱浣嗛偅鏃跺嵈鏄垜鐨勪箰鍥�俓n" +
            "涓嶅�?璇寸ⅶ缁跨殑鑿滅暒锛屽厜婊戠殑鐭充簳鏍忥紝楂樺ぇ鐨勭殏鑽氭爲锛岀传绾㈢殑妗戣憵锛涗篃涓嶅�?璇撮福铦夊湪鏍戝彾閲岄暱鍚燂紝鑲ヨ儢鐨勯粍铚備紡鍦ㄨ彍鑺变笂锛岃交鎹风殑鍙ぉ�?��(浜戦�?)蹇界劧浠庤崏闂寸洿绐滃悜浜戦渼閲屽幓浜嗐�俓n" +
            "鍗曟槸鍛ㄥ洿鐨勭煭鐭殑娉ュ鏍逛竴甯︼紝灏辨湁鏃犻檺瓒ｅ懗銆傛补铔夊湪杩欓噷浣庡敱锛岃煁锜�浠�?湪杩欓噷寮圭惔銆傜炕寮�鏂爾鏉ワ紝鏈夋椂浼氶亣瑙佽�?铓ｏ紱杩樻湁鏂戣潵锛屽�樿嫢鐢ㄦ墜鎸囨寜浣忓畠鐨勮剨姊侊紝渚夸細鍟殑涓�澹帮紝\n" +
            "浠庡悗绐嶅柗鍑轰竴闃电儫闆俱�備綍棣栦箤钘ゅ拰鏈ㄨ幉钘ょ紶缁滅潃锛屾湪鑾叉湁鑾叉埧涓�鑸殑鏋滃疄锛屼綍棣栦箤鏈夎噧鑲跨殑鏍广�傛湁浜鸿锛屼綍棣栦箤鏍规槸鏈夊儚浜哄舰鐨勶紝鍚冧簡渚垮彲浠ユ垚浠欙紝鎴戜簬鏄父甯告嫈瀹冭捣鏉ワ紝鐗佃繛涓嶆柇鍦版嫈璧锋潵锛孿n" +
            "涔熸浘鍥犳寮勫潖浜嗘偿澧欙紝鍗翠粠鏉ユ病鏈夎杩囨湁涓�鍧楁牴鍍忎汉鏍�?! 濡傛灉涓嶆�曞埡锛岃繕鍙互鎽樺埌瑕嗙泦瀛愶紝鍍忓皬鐝婄憵鐝犳敀鎴愮殑灏忕悆锛屽張閰稿張鐢滐紝鑹插懗閮芥瘮妗戣憵瑕佸ソ寰楄繙......";

        try {
            String path = "longText4TTS4Restful.wav";
            File out = new File(path);
            FileOutputStream fout = new FileOutputStream(out);

            // 鍒濇湡骞朵笉鐭ラ亾wav鏂囦欢�?�為檯闀垮害锛屽亣璁句负0锛屾渶鍚庡啀鏍℃�?
            int pcmSize = 0;
            WavHeader header = new WavHeader();
            // �?垮害瀛楁�? = 鍐呭鐨勫ぇ灏忥紙PCMSize) + 澶撮儴�?�楁鐨勫ぇ灏�(涓嶅寘鎷�?墠闈�?4瀛楄妭鐨勬爣璇嗙RIFF浠ュ強fileLength鏈韩鐨�?4瀛楄�?)
            header.fileLength = pcmSize + (44 - 8);
            header.fmtHdrLeth = 16;
            header.bitsPerSample = 16;
            header.channels = 1;
            header.formatTag = 0x0001;
            header.samplesPerSec = 16000;
            header.blockAlign = (short) (header.channels * header.bitsPerSample / 8);
            header.avgBytesPerSec = header.blockAlign * header.samplesPerSec;
            header.dataHdrLeth = pcmSize;
            byte[] h = header.getHeader();
            assert h.length == 44;
            // TODO 璇存槑锛�? 鍏堝啓鍏�?44瀛楄妭鐨剋av澶达紝濡傛灉鍚堟垚鐨勪笉鏄痺av锛屾瘮濡傛槸pcm锛屽垯涓嶉渶瑕佹姝ラ�??
            fout.write(h);

            SpeechSynthesizerRestfulChunkedWithLongTextDemo demo = new SpeechSynthesizerRestfulChunkedWithLongTextDemo(appkey, token);
            demo.process(ttsTextLong, fout);

            // TODO 璇存槑锛�? 鏇存�?44瀛楄妭鐨剋av澶达紝濡傛灉鍚堟垚鐨勪笉鏄痺av锛屾瘮濡傛槸pcm锛屽垯涓嶉渶瑕佹姝ラ�??
            RandomAccessFile wavFile = new RandomAccessFile(path, "rw");
            int fileLength = (int)wavFile.length();
            int dataSize = fileLength - 44;
            System.out.println("filelength = " + fileLength +", datasize = " + dataSize);
            header.fileLength = fileLength - 8;
            header.dataHdrLeth = fileLength - 44;
            wavFile.write(header.getHeader());
            wavFile.close();

            System.out.println("### Game Over ###");
        }catch (IOException e) {

        }
    }
}