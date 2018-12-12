import com.tiza.plugin.util.CommonUtil;
import com.tiza.plugin.util.DateUtil;
import org.junit.Test;

import java.util.Date;

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
        String str = "68 00 68 04 13 01 11 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 36 37";
        byte[] bytes = CommonUtil.hexStringToBytes(str);

        System.out.println(CommonUtil.sumCheck(bytes));


        str = "72";
        bytes = CommonUtil.hexStringToBytes(str);
        System.out.println(bytes[0]);
    }
}
