package luckyclient.publicclass;

import java.util.Map;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author： seagull
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class ChangString {

	public static String changparams(String str,Map<String,String> variable,String changname){
		try{
			if(null==str){
				return null;
			}
			str = str.replaceAll("&quot;", "\"");
			str = str.replaceAll("&#39;", "\'");
			//@@用来注释@的引用作用
			int varcount=counter(str,"@")-counter(str,"@@")*2;
			//如果存在传参，进行处理
			if(varcount>0){
				luckyclient.publicclass.LogUtil.APP.info("在"+changname+"【"+str+"】中找到"+varcount+"个可替换参数");
				int changcount=0;
				//从参数列表中查找匹配变量
				for (Map.Entry<String, String> entry : variable.entrySet()) {
					if(str.indexOf("@"+entry.getKey())>-1){
						if(str.indexOf("@@"+entry.getKey())>-1){
							str=str.replaceAll("@@"+entry.getKey(), "////CHANG////");
						}
						int viewcount=counter(str,"@"+entry.getKey());
						str=str.replaceAll("@"+entry.getKey(), entry.getValue());
						luckyclient.publicclass.LogUtil.APP.info("将"+changname+"引用变量【@"+entry.getKey()+"】替换成值【"+entry.getValue()+"】");
						str=str.replaceAll("////CHANG////","@@"+entry.getKey());
						changcount=changcount+viewcount;
					}
				}
				
				if(varcount!=changcount){
					luckyclient.publicclass.LogUtil.APP.error(changname+"有引用变量未在参数列中找到，请检查！处理结果【"+str+"】");
				}
			}
			str=str.replaceAll("@@","@");
		    return str;
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	public static int counter(String str1,String str2){
		int total = 0;
		for (String tmp = str1; tmp != null&&tmp.length()>=str2.length();){
		  if(tmp.indexOf(str2) == 0){
		    total ++;
		    tmp = tmp.substring(str2.length());
		  }else{
		    tmp = tmp.substring(1);
		  }
		}
		return total;
	}
	
	public static void main(String[] args) {

	}

}
