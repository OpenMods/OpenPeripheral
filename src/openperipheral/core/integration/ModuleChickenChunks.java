package openperipheral.core.integration;

import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.chickenchunks.AdapterTileChunkLoader;

public class ModuleChickenChunks {
  public static void init() {
    AdapterManager.addPeripheralAdapter(new AdapterTileChunkLoader());
  }
}
