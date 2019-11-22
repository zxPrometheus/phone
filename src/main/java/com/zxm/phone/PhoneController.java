package com.zxm.phone;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.Random;

@RestController
public class PhoneController {
    @RequestMapping("/phone")
    public String getCode(String phone) {
        if(phone==null) {
            return "";
        }


        // 创建jedis对象，建立与redis的链接
        Jedis jedis = new Jedis("192.168.222.130", 6379);
        // 将前端获取的电话号码做成一个前缀为phone_num的字符串，当redis里面的key
        String phoneKey = "phone_num" + phone;

        // 生成四位的验证码
        String verifyCode = genCode(5);
        // 将拼写到的Steing类型的key放入redis,将后台生成的随机5位数验证码作value设置销毁时间为20秒
        jedis.setex(phoneKey, 20, verifyCode);
        // 关闭redis流
        jedis.close();

        // 如果成功将数据返回到前端Data
        return "success";

    }

    private String genCode(int code_length) {
        String code = "";
        for (int i = 0; i < code_length; i++) {
            int num = new Random().nextInt(10);
            code += num;
        }
        return code;
    }

    @RequestMapping("/verificode")
    // 将前端的电话号码和用户输入的验证码传进来
    public String verificode(String phone,String verify_code) {
        // 判断验证码是否为空
        if(verify_code==null){
            return "error";
        }
        // 建立链接
        Jedis jedis = new Jedis("192.168.222.130", 6379);
        String phoneCode = jedis.get("phone_num:"+phone);
        // phonCode不能放在前面，会导致空指针异常
        if (verify_code.equals(phoneCode)) {
            return "success";
        }
        // 关流
        jedis.close();

        return "error";
    }
}
