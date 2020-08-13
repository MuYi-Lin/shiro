package org.muyi.chapter04.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.muyi.chapter04.config.JwtToken;
import org.muyi.chapter04.dao.UserRepository;
import org.muyi.chapter04.entity.Result;
import org.muyi.chapter04.entity.User;
import org.muyi.chapter04.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    private UserRepository UserService;

    @RequiresPermissions(value = "user")
    @RequestMapping("/user")
    public String getOne(){
        return "user";
    }

    @RequiresPermissions(value = "admin")
    @RequestMapping("/admin")
    public String getTwo(){
        return "admin";
    }

    @RequestMapping("/doLogin")
    @ResponseBody
    public Result doLogin(@RequestBody User user, HttpServletResponse response){
        //获取subject
        Subject subject = SecurityUtils.getSubject();

        String token = JWTUtil.sign(user.getName(), user.getPassword());
        //封装用户数据
        AuthenticationToken userToken= new JwtToken(token);
        //执行登录方法,用捕捉异常去判断是否登录成功
        try {
            //经token放在请求头里面
            response.setHeader("Authorization", token);
            subject.login(userToken);
            return  new Result(true, "登录成功", null);
        }catch (AuthenticationException e){
            return  new Result(false, e.getMessage(), null);
        }
    }

}
