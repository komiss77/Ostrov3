package ru.ostrov77.factions.signProtect;

import java.util.HashSet;
import java.util.Set;
import ru.komiss77.ApiOstrov;
import ru.ostrov77.factions.FM;


public final class ProtectionInfo {
    
    private final String owner;
    private Set <String> users;
    public int validTo;
    public int autoCloseDelay = -1;

    public ProtectionInfo(final String owner) { //создание
        this.owner = owner;
        users = null;
        validTo = FM.getTime() + 60*60*24*90;
        //validTo = FM.getTime() + 90;
    }

    public ProtectionInfo(final String owner, final String usersRaw, final int validTo, final int autoCloseDelay) { //загрузка
        this.owner = owner;
        if (!usersRaw.isEmpty()) {
            for (String name:usersRaw.split(",")) {
                if (!name.isEmpty()) {
                    addUser(name);
                }
            }
        }
        this.validTo = validTo;
        this.autoCloseDelay = autoCloseDelay;
    }

    
    
    
    public boolean canUse(final String name) {
        return isOwner(name) || (users!=null && users.contains(name));
    }
    public boolean addUser(final String name) {
        if (users==null) users = new HashSet<>();
        return users.add(name);
    }
    public String getUsersString() {
        if (users==null) return "";
        return ApiOstrov.listToString(users, ",");
    }

    public String getOwner() {
        return owner;
    }

    public boolean isOwner(final String name) {
        return owner.equalsIgnoreCase(name);
    }


    public String getExpiriedInfo() {
        return validTo==-1 ? "§7#Бессрочно" : "§6#"+ApiOstrov.dateFromStamp(validTo);
    }
    
    public boolean isExpiried() {
        return validTo !=-1 && FM.getTime()>validTo;
    }

    public boolean hasUsers() {
        return users!=null;
    }

    Set <String> getUsers() {
        return users;
    }

    public boolean removeUser(final String name) {
        if (hasUsers() && users.remove(name)) {
            if (users.isEmpty()) users=null;
            return true;
        }
        return false;
    }

    public int userCount() {
        return users==null ? 0 : users.size();
    }
    


    
}
