package ru.ostrov77.factions.objects;

import ru.ostrov77.factions.Enums.LogType;



public final class Log {
    
    
    public final LogType type;
    public final String msg;
    public final int timestamp;        

    public Log(final String type, final String msg, final int timestamp) {
        this.type = LogType.fromString(type);
        this.msg = msg;
        this.timestamp = timestamp;
    }
    
}
