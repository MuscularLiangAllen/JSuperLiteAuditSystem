package com.liangtee.jsuperlite.auditsys.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.bingoohuang.patchca.color.SingleColorFactory;
import com.github.bingoohuang.patchca.custom.ConfigurableCaptchaService;
import com.github.bingoohuang.patchca.filter.predefined.CurvesRippleFilterFactory;
import com.github.bingoohuang.patchca.font.RandomFontFactory;
import com.github.bingoohuang.patchca.utils.encoder.EncoderHelper;
import com.github.bingoohuang.patchca.word.RandomWordFactory;
import com.liangtee.jsuperlite.auditsys.model.User;
import com.liangtee.jsuperlite.auditsys.service.UserService;
import com.liangtee.jsuperlite.auditsys.utils.InjectionFilter;
import com.liangtee.jsuperlite.auditsys.utils.MD5Encoder;
import com.liangtee.jsuperlite.auditsys.values.json.ReturnMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.OutputStream;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/4/4.
 * All Rights Reserved
 */

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String showLoginPage(HttpServletRequest request) {
        if(request.getSession().getAttribute("user") != null)
            return "modules/framework";

        return "login";
    }

    @RequestMapping(path = "/login/validate", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String validate(@RequestParam(value = "userName", required = true) String userName,
                                         @RequestParam(value = "passwd", required = true) String passwd,
                                         @RequestParam(value = "inputCaptcha", required = true)String inputCaptcha,
                                         HttpServletRequest request) {

        if(InjectionFilter.filter(userName, passwd, inputCaptcha)) return JSON.toJSONString(new ReturnMessage("拒绝访问"));

        String captcha = (String) request.getSession().getAttribute("captcha");
        if(!captcha.equalsIgnoreCase(inputCaptcha)) return JSON.toJSONString(new ReturnMessage("验证码错误"));

        if(!userService.isExist("USER_NAME = ? AND PASSWD = ?",
                userName, MD5Encoder.get2RoundsHash(passwd, "MD5")))
            return JSON.toJSONString(new ReturnMessage("用户名或密码错误"));

        User user = userService.get(userName, MD5Encoder.get2RoundsHash(passwd, "MD5"));
        if(!user.isActive())
            return JSON.toJSONString(new ReturnMessage("此账户已被禁用, 重新使用请联系管理员"));

        request.getSession().setAttribute("user", user);

        return JSON.toJSONString(new ReturnMessage("PASS"));
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return "redirect:/";
    }


    @RequestMapping(path = "/get/captcha", method = RequestMethod.GET)
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {

        RandomFontFactory fontFactory = new RandomFontFactory();
        fontFactory.setMaxSize(30);
        fontFactory.setMinSize(26);

        RandomWordFactory wordFactory = new RandomWordFactory();
        wordFactory.setCharacters("abcdefghkmnpqstwxyz023456789");
        wordFactory.setMaxLength(5);
        wordFactory.setMinLength(4);

        ConfigurableCaptchaService cs = new ConfigurableCaptchaService();
        cs.setFontFactory(fontFactory);
        cs.setWordFactory(wordFactory);
        cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)));
        cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));
        cs.setWidth(140);
        cs.setHeight(40);

        OutputStream os = null;
        String captcha = null;

        try {
            HttpSession session = request.getSession();
            os = response.getOutputStream();
            captcha = EncoderHelper.getChallangeAndWriteImage(cs, "png", os);
            session.setAttribute("captcha", captcha);

            response.setContentType("image/png");
            response.setHeader("cache", "no-cache");

            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
