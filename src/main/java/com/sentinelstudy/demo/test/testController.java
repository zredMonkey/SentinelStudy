package com.sentinelstudy.demo.test;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author: zhouhongzhan
 * @Description:
 * @Date: 2020/9/13 20:41
 **/
public class testController {

    @GetMapping("/test")
    public String test() {
        return "hello sentinel";
    }
}
