package openperipheral.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedSet;

import dan200.computercraft.api.filesystem.IMount;


public class ResourceMount implements IMount {

	private static final String RESOURCE_PATH = "/openperipheral/lua/";
	private final SortedSet<String> files;

	public ResourceMount() {
		ImmutableSortedSet.Builder<String> files = ImmutableSortedSet.naturalOrder();
		InputStream fileList = getClass().getResourceAsStream(RESOURCE_PATH + "files.lst");
		if (fileList != null) {
			Scanner sc = new Scanner(fileList);

			while (sc.hasNextLine()) {
				String fileName = sc.nextLine();
				files.add(fileName);
			}

			sc.close();
		}

		this.files = files.build();
	}

	@Override
	public boolean exists(String path) throws IOException {
		return path.isEmpty() || files.contains(path);
	}

	@Override
	public boolean isDirectory(String path) throws IOException {
		return path.isEmpty();
	}

	@Override
	public void list(String path, List<String> contents) throws IOException {
		contents.addAll(files);
	}

	@Override
	public long getSize(String path) throws IOException {
		return 0;
	}

	@Override
	public InputStream openForRead(String path) throws IOException {
		if (!files.contains(path)) throw new IOException();
		return getClass().getResourceAsStream(RESOURCE_PATH + path);
	}

}
