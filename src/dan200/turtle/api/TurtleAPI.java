/*    */ package dan200.turtle.api;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public class TurtleAPI
/*    */ {
/* 75 */   private static boolean ccTurtleSearched = false;
/* 76 */   private static Class ccTurtle = null;
/* 77 */   private static Method ccTurtle_registerTurtleUpgrade = null;
/*    */ 
/*    */   public static void registerUpgrade(ITurtleUpgrade upgrade)
/*    */   {
/* 26 */     if (upgrade != null)
/*    */     {
/* 28 */       findCCTurtle();
/* 29 */       if (ccTurtle_registerTurtleUpgrade != null)
/*    */         try
/*    */         {
/* 32 */           ccTurtle_registerTurtleUpgrade.invoke(null, new Object[] { upgrade });
/*    */         }
/*    */         catch (Exception e)
/*    */         {
/*    */         }
/*    */     }
/*    */   }
/*    */ 
/*    */   private static void findCCTurtle()
/*    */   {
/* 46 */     if (!ccTurtleSearched)
/*    */       try
/*    */       {
/* 49 */         ccTurtle = Class.forName("dan200.CCTurtle");
/* 50 */         ccTurtle_registerTurtleUpgrade = findCCTurtleMethod("registerTurtleUpgrade", new Class[] { ITurtleUpgrade.class });
/*    */       }
/*    */       catch (ClassNotFoundException e)
/*    */       {
/* 55 */         System.out.println("ComputerCraftAPI: CCTurtle not found.");
/*    */       }
/*    */       finally {
/* 58 */         ccTurtleSearched = true;
/*    */       }
/*    */   }
/*    */ 
/*    */   private static Method findCCTurtleMethod(String name, Class[] args)
/*    */   {
/*    */     try
/*    */     {
/* 67 */       return ccTurtle.getMethod(name, args);
/*    */     }
/*    */     catch (NoSuchMethodException e) {
/* 70 */       System.out.println("ComputerCraftAPI: CCTurtle method " + name + " not found.");
/* 71 */     }return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\mikeef\Documents\OpenPeripheral_161\forge\mcp\jars\mods\ComputerCraft\
 * Qualified Name:     dan200.turtle.api.TurtleAPI
 * JD-Core Version:    0.6.2
 */