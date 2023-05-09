package luckyclient.tool.playsound;
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;

public class TokenDemo {    
    private static final String REGIONID = "cn-shanghai";   
    private static final String DOMAIN = "nls-meta.cn-shanghai.aliyuncs.com";    
    private static final String API_VERSION = "2019-02-28";    
    private static final String REQUEST_ACTION = "CreateToken";    
    private static final String KEY_TOKEN = "Token";
    private static final String KEY_ID = "Id";
    private static final String KEY_EXPIRETIME = "ExpireTime";
    public static String getToken() throws ClientException {  
    	String token=null;
        String accessKeyId = "qHBdEsPxGd3409fY";
        String accessKeySecret = "wasqwfiNX55lQuspubS7E4hvLURcEg";
        
        DefaultProfile profile = DefaultProfile.getProfile(
            REGIONID,
            accessKeyId,
            accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setDomain(DOMAIN);
        request.setVersion(API_VERSION);
        request.setAction(REQUEST_ACTION);
        request.setMethod(MethodType.POST);
        request.setProtocol(ProtocolType.HTTPS);     
        
        CommonResponse response = client.getCommonResponse(request);
        System.out.println(response.getData());
        if (response.getHttpStatus() == 200) {
            JSONObject result = JSON.parseObject(response.getData());
            token = result.getJSONObject(KEY_TOKEN).getString(KEY_ID);
            long expireTime = result.getJSONObject(KEY_TOKEN).getLongValue(KEY_EXPIRETIME);
            System.out.println("Token: " + token + "过期时间: " + expireTime);
           
            String expireDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(expireTime * 1000));
            System.out.println("Token过期时间" + expireDate);
        }
        else {
            System.out.println("Can't get the token successfully?");
        }
		
		return token;
    }
}
