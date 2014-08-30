package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Retention(value=java.lang.annotation.RetentionPolicy.SOURCE)
@Target(value={PACKAGE,TYPE,ANNOTATION_TYPE,METHOD,CONSTRUCTOR,FIELD,LOCAL_VARIABLE,PARAMETER})
public @interface Generated {
	String value();
}