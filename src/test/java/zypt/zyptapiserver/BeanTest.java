package zypt.zyptapiserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
public class BeanTest {

    @Autowired
    ApplicationContext context;

    @Test
    void check() {
        Object bean = context.getBean("objectMapper");
        Assertions.assertThat(bean).isInstanceOf(ObjectMapper.class);
    }

    @Test
    void checkByType() {
        // 이름 대신 타입으로 빈이 존재하는지 확인
        ObjectMapper bean = context.getBean(ObjectMapper.class);
        Assertions.assertThat(bean).isNotNull();

    }

    @Test
    void printAllBeans() {
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            System.out.println("beanName = " + name);
        }
    }

    @Test
    void checkApplicationContext() {
        String name = context.getClass().getSimpleName();
        System.out.println(name);
    }
}
