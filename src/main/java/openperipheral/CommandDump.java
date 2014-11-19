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
import openperipheral.adapter.*;
import openperipheral.adapter.object.IObjectMethodExecutor;
import openperipheral.adapter.peripheral.IPeripheralMethodExecutor;
import openperipheral.util.DocBuilder;

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
		return "op_dump <file>";
	}

	@Override
	public List<?> getCommandAliases() {
		return null;
	}

	private static <E extends IMethodExecutor> void processExternalAdapters(DocBuilder builder, AdapterManager<E> manager, String type) {
		for (Map.Entry<Class<?>, Collection<AdapterWrapper<E>>> e : manager.listExternalAdapters().entrySet()) {
			final Class<?> cls = e.getKey();
			for (AdapterWrapper<E> w : e.getValue())
				builder.createDocForAdapter(type, "external", cls, w);
		}
	}

	private static <E extends IMethodExecutor> void processInternalAdapters(DocBuilder builder, AdapterManager<E> manager, String type) {
		for (Map.Entry<Class<?>, AdapterWrapper<E>> e : manager.listInternalAdapters().entrySet()) {
			final AdapterWrapper<E> adapter = e.getValue();
			if (!adapter.getMethods().isEmpty()) builder.createDocForAdapter(type, "internal", e.getKey(), adapter);
		}
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		String filename;
		if (args.length == 1) {
			filename = args[0];
		} else {
			filename = "openperipheral_docs.xml";
		}

		try {
			File output = new File(filename);

			if (!output.isAbsolute()) output = new File(OpenMods.proxy.getMinecraftDir(), filename);

			DocBuilder builder = new DocBuilder();

			for (Map.Entry<Class<?>, MethodMap<IPeripheralMethodExecutor>> e : AdapterManager.PERIPHERALS_MANAGER.listCollectedClasses().entrySet())
				builder.createDocForTe(e.getKey(), e.getValue());

			for (Map.Entry<Class<?>, MethodMap<IObjectMethodExecutor>> e : AdapterManager.OBJECTS_MANAGER.listCollectedClasses().entrySet())
				builder.createDocForObject(e.getKey(), e.getValue());

			processExternalAdapters(builder, AdapterManager.PERIPHERALS_MANAGER, "peripheral");
			processInternalAdapters(builder, AdapterManager.PERIPHERALS_MANAGER, "peripheral");
			processExternalAdapters(builder, AdapterManager.OBJECTS_MANAGER, "object");
			processInternalAdapters(builder, AdapterManager.OBJECTS_MANAGER, "object");

			builder.dump(output);
			sender.addChatMessage(new ChatComponentText("Done! Created file in " + output.getAbsolutePath()));
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
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		return false;
	}

}
