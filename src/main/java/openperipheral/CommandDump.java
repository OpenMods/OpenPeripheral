package openperipheral;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import openmods.Log;
import openmods.OpenMods;
import openperipheral.adapter.AdapterRegistry;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.converter.wrappers.AdapterWrapper;
import openperipheral.interfaces.cc.Registries;
import openperipheral.util.DocBuilder;

import com.google.common.collect.Lists;

public class CommandDump implements ICommand {

	@Override
	public int compareTo(Object o) {
		return getCommandName().compareTo(((ICommand)o).getCommandName());
	}

	@Override
	public String getCommandName() {
		return "op_dump";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "op_dump <type> <file>";
	}

	@Override
	public List<?> getCommandAliases() {
		return null;
	}

	private static <E extends IMethodExecutor> void processExternalAdapters(DocBuilder builder, AdapterRegistry registry, String type) {
		for (Map.Entry<Class<?>, Collection<AdapterWrapper>> e : registry.listExternalAdapters().entrySet()) {
			final Class<?> cls = e.getKey();
			for (AdapterWrapper w : e.getValue())
				builder.createDocForAdapter(type, "external", cls, w);
		}
	}

	private static <E extends IMethodExecutor> void processInternalAdapters(DocBuilder builder, AdapterRegistry registry, String type) {
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
			File output = new File(filename);

			if (!output.isAbsolute()) output = new File(OpenMods.proxy.getMinecraftDir(), filename);

			DocBuilder builder = new DocBuilder();

			for (Map.Entry<Class<?>, Map<String, IMethodExecutor>> e : Registries.PERIPHERAL_METHODS_FACTORY.listCollectedClasses().entrySet())
				builder.createDocForTe(e.getKey(), e.getValue());

			for (Map.Entry<Class<?>, Map<String, IMethodExecutor>> e : Registries.OBJECT_METHODS_FACTORY.listCollectedClasses().entrySet())
				builder.createDocForObject(e.getKey(), e.getValue());

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

			sender.addChatMessage(new ChatComponentText("Done! Created " + format + " file in " + output.getAbsolutePath()));
		} catch (Throwable t) {
			Log.warn(t, "Failed to execute dump command");
			sender.addChatMessage(new ChatComponentText("Failed to execute! Check logs"));
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		return true;
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
