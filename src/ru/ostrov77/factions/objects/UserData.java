package ru.ostrov77.factions.objects;

import java.util.EnumSet;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.FM;


public final class UserData {   //хранятся в f.users, можно менять, даже когда игрок офф.
    
    private Role role = Role.Рекрут;
    private final EnumSet<Perm>perms;//персональные права
    //private int stars = 0; //личные крац
    public final int joinedAt;
    private String fpSettings; //настройки для fplayer, чтобы не грузить с мускула. Обновляются при сохранении fplayer
    
    //при загрузке клана
    public UserData(final String permsRaw, final int joinedAt, final String fpSettings) {
        perms = EnumSet.noneOf(Perm.class);
        permFromString(permsRaw);
        this.joinedAt = joinedAt;
        this.fpSettings = fpSettings;
    }
    
    //при присоединении
    public UserData(final Role role) {
        perms = EnumSet.noneOf(Perm.class);
        joinedAt =  FM.getTime();
        fpSettings = "";
        setRole(role);
    }

    public String getSettings() {
        return fpSettings;
    }
    public void setSettings(final String fpSettings) {
        this.fpSettings = fpSettings;
    }
    private void permFromString (final String raw) {
        Perm perm;
        for (final String s:raw.split(",")) {
            if (s.startsWith("role:")){
                role=Role.fromString(s.replaceFirst("role:", ""));
            //} else if (s.startsWith("stars:") && ApiOstrov.isInteger(s.replaceFirst("stars:", ""))){
            //    stars=Integer.parseInt(s.replaceFirst("stars:", ""));
            } else if (s.startsWith("perms:")) {
                perm = Perm.fromInt(s.replaceFirst("perms:", ""));//.split(":")[0]);
                if (perm!=null) {
                    perms.add(perm);
                }
            }
        }
    }

    
    
    public String permAsString() {
        StringBuilder sb = new StringBuilder("");
        
        sb.append("role:").append(role.toString()).append(",");
        
        if (!perms.isEmpty()) {
            sb.append("perms:");
            perms.forEach( (p) -> {
                sb.append(p.order).append(",");
            } );
        }

        //sb.append("stars:").append(stars).append(",");
        
        return sb.toString();
    }

    
    
    
    
    
    public Role getRole() {
        return role;
    }
    public void setRole (final Role role) {
        this.role = role;
        perms.clear();
        if (role!=Role.Лидер) {
            for (final Perm perm:Perm.values()) {
                if (perm.hasRoleDefault.contains(role)) perms.add(perm);
            }
        }
        /*
        switch (role) {
            
            case Лидер: //не надо, всегда true
                perms.clear();
                break;
                
            case Офицер:
                for (final Perm perm:Perm.values()) {
                    if (perm.hasRoleDefault.contains(role)) perms.add(perm);
                }
                //perms.remove(Perm.Kick);
                break;
                
            case Техник:
                perms.add(Perm.Invite);
                break;

            case Рядовой:
                perms.add(Perm.ViewLogs);
                break;
 
            case Рекрут:
                //по умолчанию ничего
                break;
        }*/
        
    }

    
    
    
    
    public boolean hasPersonalPerm(final Perm perm) {
        if (role==Role.Лидер) return true;
        return perms.contains(perm);
    }
    protected boolean addPerm(final Perm perm) {
        return perms.add(perm);
    }
    protected boolean removePerm(final Perm perm) {
        return perms.remove(perm);
    }


   // public int getStars() {
   //     return stars;
   // }
   // public void setStars(final int ammount) {
   //     stars = ammount;
  //  }
    
    
    
    
}
