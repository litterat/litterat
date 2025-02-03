package io.litterat.core.resolve;

import io.litterat.annotation.ToData;
import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassArray;
import io.litterat.bind.DataClassAtom;
import io.litterat.bind.DataClassField;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.DataClassUnion;
import io.litterat.core.TypeContext;
import io.litterat.schema.TypeException;
import io.litterat.schema.meta.Array;
import io.litterat.schema.meta.Element;
import io.litterat.schema.meta.Field;
import io.litterat.schema.meta.Record;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class RecordResolver implements TypeResolver<Record, DataClassRecord> {

    public Record resolveDefinition(TypeContext context, DataClassRecord dataClass, Type type) throws TypeException {
        try {
            DataClassField[] classFields = dataClass.fields();
            Field[] fields = new Field[classFields.length];
            for (int x = 0; x < classFields.length; x++) {
                DataClassField classField = classFields[x];
                DataClass fieldDataClass = classField.dataClass();

                Element element = null;
                if (fieldDataClass instanceof DataClassAtom atom) {
                    element = context.getTypename( atom.dataClass() );
                } else if (fieldDataClass instanceof DataClassArray array ) {
                    element = new Array( context.getTypename( array.arrayDataClass().typeClass() ));
                } else if (fieldDataClass instanceof DataClassUnion union ) {
                    element = context.getTypename( union.typeClass() );
                } else if (fieldDataClass instanceof DataClassRecord record ) {
                    element = context.getTypename(record.typeClass());
                } else {
                    throw new TypeException("Unknown field type");
                }
                //Definition def = classFields[x].dataClass().definition();
//				if (!(def instanceof Element)) {
//					throw new TypeException("Only elemnt types allowed for fields: " + def.getClass().getName());
//				}
                //Typename typename = context.getTypename( classField.type() );

                fields[x] = new Field(classField.name(), element, classField.isRequired());
            }

            return new Record(fields);

        } catch (SecurityException | TypeException e) {
            throw new TypeException("Failed to resolve", e);
        }

    }

    private Class<?> resolveTargetClass(Class<?> targetClass) throws CodeAnalysisException {

        // Default to targetClass.
        Class<?> targetToData = targetClass;

        // Find the correct type for ToData.
        for (Type genericInterface : targetClass.getGenericInterfaces()) {

            if (genericInterface instanceof ParameterizedType
                    && ((ParameterizedType) genericInterface).getRawType() == ToData.class) {

                targetToData = (Class<?>) ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
                break;
            }

        }

        return targetToData;
    }

}
