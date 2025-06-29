package zypt.zyptapiserver.annotation;

import zypt.zyptapiserver.domain.enums.SocialType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SocialIdentifier {
    SocialType value();
}
