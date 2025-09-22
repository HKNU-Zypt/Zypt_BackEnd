package zypt.zyptapiserver.aop.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class LoggingPointcut {

    @Pointcut("execution(* zypt.zyptapiserver..*(..))")
    public void allApplication(){};

    @Pointcut("execution(* zypt.zyptapiserver.util..*(..)) ")
    public void util(){};


    @Pointcut("within(zypt.zyptapiserver..*Source)")
    public void source(){};

    @Pointcut("execution(* zypt.zyptapiserver.aop..*(..))")
    public void Aop(){};




}
