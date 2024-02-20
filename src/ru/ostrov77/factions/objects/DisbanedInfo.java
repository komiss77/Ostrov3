package ru.ostrov77.factions.objects;

import ru.ostrov77.factions.Enums.LogType;



public final class DisbanedInfo {
    
    
    public final int factionId;
    public final String factionName;
    public final int created;        
    public final int disbaned;        
    public final String reason;

    public DisbanedInfo(final int factionId, final String factionName, final int created, final int disbaned, final String reason) {
        this.factionId = factionId;
        this.factionName = factionName;
        this.created = created;
        this.disbaned = disbaned;
        this.reason = reason;
    }
    
}
