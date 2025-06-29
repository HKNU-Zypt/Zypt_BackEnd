package zypt.zyptapiserver.aop.oidc;

import org.aspectj.lang.annotation.Pointcut;

public class OIDCPointcut {

    @Pointcut("execution(public * *.oidc.getPublicKeyByKid(..))")
    public void oidc() {}

    @Pointcut("execution(* zypt.zyptapiserver.auth.service.SocialService.getUserInfo(..))")
    public void getUserInfo() {}


}
