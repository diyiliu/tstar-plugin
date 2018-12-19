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

    @Test
    public void test3(){
        String str = "02 00 00 7d 02 01 81 70 21 80 74 00 08 00 00 00 00 00 00 00 03 01 ae 93 8a 06 ba 81 04 00 00 00 00 00 8e 18 12 18 11 20 52 30 01 1a 31 01 06 d0 04 00 00 00 00 d4 01 3c e1 02 01 83 e2 21 54 5a 43 53 2d 31 2e 31 33 2e 31 30 39 2e 34 20 31 37 2d 31 32 2d 32 38 2c 4d 54 30 33 5f 50 31 30 e3 0f 34 36 30 30 32 31 39 35 38 33 34 34 34 37 32 e4 13 01 cc 00 00 74 7c f6 07 33 73 a1 29 36 1f 74 7c a8 40 15 e5 04 01 2c";
        byte[] bytes = CommonUtil.hexStringToBytes(str);


        System.out.println(bytes.length);

        System.out.println(CommonUtil.getCheck(bytes));
    }
}
