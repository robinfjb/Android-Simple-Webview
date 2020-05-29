package robin.scaffold.lib.util;

import android.text.TextUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MathUtils {
    public static Map<String, Pattern> patternHashMap = new HashMap<String, Pattern>();
    /**
     * 根据正则表达式和所给str查找匹配到的字符串,多次匹配，无group
     * 例如str=“21sdf14fswer51fdf” pattern="\\d+(\\.\\d+)?"匹配数字得结果[21,14,51]
     * @param str
     * @param pattern
     * @return
     */
    public static String[] getMatchStr(String str, String pattern) {
        if(TextUtils.isEmpty(str) || TextUtils.isEmpty(pattern)){
            return null;
        }
        String result[] = null;
        Pattern p = patternHashMap.get(pattern);
        if(p == null){
            p = Pattern.compile(pattern);
            patternHashMap.put(pattern, p);
        }
        Matcher m = p.matcher(str);

        List<String> tempList = new ArrayList<String>();
        while (m.find()) {
            tempList.add(m.group());
        }

        if(tempList.size() > 0){
            result = new String[tempList.size()];
            for(int i = 0;i < tempList.size();i++){
                result[i] = tempList.get(i);
            }
        }

        return result;
    }

    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
        }
        return "";
    }
}
