package ru.ostrov77.factions.Enums;

    
    public enum TopType {
            claims("§aЗемли"),
            power("§eСила"),
            stars("§2Казна"),
            ;

            
            
        public final String displayName;
        
        private TopType (final String displayName) {
            this.displayName = displayName;
        }
        
        
        
        public static TopType next(final TopType current) {
            if (current==null) return claims;
            int i=0;
            for ( ; i<TopType.values().length; i++) {
                if (current==TopType.values()[i]) break;
            }
            i++;
            if(i>=TopType.values().length) i=0;
            return TopType.values()[i];
        }

    }