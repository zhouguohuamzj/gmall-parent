import org.springframework.util.StringUtils;

/**
 * @description:
 * @title: Test1
 * @Author coderZGH
 * @Date: 2022/11/10 20:57
 * @Version 1.0
 */
public class Test1 {


    public static void main(String[] args) {

        String props = "106:安卓手机:手机系统";
        String[] split1 = org.apache.commons.lang3.StringUtils.split(props, ":");
        String[] split = StringUtils.split(props, ":");
        for (String s : split1) {
            System.out.println(s);
        }
    }
}