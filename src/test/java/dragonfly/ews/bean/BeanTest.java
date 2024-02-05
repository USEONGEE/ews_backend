package dragonfly.ews.bean;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionService;

@SpringBootTest
public class BeanTest {
    @Autowired
    ApplicationContext ac;

    @Autowired
    ConversionService convertor;

    @Test
    void searchAllBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = ac.getBean(beanDefinitionName);
            System.out.println("bean=" + bean.getClass().toString());
        }
    }

    @Test
    void searchFormatter() {
        System.out.println("convertor=" + convertor.getClass().toString());
    }
}
