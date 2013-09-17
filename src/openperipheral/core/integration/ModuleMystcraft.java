package openperipheral.core.integration;

import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.mystcraft.AdapterWritingDesk;


public class ModuleMystcraft {

  public static void init() {
    AdapterManager.addPeripheralAdapter(new AdapterWritingDesk());

  }
}
