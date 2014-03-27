package openperipheral.util;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.minecraft.tileentity.TileEntity;
import openmods.Log;
import openperipheral.adapter.AdapterManager;
import openperipheral.adapter.IDescriptable;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.composed.ClassMethodsList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;

public class DocBuilder {

	private final Document doc;
	private final Element root;

	public DocBuilder() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			root = doc.createElement("documentation");
			doc.appendChild(root);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	public void dump(File output) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(output);
			transformer.transform(source, result);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	public void createDocForTe(Class<? extends TileEntity> te) {
		Element result = doc.createElement("tileEntity");
		try {
			fillDocForClass(result, AdapterManager.peripherals, te);
		} catch (Throwable t) {
			Log.warn(t, "Error while creating docs for TE %s", te);
			result.setAttribute("invalid", "true");
			result.appendChild(doc.createComment("Something went wrong while making documentation for this element. Data may be partially missing or invalid"));
		}
		final String teName = Objects.firstNonNull(PeripheralUtils.getClassToNameMap().get(te), "null");
		result.appendChild(createProperty("name", teName));
		root.appendChild(result);
	}

	public void createDocForObject(Class<?> cls) {
		Element result = doc.createElement("luaObject");
		fillDocForClass(result, AdapterManager.objects, cls);
		root.appendChild(result);
	}

	private void fillDocForClass(Element result, AdapterManager<?, ?> manager, Class<?> cls) {
		result.appendChild(createProperty("class", cls.getName()));
		result.appendChild(createProperty("simpleName", cls.getSimpleName()));

		ClassMethodsList<?> adapted = manager.getAdapterClass(cls);
		for (IMethodExecutor method : adapted.getMethods()) {
			Element methodDoc = doc.createElement("method");
			fillDocForMethod(methodDoc, method.getWrappedMethod());
			result.appendChild(createProperty("isSynthetic", Boolean.toString(method.isSynthetic())));
			result.appendChild(methodDoc);
		}
	}

	private void fillDocForMethod(Element result, IDescriptable method) {
		for (String name : method.getNames())
			result.appendChild(createProperty("name", name));

		result.appendChild(createProperty("signature", method.signature()));
		Element description = doc.createElement("extra");
		serializeMap(description, method.describe());
		result.appendChild(description);
	}

	private void serializeValue(Element output, Object value) {
		if (value == null) output.appendChild(doc.createTextNode("null"));
		else if (value instanceof Map) serializeMap(output, (Map<?, ?>)value);
		else if (value instanceof Collection) serializeCollection(output, (Collection<?>)value);
		else output.appendChild(doc.createTextNode(value.toString()));
	}

	private void serializeCollection(Element output, Collection<?> list) {
		int index = 0;
		for (Object o : list) {
			Element e = doc.createElement("e");
			e.setAttribute("index", Integer.toString(index++));
			serializeValue(e, o);
			output.appendChild(e);
		}
	}

	private void serializeMap(Element output, Map<?, ?> map) {
		for (Map.Entry<?, ?> e : map.entrySet()) {
			Object key = e.getKey();
			Element entry = doc.createElement(key == null? "null" : key.toString());
			serializeValue(entry, e.getValue());
			output.appendChild(entry);
		}
	}

	private Element createProperty(String tag, String value) {
		Element el = doc.createElement(tag);
		el.appendChild(doc.createTextNode(value));
		return el;
	}
}
