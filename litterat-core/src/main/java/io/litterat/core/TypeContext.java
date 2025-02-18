package io.litterat.core;

import io.litterat.annotation.DataBridge;
import io.litterat.annotation.Namespace;
import io.litterat.bind.DataBindContext;
import io.litterat.bind.DataBindException;
import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassArray;
import io.litterat.bind.DataClassAtom;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.DataClassUnion;
import io.litterat.core.resolve.ArrayResolver;
import io.litterat.core.resolve.AtomResolver;
import io.litterat.core.resolve.CodeAnalysisException;
import io.litterat.core.resolve.DefaultNameBinder;
import io.litterat.core.resolve.RecordResolver;
import io.litterat.core.resolve.UnionResolver;
import io.litterat.schema.TypeException;
import io.litterat.schema.TypeLibrary;
import io.litterat.schema.TypeLibraryState;
import io.litterat.schema.TypeNotDefinedException;
import io.litterat.schema.meta.Definition;
import io.litterat.schema.meta.Meta;
import io.litterat.schema.meta.Typename;

import java.lang.reflect.Type;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

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
            register(Meta.BOOLEAN, Boolean.class);
			register(Meta.BOOLEAN, boolean.class);

			register(Meta.INT32, Integer.class);
			register(Meta.INT32, int.class);

			register(Meta.INT16, Short.class);
			register(Meta.INT16, short.class);

			register(Meta.FLOAT, Float.class);
			register(Meta.FLOAT, float.class);

			register(Meta.STRING, String.class);
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

	public void registerPackage(Package pkg) {
		Namespace namespace = pkg.getAnnotation(Namespace.class);
		if (namespace != null) {
			nameBinder.registerPackage(namespace.value(), pkg);
		}
	}

	public void registerPackage(String name, Package pkg) {
		nameBinder.registerPackage(name, pkg);
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
				DataClass dataClass = bindContext.getDescriptor(targetClass, parameterizedType);
				typename = nameBinder.resolve(this, dataClass);
				result = register(typename, targetClass);
			} else {
				result = bindContext.getDescriptor(targetClass, parameterizedType);
			}
			return result;
        } catch (DataBindException e) {
            throw new TypeException(e);
        }
	}

	public Definition getDefinition(Typename typename) throws TypeException {
		return typeLibrary.getDefinition(typename);
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
			try {
				DataClass dataClass = bindContext.getDescriptor(targetClass, parameterizedType);
				typename = nameBinder.resolve(this, dataClass);
				register(typename, dataClass, parameterizedType);
			} catch (DataBindException ex) {
				throw new TypeException(ex);
			}
		}
		return typename;
	}

	public DataClass register(Typename typename, Definition definition) throws TypeException {

		// Register the schema.
		typeLibrary.register(typename, definition);

		// Find a class
		Class<?> targetClass = nameBinder.resolve(this, typename);
		if (targetClass == null) {
			throw new TypeException("Failed to find system class for :" + typename);
		}

		// Get the DataClass.
        try {
            DataClass dataClass = dataBindContext().getDescriptor(targetClass);
			typenameClass.putIfAbsent(typename, targetClass);
			classTypename.putIfAbsent(targetClass, typename);
			return dataClass;
		} catch (DataBindException e) {
            throw new TypeException("Failed to find data class for :" + targetClass.getName());
        }

    }

	public DataClass register(Typename typename, Class<?> targetClass) throws TypeException {

		try {
			DataClass dataClass = dataBindContext().getDescriptor(targetClass);
			return register(typename, dataClass, dataClass.dataClass());
		} catch (DataBindException ex) {
			throw new TypeException(ex);
		}
	}


	private DataClass register(Typename typename, DataClass dataClass, Type parameterizedType) throws TypeException, DataBindException {
		Class<?> targetClass = dataClass.typeClass();

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
			register(typename, targetClass);
			//typenameClass.putIfAbsent(typename, targetClass);
        } catch (DataBindException e) {
            throw new TypeException(e);
        }
    }
}
