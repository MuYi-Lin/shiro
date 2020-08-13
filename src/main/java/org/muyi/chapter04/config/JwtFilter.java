package org.muyi.chapter04.config;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.muyi.chapter04.entity.Result;
import org.muyi.chapter04.util.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JwtFilter extends BasicHttpAuthenticationFilter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        System.err.println("preHandle");
        return super.preHandle(request, response);
    }

    /**
     * 检测Header里Authorization字段
     * 判断是否登录
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        return authorization != null;
    }

    /**
     * 登录验证
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response){
//        logger.info("调用executeLogin验证登录");

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader("Authorization");

        JwtToken token = new JwtToken(authorization);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(token);

        //绑定上下文获取账号
        String account = JWTUtil.getUsername(authorization);

//        //绑定上下文
//        new UserContext(new LoginUser(account, null));

        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * 是否允许访问
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            try {
                this.executeLogin(request, response);
                return true;
            } catch (Exception e) {
                String msg = e.getMessage();
                Throwable throwable = e.getCause();
                if (throwable != null && throwable instanceof SignatureVerificationException) {
                    msg = String.format("Token或者密钥不正确(%s)",throwable.getMessage());
                } else if (throwable != null && throwable instanceof TokenExpiredException) {
                    msg = String.format("Token已过期(%s)",throwable.getMessage());
                } else {
                    if (throwable != null) {
                        msg = throwable.getMessage();
                    }
                }
                this.response401(response, msg);
                return false;
            }
        }else{
            return false;
        }
    }

    /**
     * 重写 onAccessDenied 方法，避免父类中调用再次executeLogin
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
//        logger.info("调用onAccessDenied拒绝访问");
        this.sendChallenge(request, response);
        return false;
    }

    /**
     * 401非法请求
     * @param resp
     * @param msg
     */
    private void response401(ServletResponse resp, String msg) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = httpServletResponse.getWriter();

            Result result = new Result();
            result.setResult(false);
            result.setCode(40001);
            result.setMessage(msg);
            out.append(JSON.toJSONString(result));
        } catch (IOException e) {
            logger.error("返回Response信息出现IOException异常:" + e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
