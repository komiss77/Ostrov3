package ru.ostrov77.factions.Enums;


    public enum AchievementType {
        
        KILLS ("KILLS", 0, "Собрать душ : %x%", 10, new int[] { 10, 30, 50, 100, 150, 200, 500, 1000 }), 

        ;
        
        String defaultDescription;
        int prizeMultiplier;
        int[] levels;
        
        private AchievementType(final String s, final int n, final String defaultDescription, final int prizeMultiplier, final int... levels) {
            this.defaultDescription = defaultDescription;
            this.prizeMultiplier = prizeMultiplier;
            this.levels = levels;
        }
        
        
    }  
