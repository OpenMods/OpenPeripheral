/*    */ package dan200.computer.api;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.lang.reflect.Method;
/*    */ import net.minecraft.tileentity.TileEntity;
/*    */ 
/*    */ public class ComputerCraftAPI
/*    */ {
/* 68 */   private static boolean ccSearched = false;
/* 69 */   private static Class computerCraft = null;
/* 70 */   private static Method computerCraft_registerExternalPeripheral = null;
/*    */ 
/*    */   public static void registerExternalPeripheral(Class<? extends TileEntity> clazz, IPeripheralHandler handler)
/*    */   {
/* 26 */     findCC();
/* 27 */     if (computerCraft_registerExternalPeripheral != null)
/*    */       try
/*    */       {
/* 30 */         computerCraft_registerExternalPeripheral.invoke(null, new Object[] { clazz, handler });
/*    */       }
/*    */       catch (Exception e)
/*    */       {
/*    */       }
/*    */   }
/*    */ 
/*    */   private static void findCC()
/*    */   {
/* 43 */     if (!ccSearched)
/*    */       try {
/* 45 */         computerCraft = Class.forName("dan200.ComputerCraft");
/* 46 */         computerCraft_registerExternalPeripheral = findCCMethod("registerExternalPeripheral", new Class[] { Class.class, IPeripheralHandler.class });
/*    */       }
/*    */       catch (Exception e)
/*    */       {
/* 50 */         System.out.println("ComputerCraftAPI: ComputerCraft not found.");
/*    */       } finally {
/* 52 */         ccSearched = true;
/*    */       }
/*    */   }
/*    */ 
/*    */   private static Method findCCMethod(String name, Class[] args)
/*    */   {
/*    */     try
/*    */     {
/* 60 */       return computerCraft.getMethod(name, args);
/*    */     }
/*    */     catch (NoSuchMethodException e) {
/* 63 */       System.out.println("ComputerCraftAPI: ComputerCraft method " + name + " not found.");
/* 64 */     }return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\mikeef\Documents\OpenPeripheral_161\forge\mcp\jars\mods\ComputerCraft\
 * Qualified Name:     dan200.computer.api.ComputerCraftAPI
 * JD-Core Version:    0.6.2
 */