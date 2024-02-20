package ru.ostrov77.factions.objects;

import java.util.EnumMap;
import ru.komiss77.ApiOstrov;
import ru.ostrov77.factions.Econ;
import ru.ostrov77.factions.Enums.Role;



public class EconSettings {

    public int econLevel = 1;
    public int loni = 0;
    public int substance = 0;
    public int factionTax = Econ.FACTION_TAX_INTERVAL; //счётчик часов до налога клана
    public int memberTax = Econ.PLAYER_TAX_INTERVAL;   //счётчик часов до налога участников
    private final EnumMap<Role, Integer> taxByRole;     //налог по должностям
   

    
    public EconSettings() {
        //всё будет false
        taxByRole = new EnumMap<>(Role.class);
     }

    
    

    
    public void fromString(final String s) {
        final String[]split = s.split(",");
        if (split.length>=1 && ApiOstrov.isInteger(split[0])) econLevel = Integer.parseInt(split[0]);
        if (split.length>=2 && ApiOstrov.isInteger(split[1])) loni = Integer.parseInt(split[1]);
        if (split.length>=3 && ApiOstrov.isInteger(split[2])) substance = Integer.parseInt(split[2]);
        if (split.length>=4 && ApiOstrov.isInteger(split[3])) factionTax = Integer.parseInt(split[3]);
        if (split.length>=5 && ApiOstrov.isInteger(split[4])) memberTax = Integer.parseInt(split[4]);
        if (split.length>=6 && split[5].startsWith("tax:")) {
            final String taxAsString = split[5].replaceFirst("tax:","");
            if (!taxAsString.isEmpty()) {
                Role role;
                for (final String t : taxAsString.split(":")) {
                    if ( ApiOstrov.isInteger(t) && t.length()==2 ) { //ведь кодировано как 45:12:... и проверяем блоком как число!
                        role = Role.fromOrder(t.charAt(0));
                        if (role!=null) taxByRole.put( role, Character.getNumericValue(t.charAt(1)) );
                    }
                }
            }
        }
        
         
        
        
          
        if (loni<0) loni=0;
        if (substance<0) substance=0;
        /*if (econLevel<0) econLevel=0;
        if (factionTax<0) factionTax=1;
        if (memberTax<0) memberTax=1;
        */
    }
    

    public String asString() {
        StringBuilder sb = new StringBuilder("");
        
        
        sb.append(econLevel).append(",");
        sb.append(loni).append(",");
        sb.append(substance).append(",");
        sb.append(factionTax).append(",");
        sb.append(memberTax).append(",");
        
        sb.append("tax:");
        for (final Role role:taxByRole.keySet()) {
            if (taxByRole.get(role)>1 && taxByRole.get(role)<=9) {
                sb.append(role.order).append(taxByRole.get(role)).append(":");
            }
        }
        sb.append(",");
        

        return sb.toString();
    }












    
    
    
    
    
    
    
    public int getTaxByRole(final Role role) {
        return taxByRole.containsKey(role) ? taxByRole.get(role) : 1;
    }

    public void setTaxByRole(final Role role, final int tax) {
        taxByRole.put(role, tax);
    }
    
 
    
    
    
    
    
}
