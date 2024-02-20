
import ru.ostrov77.factions.turrets.Turret;
import ru.ostrov77.factions.turrets.TurretType;





public class main {
    
    public static int level = 121;
    public static int order = 212;
    public static boolean shotMobs = true;  //1
    public static boolean shotWildernes = false;  //2
    public static boolean shotEnemy = true;  //4
    public static boolean shotOther = true;  //8


    public static void main(String[] args) {
        String s = "60|§6[§eReport§6] §f";
        
        System.out.println(s);
        System.out.println(s.substring(0, s.indexOf("|")));
        


        int code = 1;
        int x = 5;//1500111222;
        int y = 6;//1500111222;
        int z = 7;//2111222333;
        //int xor = x ^ z;
        //int res = (code<<24) | ((x&0xF)<<16) | (y<<8) | z;
        
        //System.out.println("MAX="+Integer.toBinaryString(Integer.MAX_VALUE));
        
       // int sett = getSettings();

        
       // System.out.println("="+(16>>4));
        
       // applySettings(sett);
        
      //  System.out.println("sett2="+ Integer.toBinaryString(getSettings()));
       // System.out.println("lvl="+ level);
        //System.out.println("order="+ order);
        
       // System.out.println("x="+Long.toBinaryString(Long.) );
        //System.out.println("x&0xF="+Integer.toBinaryString(x&0xF));
        //System.out.println("(x&0xF)<<16="+Integer.toBinaryString((x&0xF)<<16)+" = "+((x&0xF)<<16));
        
        //System.out.println("y="+Integer.toBinaryString(x));
        //System.out.println("y<<8="+Integer.toBinaryString(y<<8));
        
        //System.out.println("((x&0xF)<<16) | (y<<8)="+Integer.toBinaryString(((x&0xF)<<16) | (y<<8)));
       // System.out.println("((x&0xF)<<16) | (y<<8) | z="+Integer.toBinaryString(((x&0xF)<<16) | (y<<8) | z)+" dec="+(((x&0xF)<<16) | (y<<8) | z));
        //System.out.println("(code<<24) | ((x&0xF)<<16) | (y<<8) | z="+Integer.toBinaryString(code<<24 | (x&0xF)<<16 | y<<8 | z) + " dec="+((code<<24) | ((x&0xF)<<16) | (y<<8) | z));
        
        //System.out.println("z="+Integer.toBinaryString(y));
        
        //System.out.println("res="+Integer.toBinaryString(res));
        
        //System.out.println("backX="+(res>>16 & 0xFF));
        //System.out.println("backY="+(res>>8 & 0xFF));
        //System.out.println("backZ="+(res & 0xFF));
        //System.out.println(Integer.toBinaryString(loc));
        
        //по 13бит на x z чанка, 5бит длина мира
        //System.out.println("len="+(res>>26));
        //System.out.println("x="+  ((res>>13 & 0b__00011111_11111111)-4096)   );
        //System.out.println("z="+  ((res & 0b__00011111_11111111)-4096)   );
    }
    
    public static void applySettings(final int settings) {
System.out.println("applySettings="+Integer.toBinaryString(settings));
System.out.println("(settings & 0x1)="+((settings & 0x1) > 0));
System.out.println("(settings & 0x2)="+((settings & 0x2)>0));
System.out.println("(settings & 0x4)="+((settings & 0x4)>0));
System.out.println("(settings & 0x8)="+((settings & 0x8)>0));
        level = settings>>8 & 0xFF;
        order  = settings>>16 & 0xFF;
        shotMobs = (settings & 0x1) > 0;
        shotWildernes = (settings & 0x2) > 0;
        shotEnemy = (settings & 0x4) > 0;
        shotOther = (settings & 0x8) > 0;
    }
    
    public static int getSettings() {
        return (shotOther?0x8:0) | (shotEnemy?0x4:0) | (shotWildernes?0x2:0) | (shotMobs?0x1:0) | order<<16 | level<<8;
    }
    


}
