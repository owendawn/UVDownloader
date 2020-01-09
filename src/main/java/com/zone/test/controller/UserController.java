package com.zone.test.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zone.test.base.common.JsonPageResult;
import com.zone.test.entity.User;
import com.zone.test.service.UserService;
import com.zone.test.base.common.BaseController;
import com.zone.test.base.common.JsonResult;
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
    public String login(@RequestParam(value = "name", defaultValue = "") String name, ModelMap modelMap) {
        if ("".equals(name)) {
            modelMap.put("returnMsg", "用户名为空");
        } else {
            logger.info("the param name value is :" + name);
            List<HashMap<String, Object>> list = userService.getUser(name);
            if (list!=null&&list.size() > 0) {
                modelMap.put("returnMsg", "用户存在，跳转主页");
                return "/index2";
            } else {
                modelMap.put("returnMsg", "用户名不存在");
            }
        }
        return "/login";
    }

    @PostMapping( value = "/sayHi")
    @ResponseBody
    public HashMap sayHi(){
        List<String> re=userService.getCount();
        List<User> re2=userService.getCount2();
        IPage<User> table=userService.queryUser(new Page<>(2,2));
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("success",true);
        hashMap.put("msg",helloInteger+"、哈哈 I'm fine, Thank You!\n" +
                " and what's more, checked for toggling Datasource and paging");
        hashMap.put("master-ds-com",re);
        hashMap.put("cluster-ds-com",re2);
        hashMap.put("table",new JsonPageResult.Builder<>().page(table).data(table.getRecords()).build());
        hashMap.put("table2",userService.queryUser2(2,2));
        return hashMap;
    }

    @GetMapping("/testTransaction")
    @ResponseBody
    public JsonResult<Object> testTransaction(){
        userService.testTransactional("helloMonster");
        return new JsonResult.Builder<>().build();

    }
}