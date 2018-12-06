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
}
