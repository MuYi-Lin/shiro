package org.muyi.chapter04.config;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.muyi.chapter04.dao.UserRepository;
import org.muyi.chapter04.entity.User;
import org.muyi.chapter04.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShiroRealm extends AuthorizingRealm {
    @Autowired
    UserRepository userRepository;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     * @param auth
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth)
            throws AuthenticationException {
        String token = (String)auth.getPrincipal();
        String account  = JWTUtil.getUsername(token);

        if (account == null) {
            throw new AuthenticationException("token invalid");
        }
        User user = userRepository.getByName(account);

        if(user == null){
            throw new UnknownAccountException("用户不存在");
        }
        if (JWTUtil.verify(token,user.getName(),user.getPassword())) {
            return new SimpleAuthenticationInfo(token, token, "shiroRealm");
        }
        throw new AuthenticationException("用户密码错误");
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("授权");
        String account  = JWTUtil.getUsername(principalCollection.toString());
        User user = userRepository.getByName(account);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addStringPermission(user.getPerms());
        return simpleAuthorizationInfo;
    }

    /*@Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("认证");
        String token = (String) authenticationToken.getPrincipal();
        String username = null;

        try {
            username = JWTUtil.getUsername(token);
        }catch (Exception e){
            throw new AuthenticationException("heard的token拼写错误或者值为空");
        }
        if(username == null){
            throw new AuthenticationException("token无效");
        }

        User user = userRepository.getByName(username);

        if(user == null){
            throw new UnknownAccountException("用户不存在");
        }
        boolean result = JWTUtil.verify(token,username,user.getPassword());
        if(!result){
            throw  new IncorrectCredentialsException("用户密码错误");
        }
        return new SimpleAuthenticationInfo(user,token,"shiroRealm");
    }*/
}
