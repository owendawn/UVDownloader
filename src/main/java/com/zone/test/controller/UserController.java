package com.zone.test.controller;

import com.zone.test.base.common.BaseController;
import com.zone.test.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private Integer helloInteger;
    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping("/login")
    public String login(@RequestParam(value = "name", defaultValue = "") String name, @RequestParam(value = "logined", defaultValue = "false") boolean logined, ModelMap modelMap) {
        if (name.equals("")) {
            modelMap.put("returnMsg", "用户名为空");
        } else {
            logger.info("the param logined value is :" + logined);
            List<HashMap<String, Object>> list = userService.checkUserExists(name);
            System.out.println(name);
            if (list!=null&&list.size() > 0) {
                modelMap.put("returnMsg", "用户存在，跳转主页");
                return "/index2";
            } else {
                modelMap.put("returnMsg", "用户名不存在");
            }
        }
        return "/login";
    }

//    @RequestMapping(value = "/sayHi",method = RequestMethod.POST)
    @PostMapping("/sayHi")
    @ResponseBody
    public HashMap sayHi(){
        Integer re=userService.getCount();
        HashMap hashMap=new HashMap();
        hashMap.put("success",true);
        hashMap.put("msg",helloInteger+"、I'm fine, Thank You!");
        return hashMap;
    }

//    @RequestMapping(value = "/testTransaction",method = RequestMethod.GET)
    @GetMapping("/testTransaction")
    @ResponseBody
    public HashMap testTransaction(){
        String errType="";
        try {
            //测试事务
            userService.testTransactional("helloMonster");
        }catch (Exception e){
            errType=e.getMessage();
            logger.error(e.getMessage(),e);
        }
        HashMap hashMap=new HashMap();
        hashMap.put("success",true);
        hashMap.put("msg","修改用户id为2的用户的密码为helloMonster，中途抛出异常("+errType+")，请查看结果");
        return hashMap;
    }
}