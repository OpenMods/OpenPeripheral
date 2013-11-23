package openperipheral.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import openperipheral.Config;
import dan200.computer.api.IMount;

public class BasicMount implements IMount {

	@Override
	public boolean exists(String path) throws IOException {
		File file = new File(new File(Config.localLuaFolder), path);
		return file.exists();
	}

	@Override
	public boolean isDirectory(String path) throws IOException {
		File file = new File(new File(Config.localLuaFolder), path);
		return file.isDirectory();
	}

	@Override
	public void list(String path, List<String> contents) throws IOException {
		File directory = new File(new File(Config.localLuaFolder), path);
		for (File file : directory.listFiles()) {
			contents.add(file.getName());
		}
	}

	@Override
	public long getSize(String path) throws IOException {
		File file = new File(new File(Config.localLuaFolder), path);
		return file.length();
	}

	@Override
	public InputStream openForRead(String path) throws IOException {
		File file = new File(new File(Config.localLuaFolder), path);
		return new FileInputStream(file);
	}
}
