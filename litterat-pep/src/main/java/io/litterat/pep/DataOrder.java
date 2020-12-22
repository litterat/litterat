package io.litterat.pep;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Allows setting the data order for any @Data class. By default immutable classes will use the
 * order of parameters in the constructor. POJO classes will use alphabetical order of fields
 * detected. The DataOrder annotation must list all fields found in the class in the expected order.
 * If any fields are missing or names do not match a runtime error will be returned.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface DataOrder {

	public String[] value();
}
