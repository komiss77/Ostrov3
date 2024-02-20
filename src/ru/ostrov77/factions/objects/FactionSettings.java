package ru.ostrov77.factions.objects;

import java.util.EnumMap;
import ru.komiss77.ApiOstrov;
import ru.ostrov77.factions.religy.Religy;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.Relations;



public class FactionSettings {

    public boolean useCreative=false;
    public int level = 1;
    protected int power = 0;
    public int maxUsers = 3;
    public boolean inviteOnly = true;
    
    public int diplomatyLevel = 1;
    public int lastWarEndStamp = 0;
    public int warProtect = Relations.WAR_PROTECT_FOR_NEW;
    public Religy religy = Religy.Нет;
    public int relygyChangeStamp = 0;
    
    protected final EnumMap<Science,Integer> sciense;
    public boolean admin=false;
    
    
    
    

    
    public FactionSettings() {
        //всё будет false
        sciense = new EnumMap<>(Science.class);
     }

    
    
    
    public void fromString(final String s) {
        final String[]split = s.split(",");
        
        switch (split.length) {
            case 12:
                if (split[11].trim().equals("1")) admin = true;
            case 11:
                if (ApiOstrov.isInteger(split[10])) relygyChangeStamp = Integer.parseInt(split[10]);
            case 10:
                if (ApiOstrov.isInteger(split[9])) religy = Religy.fromCode(Integer.parseInt(split[9]));
            case 9:
                if (split[8].startsWith("science:")) {
                    final String scAsString = split[8].replaceFirst("science:","");  //1:3;11:12;
                    if (!scAsString.isEmpty()) {
                        Science sc;
                        String[] split2;
                        for (final String sc_level : scAsString.split(";")) {   //1:3
                            split2 = sc_level.split(":");
                            if ( split2.length==2 && ApiOstrov.isInteger(split2[0]) && ApiOstrov.isInteger(split2[1]) ) {
                                sc = Science.fromCode(Integer.parseInt(split2[0]));
                                if (sc!=null) sciense.put( sc, Integer.parseInt(split2[1]) );
                            }
                        }
                    }
                }
            case 8:
                if (ApiOstrov.isInteger(split[7])) warProtect = Integer.parseInt(split[7]);
            case 7:
                if (ApiOstrov.isInteger(split[6])) lastWarEndStamp = Integer.parseInt(split[6]);
            case 6:
                 if (ApiOstrov.isInteger(split[5])) diplomatyLevel = Integer.parseInt(split[5]);
            case 5:
                if (split[4].trim().equals("0")) inviteOnly = false;
            case 4:
                if (ApiOstrov.isInteger(split[3])) maxUsers = Integer.parseInt(split[3]);
            case 3:
                if (ApiOstrov.isInteger(split[2])) power = Integer.parseInt(split[2]);
            case 2:
                if (ApiOstrov.isInteger(split[1])) level = Integer.parseInt(split[1]);
            case 1:
                if (split[0].trim().equals("1")) useCreative = true;
        }

    }
    
    
    
    
    public String asString() {
        StringBuilder sb = new StringBuilder("");
        
        sb.append(useCreative ? "1" : "0").append(",");
        sb.append(level).append(",");
        sb.append(power).append(",");
        sb.append(maxUsers).append(",");
        sb.append(inviteOnly ? "1" : "0").append(",");
        sb.append(diplomatyLevel).append(",");
        sb.append(lastWarEndStamp).append(",");
        sb.append(warProtect).append(",");
        sb.append("science:");
        for (final Science sc:sciense.keySet()) {
            sb.append(sc.code).append(":").append(sciense.get(sc)).append(";");
        }
        sb.append(",");
        sb.append(religy.order).append(",");
        sb.append(relygyChangeStamp).append(",");
        sb.append(admin ? "1" : "0").append(",");
        

        return sb.toString();
    }












    
    
    
    
    
    
    
    
 
    
    
    
    
    
}
