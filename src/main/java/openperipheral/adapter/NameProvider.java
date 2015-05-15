package openperipheral.adapter;

import java.io.*;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import openmods.Log;
import openperipheral.api.peripheral.PeripheralTypeId;
import openperipheral.util.NameUtils;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.Closer;

public class NameProvider {

	private static final String DEFAULTS_FILE = "/default_names.txt";

	private final Splitter lineSplitter = Splitter.onPattern("\\s+");

	public static final NameProvider instance = new NameProvider();

	private final Map<String, String> names = Maps.newTreeMap();

	private File file;

	public void initialize(File configDir) {
		readDefaultNames();
		this.file = new File(configDir, "peripheral_names.txt");
		if (file.exists()) readOverlayNames();

		writeOverlayFile();
	}

	private void readOverlayNames() {
		final String absolutePath = file.getAbsolutePath();
		try {
			final InputStream stream = new FileInputStream(file);
			try {
				parseNames(absolutePath, stream);
			} finally {
				stream.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse " + absolutePath, e);
		}
	}

	private void readDefaultNames() {
		try {
			final InputStream stream = getClass().getResourceAsStream(DEFAULTS_FILE);
			try {
				parseNames(DEFAULTS_FILE, stream);
			} finally {
				stream.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse default names", e);
		}
	}

	private void parseNames(String fileName, InputStream stream) throws IOException {
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(stream));

		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) continue;
			if (line.isEmpty()) continue;
			final List<String> fields = lineSplitter.splitToList(line);
			if (fields.size() != 2) {
				Log.warn("Invalid format at %s:%s: '%s'", fileName, reader.getLineNumber(), line);
			} else {
				final String clsName = fields.get(0);
				final String name = fields.get(1);
				names.put(clsName, name);
			}
		}
	}

	private void writeOverlayFile() {
		if (file == null) return;
		try {
			Closer closer = Closer.create();
			try {
				final OutputStream stream = closer.register(new FileOutputStream(file));
				final PrintWriter writer = closer.register(new PrintWriter(stream));
				writeNames(writer);
			} finally {
				closer.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to write names to " + file.getAbsolutePath(), e);
		}
	}

	private void writeNames(PrintWriter stream) {
		stream.write("#OpenPeripheral user-friendly names\n");
		stream.write("#class_name\tuser_name\n");
		for (Map.Entry<String, String> e : names.entrySet()) {
			stream.write(e.getKey());
			stream.write('\t');
			stream.write(e.getValue());
			stream.write('\n');
		}
	}

	public String getNameByClass(Class<?> cls) {
		return names.get(cls.getName());
	}

	public String getName(Object obj) {
		if (obj == null) return "invalid";
		final Class<?> cls = obj.getClass();
		final String clsName = cls.getName();
		String name = names.get(clsName);

		if (name == null) {
			name = create(cls, obj);
			names.put(clsName, name);

			writeOverlayFile();
		}

		return name;
	}

	private static String create(Class<?> cls, Object target) {
		final String name = tryGetName(cls, target);
		return Strings.isNullOrEmpty(name)? "peripheral" : name.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
	}

	private static String tryGetName(Class<?> cls, Object target) {
		PeripheralTypeId customId = cls.getAnnotation(PeripheralTypeId.class);
		if (customId != null) return customId.value();

		if (target instanceof IInventory) {
			try {
				return ((IInventory)target).getInventoryName();
			} catch (Throwable t) {
				Log.warn(t, "Can't get inventory name for %s", cls);
			}
		}

		if (target instanceof TileEntity) {
			TileEntity te = (TileEntity)target;

			try {
				String mapping = NameUtils.getClassToNameMap().get(cls);
				if (!Strings.isNullOrEmpty(mapping)) return mapping;
			} catch (Throwable t) {
				Log.warn(t, "Failed to map class %s to name", cls);
			}

			try {
				Block block = te.getBlockType();
				if (block != null) {
					int dmg = te.getBlockMetadata();

					ItemStack is = new ItemStack(block, 1, dmg);
					try {
						String name = is.getDisplayName();
						if (!Strings.isNullOrEmpty(name)) return name;
					} catch (Throwable t) {
						Log.warn(t, "Can't get display name for %s", cls);
					}

					try {
						String name = StringUtils.removeStart(block.getUnlocalizedName(), "tile.");
						if (!Strings.isNullOrEmpty(name)) return name;
					} catch (Throwable t) {
						Log.warn(t, "Can't get unlocalized name for %s", cls);
					}

				}
			} catch (Throwable t) {
				Log.warn(t, "Exception while getting name from item for %s", cls);
			}
		}

		return cls.getSimpleName();
	}
}
