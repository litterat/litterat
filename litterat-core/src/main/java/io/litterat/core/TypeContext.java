package io.litterat.core;

import java.lang.reflect.Type;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import io.litterat.bind.*;
import io.litterat.schema.TypeException;
import io.litterat.schema.TypeLibrary;
import io.litterat.schema.TypeLibraryState;
import io.litterat.schema.TypeNotDefinedException;
import io.litterat.schema.meta.Definition;
import io.litterat.schema.meta.Meta;
import io.litterat.schema.meta.Typename;
import io.litterat.core.resolve.*;

/**
 * The TypeContext binds the Class/DataClass pair provided by the DataBindContext with the Typename/Definition
 * pair provided by the TypeLibrary. The TypeContext class maintains the Typename/Class pair and Class/Typename
 * to allow finding the links between all types.
 * <p>
 * DataClass <--- Class --- Typename ---> Definition
 * <p>
 *
 */
public class TypeContext {
	// Resolved class information
	private final ConcurrentHashMap<Typename, Class<?>> typenameClass = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Class<?>, Typename> classTypename = new ConcurrentHashMap<>();
	private final Stack<Typename> registerStack = new Stack<>();
	private final DataBindContext bindContext;
	private final TypeLibrary typeLibrary;

	private final DefaultNameBinder nameBinder = new DefaultNameBinder();

	private final RecordResolver recordResolver;
	private final AtomResolver atomResolver;
	private final ArrayResolver arrayResolver;
	private final UnionResolver unionResolver;


	public static class Builder {

		DataBindContext bindContext = null;
		TypeLibrary typeLibrary = null;

		boolean allowAny = false;

		boolean createDefinitions = true;

		public Builder() {
		}

		public Builder bindContext(DataBindContext bindContext) {
			this.bindContext = bindContext;
			return this;
		}

		public Builder typeLibrary(TypeLibrary library) {
			this.typeLibrary = library;
			return this;
		}

		public Builder allowAny() {
			allowAny = true;
			return this;
		}

		public Builder doNotCreate() {
			createDefinitions = false;
			return this;
		}

		public TypeContext build() {
			if (typeLibrary == null) {
				this.typeLibrary = new TypeLibrary();
			}

			if (bindContext == null) {
				this.bindContext = DataBindContext.builder().build();
			}

			return new TypeContext(this);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	private TypeContext(Builder builder) {
		this.bindContext = builder.bindContext;
		this.typeLibrary = builder.typeLibrary;

		// TODO put into builder.
        recordResolver = new RecordResolver();
		atomResolver = new AtomResolver();
		arrayResolver = new ArrayResolver();
		unionResolver = new UnionResolver();


        try {
			// bind atoms to local classes. Not expecting any exception here.
            register(Meta.BOOLEAN, Boolean.class, Boolean.class);
			register(Meta.BOOLEAN, boolean.class, boolean.class);

			register(Meta.INT32, Integer.class, Integer.class);
			register(Meta.INT32, int.class, int.class);

			register(Meta.INT16, Short.class, Short.class);
			register(Meta.INT16, short.class, short.class);

			register(Meta.FLOAT, Float.class, Float.class);
			register(Meta.FLOAT, float.class, float.class);
        } catch (TypeException e) {
            throw new RuntimeException(e);
        }

    }

	public DataBindContext dataBindContext() {
		return bindContext;
	}

	public TypeLibrary library() {
		return typeLibrary;
	}

	public DataClass getDescriptor(Class<?> targetClass) throws TypeException {
		// Use the erased type if type parameters not provided.
		return getDescriptor(targetClass, targetClass);
	}

	public DataClass getDescriptor(Class<?> targetClass, Type parameterizedType) throws TypeException {
        try {
			DataClass result = null;
			Typename typename = classTypename.get(targetClass);
			if (typename == null) {
				typename = nameBinder.resolve(this, targetClass, parameterizedType);
				result = register(typename, targetClass, parameterizedType);
			} else {
				result = bindContext.getDescriptor(targetClass, parameterizedType);
			}
			return result;
        } catch (DataBindException e) {
            throw new TypeException(e);
        }
	}

	public DataClass getDescriptor(Typename typename) throws TypeException {
		try {
			Class<?> targetClass = typenameClass.get(typename);
			if (targetClass == null) {
				throw new TypeNotDefinedException(typename);
			}
			return bindContext.getDescriptor( typenameClass.get(typename));
		} catch (DataBindException e) {
			throw new TypeException(e);
		}
	}

	public Typename getTypename(Class<?> targetClass) throws TypeException {
		// Use the erased type if type parameters not provided.
		return getTypename(targetClass, targetClass);
	}

	public Typename getTypename(Class<?> targetClass, Type parameterizedType) throws TypeException {
		Typename typename = classTypename.get(targetClass);
		if (typename == null) {
			typename = nameBinder.resolve(this, targetClass, parameterizedType);
			register(typename, targetClass, parameterizedType);
		}
		return typename;
	}

	public DataClass register(Typename typename, Class<?> targetClass, Type parameterizedType) throws TypeException {

        try {
            DataClass dataClass = dataBindContext().getDescriptor(targetClass, parameterizedType);

			if (registerStack.contains(typename)) {
				System.out.println("already trying to register: " + typename);
				return dataClass;
			}

			// attempt to remove registration cycles.
			registerStack.push(typename);
			Definition definition = null;
			if (typeLibrary.isRegistered(typename) && typeLibrary.getDefinitionState(typename) == TypeLibraryState.REGISTERED) {
				definition = typeLibrary.getDefinition(typename);
			} else {
				definition = createDefinition(dataClass, parameterizedType);
			}

			if (dataClass instanceof DataClassAtom) {
				System.err.println("Warning: Registering Atom: " + typename + " to " + targetClass.getName());
			}

			typeLibrary.register(typename, definition);
			typenameClass.putIfAbsent(typename, targetClass);
			classTypename.putIfAbsent(targetClass, typename);

			registerStack.pop();
			return dataClass;
        } catch (DataBindException | CodeAnalysisException e) {
            throw new TypeException(e);
        }

	}


	/**
	 * Create a meta definition for the TypeLibrary from the in-built DataClass.
	 */
	private Definition createDefinition(DataClass dataClass, Type parameterizedType)
            throws TypeException, CodeAnalysisException, DataBindException {

        return switch (dataClass) {
			case DataClassUnion dataClassUnion ->
					unionResolver.resolveDefinition(this, dataClassUnion, parameterizedType);
			case DataClassArray dataClassArray ->
					arrayResolver.resolveDefinition(this, dataClassArray, parameterizedType);
			case DataClassAtom dataClassAtom ->
					atomResolver.resolveDefinition(this, dataClassAtom, parameterizedType);
			case DataClassRecord dataClassRecord ->
					recordResolver.resolveDefinition(this, dataClassRecord, parameterizedType);
			case null, default ->
					throw new CodeAnalysisException( String.format("Unable to find a valid data conversion for class: %s", dataClass));
			};
	}


	public void registerAtom(Typename typename, Class<?> targetClass, DataBridge<?, ?> bridge) throws TypeException {
        try {
            DataClassAtom dataClass = bindContext.registerAtom(targetClass, bridge);
			register(typename, targetClass, targetClass);
			//typenameClass.putIfAbsent(typename, targetClass);
        } catch (DataBindException e) {
            throw new TypeException(e);
        }
    }
}
