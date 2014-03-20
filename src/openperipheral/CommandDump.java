package openperipheral;

import java.io.File;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import openmods.Log;
import openmods.OpenMods;
import openperipheral.adapter.PeripheralHandlers;
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

			for (Class<? extends TileEntity> te : PeripheralHandlers.getAllAdaptedTeClasses())
				builder.createDocForTe(te);

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
