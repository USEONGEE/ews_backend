package dragonfly.ews.common.security.postprocessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationFilterBeanPostProcessor implements BeanPostProcessor {
    /**
     * [Authorization 에러 필터 해제]
     * <p/>
     * [Version] Spring Boot 3.2.2 && Spring Security 6.2.2
     * <br/>
     * 현재 버전에서
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AuthorizationFilter authorizationFilter) {
            authorizationFilter.setFilterErrorDispatch(false);
        }
        return bean;
    }

}
