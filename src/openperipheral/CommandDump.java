package openperipheral;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;
import openmods.Log;
import openmods.OpenMods;
import openperipheral.adapter.AdapterManager;
import openperipheral.adapter.IMethodsList;
import openperipheral.adapter.composed.ClassMethodsList;
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

			for (Map.Entry<Class<?>, ClassMethodsList<IPeripheralMethodExecutor>> e : AdapterManager.peripherals.listCollectedClasses().entrySet())
				builder.createDocForTe(e.getKey(), e.getValue());

			for (Map.Entry<Class<?>, ClassMethodsList<IObjectMethodExecutor>> e : AdapterManager.objects.listCollectedClasses().entrySet())
				builder.createDocForObject(e.getKey(), e.getValue());

			for (IMethodsList<?> e : AdapterManager.peripherals.listExternalAdapters())
				builder.createDocForPeripheral("peripheralAdapter", e);

			for (IMethodsList<?> e : AdapterManager.objects.listExternalAdapters())
				builder.createDocForPeripheral("objectAdapter", e);

			builder.dump(output);
			sender.sendChatToPlayer(ChatMessageComponent.createFromText("Done! Created file in " + output.getAbsolutePath()));
		} catch (Throwable t) {
			Log.warn(t, "Failed to execute dump command");
			sender.sendChatToPlayer(ChatMessageComponent.createFromText("Failed to execute! Check logs"));

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
