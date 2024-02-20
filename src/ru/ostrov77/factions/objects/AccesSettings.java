package ru.ostrov77.factions.objects;

import java.util.EnumMap;
import java.util.EnumSet;
import ru.komiss77.ApiOstrov;
import ru.ostrov77.factions.Enums.AccesMode;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.Enums.Role;



public class AccesSettings {

    public boolean changed = false;
    public boolean permChanged = false;
    
    public final EnumMap<Role,AccesMode> roleAcces;
    public final EnumMap<Relation,AccesMode> relationAcces;
    public AccesMode wildernesAcces = AccesMode.DenyAll;
    
    public final EnumMap<Role,EnumSet<Perm>> rolePerms; //права групп
    
    
    
    public AccesSettings() {
        //всё будет false
        roleAcces = new EnumMap<>(Role.class);
        relationAcces = new EnumMap<>(Relation.class);
        
        rolePerms = new EnumMap<>(Role.class);  //инициализация пустышки
        for (final Role role : Role.values()) {
            if (role == Role.Лидер) continue;
            rolePerms.put(role, EnumSet.noneOf(Perm.class));
        }
        for (final Perm perm : Perm.values()) { //прогрузка дефолтных
            perm.hasRoleDefault.forEach( (role) -> {
                rolePerms.get(role).add(perm);
            } );
        }
    }

    
    
    
    public void fromString(final String s) {
        final String[]split = s.split(",");
        int resInt;
        AccesMode mode;
        Role role;
        Relation rel;
        if (split.length>=1 && !split[0].isEmpty() && split[0].length()%2==0 && ApiOstrov.isInteger(split[0])) {
            resInt = Integer.parseInt(split[0]);
            while (resInt>0) {  //счёт идет обратно, так что сначала mode, потом role!!!
                mode = AccesMode.fromCode(resInt - ((int)(resInt/10))*10 );
                resInt = resInt/10;
                role = Role.fromOrder(resInt - ((int)(resInt/10))*10 );
                resInt = resInt/10;
                setMode(role,mode); //проверочки в Клаиме
            }
        }
        if (split.length>=2 && !split[1].isEmpty() && split[1].length()%2==0 && ApiOstrov.isInteger(split[1])) {
            resInt = Integer.parseInt(split[1]);
            while (resInt>0) {  //счёт идет обратно, так что сначала mode, потом role!!!
                mode = AccesMode.fromCode(resInt - ((int)(resInt/10))*10 );
                resInt = resInt/10;
                rel = Relation.fromOrder(resInt - ((int)(resInt/10))*10 );
                resInt = resInt/10;
                setMode(rel,mode); //проверочки в Клаиме
            }
        }
        if (split.length>=3 && ApiOstrov.isInteger(split[2])) wildernesAcces = AccesMode.fromCode(Integer.parseInt(split[2]));
        
        
        
        


    }
    

    public String asString() {
        StringBuilder sb = new StringBuilder("");
        
        sb.append(getRoleAccesRaw()).append(",");
        sb.append(getRelationAccesRaw()).append(",");
        sb.append(wildernesAcces.code);
        //.append(",");

        return sb.toString();
    }
















    public void rolePermsFromString(final String s) {
        Role role;
        Perm perm;
//System.out.println("rolePermsFromString "+s);
        for (String s1 : s.split(";")) {
            final String[] s2 = s1.split(":");
//System.out.println("s2= "+s2[0]+" len="+s2.length);
                if (s2.length>=1 && !s2[0].isEmpty() ) { //тут s2[1].isEmpty() не проверять, или не вычистит дефолтные права!!!
                    role = Role.fromString(s2[0]);          //и если вторая часть пустая, length=1 !!
                    if (role!=null) {
                        rolePerms.get(role).clear(); //удаляем дефолтные
                        if (s2.length<2 || s2[1].isEmpty()) continue;
                        for (String s3 : s2[1].split(",")) {
                            if (ApiOstrov.isInteger(s3)) {
                                perm = Perm.fromInt(Integer.valueOf(s3));
                                if (perm!=null) {
                                    rolePerms.get(role).add(perm);
                                }
                            }
                            
                        }
                    }
                
            }
            
        }
    }
    
    
    public String rolePermsAsString() {
        StringBuilder sb = new StringBuilder();
        for (final Role r : rolePerms.keySet()) {
            sb.append(r).append(":");
            rolePerms.get(r).forEach( (perm) -> {
                sb.append(perm.order).append(",");
            } );
            sb.append(";");
        }
        return sb.toString();
    }
    
    
    

    
    
    
    
    
    
    
    
    
    
    public int getRoleAccesRaw() {
        int raw = 0;
        for (final Role role : roleAcces.keySet()) {
            if (roleAcces.get(role)!=AccesMode.GLOBAL) raw = raw*100 + role.order*10 + roleAcces.get(role).code;
        }
        //StringBuilder sb = new StringBuilder();
        //roleAcces.keySet().forEach( (role) -> {
        //    if (roleAcces.get(role)!=AccesMode.GLOBAL) sb.append(role.order).append(roleAcces.get(role).code);
        //} );
        //return sb.toString();
        return raw;
    }
    
    public int getRelationAccesRaw() {
        int raw = 0;
        for (final Relation rel : relationAcces.keySet()) {
            if (relationAcces.get(rel)!=AccesMode.GLOBAL) raw = raw*100 + rel.order*10 + relationAcces.get(rel).code;
        }
        //StringBuilder sb = new StringBuilder();
        //relationAcces.keySet().forEach( (rel) -> {
        //    if (relationAcces.get(rel)!=AccesMode.GLOBAL) sb.append(rel.order).append(relationAcces.get(rel).code);
        //} );
        //return sb.toString();
        return raw;
    }
/*    public String getRoleAccesString() {
        StringBuilder sb = new StringBuilder();
        roleAcces.keySet().forEach( (role) -> {
            if (roleAcces.get(role)!=AccesMode.AllowAll) sb.append(role.order).append(roleAcces.get(role).code);
        } );
        return sb.toString();
    }
    public String getRelationAccesString() {
        StringBuilder sb = new StringBuilder();
        relationAcces.keySet().forEach( (rel) -> {
            if (relationAcces.get(rel)!=AccesMode.AllowAll) sb.append(rel.order).append(relationAcces.get(rel).code);
        } );
        return sb.toString();
    }*/

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    

    public AccesMode getMode(final Relation rel) {
        if (rel != null && relationAcces.containsKey(rel)) return relationAcces.get(rel);
        return rel.defaultMode;
    }
    public AccesMode getMode(final Role role) {
        if (role != null && roleAcces.containsKey(role)) return roleAcces.get(role);
        return AccesMode.AllowAll;    
    }


    public void setMode(final Relation rel, final AccesMode mode) {
        if (rel!=null && mode!=null) {
            if (mode==rel.defaultMode) {
                if (relationAcces.containsKey(rel)) relationAcces.remove(rel);
            } else {
                relationAcces.put(rel, mode);
            }
            changed = true;
        }
    }
    public void setMode(final Role role, final AccesMode mode) {
        if (role!=null && mode!=null) {
            if ( mode==AccesMode.AllowAll) {
                if (roleAcces.containsKey(role)) roleAcces.remove(role);
            } else {
                roleAcces.put(role, mode);
            }
            changed = true;
        }
    }



    
    
    
    
    
    
    
}
