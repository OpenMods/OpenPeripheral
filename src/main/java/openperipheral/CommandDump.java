package openperipheral;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import openmods.Log;
import openmods.OpenMods;
import openmods.utils.SidedCommand;
import openperipheral.adapter.AdapterRegistry;
import openperipheral.adapter.composed.ComposedMethodsFactory;
import openperipheral.adapter.composed.IMethodMap;
import openperipheral.adapter.wrappers.AdapterWrapper;
import openperipheral.util.DocBuilder;
import openperipheral.util.DocBuilder.IClassDecorator;

import com.google.common.collect.Lists;

public class CommandDump extends SidedCommand {

	public CommandDump(String name, boolean restricted) {
		super(name, restricted);
	}

	private interface IArchSerializer {
		public void serialize(DocBuilder builder);
	}

	private static final List<IArchSerializer> archSerializers = Lists.newArrayList();

	public static void addArchSerializer(final String architecture, final String type, final IClassDecorator decorator, final ComposedMethodsFactory<? extends IMethodMap> methods) {
		archSerializers.add(new IArchSerializer() {
			@Override
			public void serialize(DocBuilder builder) {
				for (Map.Entry<Class<?>, ? extends IMethodMap> e : methods.listCollectedClasses().entrySet())
					builder.createDocForClass(architecture, type, decorator, e.getKey(), e.getValue());
			}
		});
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return name + " <type> <file>";
	}

	private static void processExternalAdapters(DocBuilder builder, AdapterRegistry registry, String type) {
		for (Map.Entry<Class<?>, Collection<AdapterWrapper>> e : registry.listExternalAdapters().entrySet()) {
			final Class<?> cls = e.getKey();
			for (AdapterWrapper w : e.getValue())
				builder.createDocForAdapter(type, "external", cls, w);
		}
	}

	private static void processInternalAdapters(DocBuilder builder, AdapterRegistry registry, String type) {
		for (Map.Entry<Class<?>, AdapterWrapper> e : registry.listInternalAdapters().entrySet()) {
			final AdapterWrapper adapter = e.getValue();
			if (!adapter.getMethods().isEmpty()) builder.createDocForAdapter(type, "inline", e.getKey(), adapter);
		}
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		final String format = (args.length >= 1)? args[0] : "xhtml";
		final String name = (args.length >= 2)? args[1] : "openperipheral_docs";
		final String filename = name + '.' + format;

		try {
			final long start = System.currentTimeMillis();
			File output = new File(filename);

			if (!output.isAbsolute()) output = new File(OpenMods.proxy.getMinecraftDir(), filename);

			final DocBuilder builder = new DocBuilder();

			for (IArchSerializer serializer : archSerializers)
				serializer.serialize(builder);

			processExternalAdapters(builder, AdapterRegistry.PERIPHERAL_ADAPTERS, "peripheral");
			processInternalAdapters(builder, AdapterRegistry.PERIPHERAL_ADAPTERS, "peripheral");
			processExternalAdapters(builder, AdapterRegistry.OBJECT_ADAPTERS, "object");
			processInternalAdapters(builder, AdapterRegistry.OBJECT_ADAPTERS, "object");

			if (format.equalsIgnoreCase("xhtml")) builder.dumpXml(output, true);
			else if (format.equalsIgnoreCase("xml")) builder.dumpXml(output, false);
			else {
				sender.addChatMessage(new ChatComponentText("Invalid format: " + format));
				return;
			}

			long duration = System.currentTimeMillis() - start;
			sender.addChatMessage(new ChatComponentTranslation("openperipheralcore.dump.done", format, output.getAbsolutePath(), duration));
		} catch (Throwable t) {
			Log.warn(t, "Failed to execute dump command");
			sender.addChatMessage(new ChatComponentTranslation("openperipheralcore.dump.fail"));
		}
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {
		if (astring.length == 1) return Lists.newArrayList("xml", "xhtml");
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		return false;
	}

}
