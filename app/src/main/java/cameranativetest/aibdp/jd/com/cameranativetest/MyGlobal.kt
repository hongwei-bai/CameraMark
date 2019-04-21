package cameranativetest.aibdp.jd.com.cameranativetest

/**
 * Package: cameranativetest.aibdp.jd.com.cameranativetest
 * User: baihongwei1
 * Email: baihongwei1@jd.com
 * Date: 2018/12/6
 * Time: 12:04
 * Description:
 */

fun <T1, T2> ifNotNull(value1: T1?, value2: T2?, bothNotNull: (T1, T2) -> (Unit)) {
    if (value1 != null && value2 != null) {
        bothNotNull(value1, value2)
    }
}