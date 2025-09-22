package zypt.zyptapiserver.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    private static final ThreadLocal<Stack<String>> processLog = ThreadLocal.withInitial(Stack::new);
    private static final ThreadLocal<Boolean> isException = ThreadLocal.withInitial(() -> false); // 예외를 한 번만 출력하기 위한 용도


    @Around("LoggingPointcut.allApplication() && !LoggingPointcut.source() && !LoggingPointcut.util() && !LoggingPointcut.Aop()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Stack<String> stack = processLog.get();

        // 현재 메소드 push
        stack.push(joinPoint.getSignature().toShortString());
        int depth = stack.size();

        log.debug("{}|--> {} ", indent(depth), joinPoint.getSignature().toShortString());

        try {
            Object result = joinPoint.proceed();
            long time = System.currentTimeMillis() - start;
            log.debug("{}|<-- {} {}ms", indent(depth), joinPoint.getSignature().toShortString(), time);

            return result;
        } catch (Exception e) {
            Boolean alreadyEx = isException.get();
            if (!alreadyEx) {
                log.error("{}|--> {} |--> X   EX : {}", indent(depth), joinPoint.getSignature().toShortString(), e.getMessage(), e);
                isException.set(true);
            }
            throw e;
        } finally {
            // 스택에서 pop
            stack.pop();
            if (stack.isEmpty()) {
                processLog.remove();
                isException.remove();
            }
        }
    }

    // depth 만큼 들여쓰기
    private String indent(int depth) {
        return "    ".repeat(Math.max(0, depth - 1));
    }
}