package io.litterat.core.resolve;

import java.lang.reflect.Type;

import io.litterat.core.TypeContext;
import io.litterat.core.TypeContextNameBinder;
import io.litterat.core.TypeException;
import io.litterat.core.meta.Typename;

public class DefaultNameBinder implements TypeContextNameBinder {

	@Override
	public Typename resolve(TypeContext context, Class<?> clss, Type type) throws TypeException {

		io.litterat.bind.annotation.Typename nameAnnotation = clss
				.getAnnotation(io.litterat.bind.annotation.Typename.class);
		if (nameAnnotation != null) {
			return new Typename(nameAnnotation.namespace(), nameAnnotation.name());
		}

		if (clss.isArray()) {
			Typename arrayTypename = context.getTypename(clss.getComponentType());
			return new Typename(arrayTypename.namespace(), arrayTypename.name() + "_array");
		}

		return new Typename(clss.getPackageName(), clss.getSimpleName());
	}

}
