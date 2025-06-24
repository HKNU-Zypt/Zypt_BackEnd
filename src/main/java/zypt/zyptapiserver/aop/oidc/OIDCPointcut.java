package zypt.zyptapiserver.aop.oidc;

import org.aspectj.lang.annotation.Pointcut;

public class OIDCPointcut {

    @Pointcut("execution(public * *.oidc.getPublicKeyByKid(..))")
    public void oidc() {}

    @Pointcut("target(zypt.zyptapiserver.auth.service.SocialService)")
    public void getUserInfo() {}

}
