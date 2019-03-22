import com.tiza.plugin.util.CommonUtil;
import com.tiza.plugin.util.DateUtil;
import com.tiza.plugin.util.JacksonUtil;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: TestMain
 * Author: DIYILIU
 * Update: 2018-12-06 14:36
 */
public class TestMain {


    @Test
    public void test(){
        String str = "181206141226";

        Date date = new Date(CommonUtil.parseBCDTime(CommonUtil.hexStringToBytes(str)));
        System.out.println(date);

        System.out.println(DateUtil.dateToString(date));
    }

    @Test
    public void test1(){
        int i = 171;

        System.out.println(CommonUtil.keepDecimal(i, 0.1, 1));
    }

    @Test
    public void test2(){
        String str = "68 00 68 07 1A 02 18 51 46 4C 59 30 31 30 30 30 30 30 30 30 30 30 30 30 30 30 33 32 74 6C 33";
        byte[] bytes = CommonUtil.hexStringToBytes(str);

        System.out.println(String.format("%02X", CommonUtil.sumCheck(bytes)));
    }

    @Test
    public void test3(){
        String str = "68 00 68 3c 05 27 33 26 c3 01";
        byte[] bytes = CommonUtil.hexStringToBytes(str);


        System.out.println(bytes.length);

        System.out.println(String.format("%02X", CommonUtil.sumCheck(bytes)));
    }

    @Test
    public void test4(){

        String str = "4C 56 39 30 42 57 37 53 37 4A 32 4C 44 4B 33 32 30".replaceAll(" ", "");
        System.out.println(str);
        byte[] bytes = CommonUtil.hexStringToBytes(str);
        System.out.println(bytes.length);

        System.out.println(new String(bytes));
    }

    @Test
    public void test5() {
        Map map = new HashMap() {
            {
                this.put("a", 123);
            }
        };

        String str = JacksonUtil.toJson(map);
        System.out.println(str);
    }
}
