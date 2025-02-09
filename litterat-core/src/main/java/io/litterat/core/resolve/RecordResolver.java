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
import io.litterat.schema.meta.Typename;
import io.litterat.schema.meta.Union;

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

                Element element = switch (fieldDataClass) {
                    case DataClassAtom atom -> context.getTypename(atom.dataClass());
                    case DataClassArray array -> new Array(context.getTypename(array.arrayDataClass().dataClass()));
                    case DataClassUnion union -> {
                        boolean isSealed = union.isSealed();
                        Class<?>[] unionClasses = union.memberTypes();
                        Typename[] unionMembers = new Typename[unionClasses.length];
                        for (int unionMemberIndex=0; unionMemberIndex< unionMembers.length; unionMemberIndex++) {
                            unionMembers[unionMemberIndex] = context.getTypename(unionClasses[unionMemberIndex]);
                        }
                        yield new Union(unionMembers);
                    }
                    case DataClassRecord record -> context.getTypename(record.dataClass());
                    case null, default -> throw new TypeException("Unknown field type");
                };

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
