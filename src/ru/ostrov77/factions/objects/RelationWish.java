package ru.ostrov77.factions.objects;

import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.FM;


public class RelationWish {
        public final int from;
        public final int to;
        public final int timestamp;
        public final Relation suggest;
        
        public RelationWish(final int from, final int to, final int timestamp, final Relation suggest) {
            this.from = from;
            this.to = to;
            this.timestamp = timestamp;
            this.suggest = suggest;
        }

    public int getPairKey() {
        return FM.getPairKey(from, to);
    }
        
}
