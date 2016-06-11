package openperipheral.adapter.types.classifier;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;
import openmods.reflection.TypeUtils;
import openperipheral.adapter.types.EnumeratedRange;
import openperipheral.adapter.types.ListType;
import openperipheral.adapter.types.MapType;
import openperipheral.adapter.types.NamedTupleType;
import openperipheral.adapter.types.NamedTupleType.NamedTupleField;
import openperipheral.adapter.types.NamedTupleType.TupleField;
import openperipheral.adapter.types.SetType;
import openperipheral.adapter.types.SingleArgType;
import openperipheral.adapter.types.TypeHelper;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.adapter.ITypeClassifier;
import openperipheral.api.adapter.ITypeClassifier.IGenericClassifier;
import openperipheral.converter.StructHandlerProvider;
import openperipheral.converter.StructHandlerProvider.IFieldHandler;
import openperipheral.converter.StructHandlerProvider.IStructHandler;

public class DefaultTypeClassifier implements IGenericClassifier {

	@Override
	public IScriptType classify(ITypeClassifier classifier, Type type) {
		final TypeToken<?> typeToken = TypeToken.of(type);

		{
			Class<?> cls = TypeUtils.toObjectType(typeToken.getRawType());

			if (cls == String.class) return SingleArgType.STRING;
			if (cls == UUID.class) return SingleArgType.STRING;
			if (cls == Boolean.class) return SingleArgType.BOOLEAN;
			if (cls == Void.class) return SingleArgType.VOID;
			if (Number.class.isAssignableFrom(cls)) return SingleArgType.NUMBER;

			if (cls.isEnum()) return TypeHelper.bounded(SingleArgType.STRING, EnumeratedRange.create(cls.getEnumConstants()));
			if (StructHandlerProvider.instance.isStruct(cls)) return classifyStruct(classifier, cls);
		}

		if (typeToken.isArray()) return classifyArrayType(classifier, typeToken);
		else if (TypeUtils.MAP_TOKEN.isAssignableFrom(typeToken)) return classifyMapType(classifier, typeToken);
		else if (TypeUtils.SET_TOKEN.isAssignableFrom(typeToken)) return classifySetType(classifier, typeToken);
		else if (TypeUtils.COLLECTION_TOKEN.isAssignableFrom(typeToken)) return classifyCollectionType(classifier, typeToken);

		return null;
	}

	private static IScriptType createListType(ITypeClassifier classifier, TypeToken<?> type) {
		return (type.getRawType() != Object.class)
				? new ListType(classifier.classifyType(type.getType()))
				: SingleArgType.TABLE;
	}

	private static IScriptType createSetType(ITypeClassifier classifier, TypeToken<?> type) {
		return (type.getRawType() != Object.class)
				? new SetType(classifier.classifyType(type.getType()))
				: SingleArgType.TABLE;
	}

	private static IScriptType classifyArrayType(ITypeClassifier classifier, TypeToken<?> typeToken) {
		final TypeToken<?> componentType = typeToken.getComponentType();
		return createListType(classifier, componentType);
	}

	private static IScriptType classifyCollectionType(ITypeClassifier classifier, TypeToken<?> typeToken) {
		final TypeToken<?> componentType = typeToken.resolveType(TypeUtils.COLLECTION_VALUE_PARAM);
		return createListType(classifier, componentType);
	}

	private static IScriptType classifySetType(ITypeClassifier classifier, TypeToken<?> typeToken) {
		final TypeToken<?> componentType = typeToken.resolveType(TypeUtils.SET_VALUE_PARAM);
		return createSetType(classifier, componentType);
	}

	private static IScriptType classifyMapType(ITypeClassifier classifier, TypeToken<?> typeToken) {
		final TypeToken<?> keyType = typeToken.resolveType(TypeUtils.MAP_KEY_PARAM);
		final TypeToken<?> valueType = typeToken.resolveType(TypeUtils.MAP_VALUE_PARAM);

		if (keyType.getRawType() == Object.class || valueType.getRawType() == Object.class) return SingleArgType.TABLE;

		final IScriptType qualifiedKeyType = classifier.classifyType(keyType.getType());
		final IScriptType qualifiedValueType = classifier.classifyType(valueType.getType());

		return new MapType(qualifiedKeyType, qualifiedValueType);
	}

	private static IScriptType classifyStruct(ITypeClassifier classifier, Class<?> cls) {
		IStructHandler handler = StructHandlerProvider.instance.getHandler(cls);

		List<TupleField> fields = Lists.newArrayList();
		for (IFieldHandler f : handler.fields()) {
			IScriptType type = classifier.classifyType(f.type());
			fields.add(new NamedTupleField(f.name(), type, f.isOptional()));
		}

		return new NamedTupleType(fields);
	}

}
