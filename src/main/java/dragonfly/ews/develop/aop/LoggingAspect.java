package dragonfly.ews.develop.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @Around("@annotation(LogMethodParams)")
    public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString(); // 메소드 이름과 파라미터 타입을 포함합니다.
        Object[] args = joinPoint.getArgs(); // 메소드에 전달된 파라미터를 가져옵니다.

        StringBuilder params = new StringBuilder();
        for (Object arg : args) {
            params.append(arg.getClass().getSimpleName()).append("=");
            if (arg instanceof String || arg.getClass().isPrimitive()) {
                params.append(arg).append(", ");
            } else {
                // 참조형 객체의 경우, Reflection을 사용하여 필드 값을 로깅할 수 있습니다.
                // 여기서는 간단히 toString()을 호출합니다. 필요에 따라 구체적인 필드 값을 출력하도록 수정할 수 있습니다.
                params.append(arg.toString()).append(", ");
            }
        }

        // 로깅 전에 메소드 실행
        Object result = joinPoint.proceed();

        // 메소드 호출 정보 로깅
        log.info("Called: {} with params: [{}]", methodName, params.toString());

        return result; // 메소드 실행 결과 반환
    }
}
