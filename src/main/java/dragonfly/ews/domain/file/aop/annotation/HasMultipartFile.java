package dragonfly.ews.domain.file.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @UserFileStrategy 와 함께 사용
 */
@Retention(RetentionPolicy.RUNTIME) // 런타임에 어노테이션 정보가 유지되도록 설정
@Target(ElementType.METHOD)
public @interface HasMultipartFile {
}
