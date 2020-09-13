package com.sentinelstudy.demo.resource;

import com.alibaba.csp.sentinel.SphO;

/**
 * 返回布尔值方式定义资源
 * @Description:
 * @Date: 2020/9/13 21:46
 **/
public class resourceDefine1 {

    /**
    *
    * @Description:
    *     SphO 提供 if-else 风格的 API。用这种方式，当资源发生了限流之后会返回 false，这个时候可以根据返回值，进行限流之后的逻辑处理
    * @Date: 2020/9/13 21:47
    **/
    public void test1() {
        // 资源名可使用任意有业务语义的字符串
        if (SphO.entry("自定义资源名")) {
            // 务必保证finally会被执行
            try {
                /**
                 * 被保护的业务逻辑
                 */
            } finally {
                SphO.exit();
            }
        } else {
            // 资源访问阻止，被限流或被降级
            // 进行相应的处理操作
        }
    }
}
