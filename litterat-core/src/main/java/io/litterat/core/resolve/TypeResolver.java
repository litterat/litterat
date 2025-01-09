package io.litterat.core.resolve;

import io.litterat.bind.DataClass;
import io.litterat.core.TypeContext;
import io.litterat.core.TypeException;
import io.litterat.core.meta.Definition;
import io.litterat.core.meta.Element;

import java.lang.reflect.Type;

public interface TypeResolver<D extends Definition, T extends DataClass> {

    D resolveDefinition(TypeContext context, T dataClass, Type parameterizedType)
            throws TypeException;

}
