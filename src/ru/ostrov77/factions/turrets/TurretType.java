package ru.ostrov77.factions.turrets;

import java.util.Arrays;
import java.util.List;

    
    public enum TurretType {
    
//http://textures.minecraft.net/texture/be3db27cbd1789310409081ad8c42d690b08961b55cadd45b42d46bca28b8
//https://minecraft-heads.com/custom-heads        
        
        //!!! НЕ ПЕРЕСТАВЛЯТЬ МЕСТАМИ !!! определяется по Order!!
        
	Сигнальная ( 
            false,
            "Укажет место вторжения",
            Arrays.asList(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWVlZmE0Y2QyYTU1OGU0YTgxMmUyZWE3NTQxZTYyNzUwYjk2YmExZDgyYzFkYTlmZDVmMmUzZmI5MzA4YzYzNSJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWFmZTRiNGQxNjQ1Mjk4NWNiMWE4NTJjZWI2MjJjMjg1YjkyODU5YTVlZWEyOGJhNmRkM2E2ZjVlM2U4ZDJjNSJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjVlZjhkMmJhNjI5OGE4ZjYwOGUyODNhZmRmYmU1MDkyMTFjYjNmN2JkODg0ZDhiN2UyNzUxYjI4ZmIzZSJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjU1ZWE0M2U1OTI3ODVkMDE2YWNkZWVhOWE0YTZmOWNmMjJjMjc1M2U2OTU0MDVlOGM4M2QyZTA5ZWJjZjY0NyJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWYyNmRhN2VjZTUxM2ZkZjQ4ZjMzNjE3MzJiZThhNTJmNmI4ZTdhYTRkMTlhYzJiNTk0YzZmY2YzM2I5MzgifX19"
                ),
            2,
            1, 1,  //цели  мин / макс
            12, 32,  //радиус  мин / макс
            530, 2800,   //здоровье мин / макс
            2, 7,    //сила мин / макс
            15*20, 5*20,       //тики перезарядки станд / мин
            8, 2,       //расход субстанции за действие
            4000, 200      //ЦЕНЫ: покупки / каждое улучшение
        ),


        Стреломёт ( 
            false,
            "Запускает стрелы в цель",
            Arrays.asList(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVmZDc1MTZkZGJjODFhOWM4MWMxZDllMWMyYzk4ODBkMjNhZTE2M2IzZmIyMTZlZTBjYzQzOTE3YTg4MjgifX19",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmEyYzNlNzlkNWYzNWE5ZGNhYjE5ZTQzYzNlM2E2NTE5ZTQyNmI2NGE2MTIxM2NkMmYxZDI4YjU3MDM2ZjYifX19",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDZkYjEzN2EzNTY3OWJlYWY3OTAwNzBkMGM5Yzk2YzkwNjc2MjYwZWJjMDBkZDJjNzAwNTYyYTA5OWRiMDdjMCJ9fX0",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI2MjhiNzI0ODU0MTM5M2E0YmJiZjExNjI1N2E0MjY4OTI1ZjI3M2U2MmUwY2Q3Mjc5OWY0OGQ2ZDUyZmIzMyJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTU0YmY1ZThlZWQ0ODNkZTM2NTg4MDc3OGZlNGUyZTgyYjc2OWMxMjhlYWIzMzYwMGUyYmI5ZjU5N2U1ZGFlZiJ9fX0="
                ),
            2,
            1, 2,  //цели  мин / макс
            6, 16,  //радиус  мин / макс
            120, 800,   //здоровье мин / макс
            1.5, 4.2,    //сила мин / макс
            10*20, 4*20,       //тики перезарядки станд / мин
            10, 5,       //расход субстанции за действие
            6500, 420      //ЦЕНЫ: покупки / каждое улучшение
        ),

	Целитель ( 
            true,
            "Исцеляет вас и союзников",
            Arrays.asList(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWU4NjljZGNhMzc1MTFlZGZhZjZlYmY4YTI0MzI0Yzk3ZmFhNmUxYTI1ZjFmODY4NjcxNjM4OWVkMDQwNmQ4OSJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmU2ZmFiMDkxZTQ5NmMwOTY5MTA0ODBkYTBkODVlZTkxOWJjNDlhYTMxNzc1Y2FkYmJmNTA1ZWY0MTFiNWY5NCJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJmMjQ5OWFiNGNmYzk3ZTY1ZjBmYTlmZTYzY2M2MDY3MDdhNGFlOTZhZjQwNzg0NmIxYjUzNTRmM2ZhZDk5In19fQ==", 
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjdiMzIzYjg0NTc2ZjgzNTRjNTMzN2M3ZjE2ZjExZjMwYzFjZmZlMDgwNTRkMjY4OWQwNmM3YTlhYjBjYTAzZiJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE5NjExZTdiZWU3YWJiYmU5YTQ2YThhOTljYWE0MjFkZmVhZjVkY2JlN2NlNTg5YjUzZmZkNzk2YTE4OGQzOCJ9fX0="
                ),
            3,
            1, 3,  //цели  мин / макс
            6, 20,  //радиус  мин / макс
            280, 2300,   //здоровье мин / макс
            1, 4,    //сила мин / макс
            19*20, 4*20,       //тики перезарядки станд / мин
            24, 14,       //расход субстанции за действие
            3600, 580      //ЦЕНЫ: покупки / каждое улучшение
        ),

	Псионная ( 
            false,
            "Может свести с ума",
            Arrays.asList(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGI3YWFmZGRiNDVmYWRlZmFjMzNiMDczYjk0YWI0MTE3Y2EwODQyNWE0NGUwMzQ5OWJlYjExZDA3ZDhiOTU3YiJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTYzODM1ODBlYjFmOWQ4NjIwYTQ2MDM2NzNkZjU2MjM0OTFhODczODZhNzQwNmU1M2Y2OWQ3OWFlNzA2ZTAwYyJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjc4ODNlYTQ4MDlhZjc0NWM5N2NhNzdlNzA0NDg1NDk4OTA1MTk4ZDZhZTI4MTM4YWRiZDRjNWQ0YjgxYjk0NCJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVkZDUyNTA1MDVhYWUyMmVmZGVlYmU5NDdhMzUwZWI3N2JjODFhZjQ2MDFlYTViYTg5YjZlNGQ1MDBlYjUwIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2IxOWQyZWQzZjIzYmIyNDRhMzQ1NTliNjA3MDU0YjczNjNjNjRlNTFkYzYzMmU5MTlkYzg1YzQwMWYwZGNhYSJ9fX0="
                ),
            4,
            1, 5,  //цели  мин / макс
            7, 16,  //радиус  мин / макс
            320, 1600,   //здоровье мин / макс
            5, 15,    //длительность воздействия мин / макс
            12*20, 3*20,       //тики перезарядки станд / мин
            9, 3,       //расход субстанции за действие
            5400, 310      //ЦЕНЫ: покупки / каждое улучшение
        ),

        Тесла( 
            false,
            "Электрические разряды в цель",
            Arrays.asList(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODJkZDUyYTFiZDA3MzNhZTMzMjFmNDE2MmI3OTcyZDk0MzA0YzE1ODVjM2E2MmE0MGViZDZmMDZmMGYyYzRjIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2UwNTczNDgxOGJiMDI4NjdlNzNjZjhmYjAwM2QzZDY4ZWFhZjJhMmI0YTFkYzJjZTEwZmRlYTk3MmM3YTE3OSJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWNlZGY4MGMzMzZjMjNhMzQ5ZjFhNWUwNDRjNTQ4NzE1NDBkZGJlODBjN2IyNDUxNGJmOWI3MmIxYmNhMDRmYiJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGI4YjE2YzNkZTg0Y2I3ZjU2NTdlY2JmNGNiOTViMzc4NTkzNzMyMzcyYWIxMWFmMWM5OWI4Njg2NDIxNzhhNSJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzExZmZiOTAyMDk5N2RlMWE0NGI2M2U2MGExZDk3ODZiNWM4MTI5ZGM1NDJmM2IxM2UyYmJjMDc3M2NmYjgxMCJ9fX0="
                ),
            5,
            1, 2,  //цели  мин / макс
            8, 24,  //радиус  мин / макс
            140, 800,   //здоровье мин / макс
            2, 6,    //сила мин / макс
            18*20, 5*20,       //тики перезарядки станд / мин
            9, 4,       //расход субстанции за действие
            12500, 1300      //ЦЕНЫ: покупки / каждое улучшение
        ),

	
	Бомбочки ( 
            false,
            "Заряды с таймером",
            Arrays.asList(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmE4OTc4Y2NiZjU3NmY0NDZlMjFjNTFkM2U4MGZjN2Y4NTY2ZWI3MjY1Y2M0M2M0YWQ3MWNmYjc4YzE2NTI1NyJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVkMDk2ZjhjNjhlZWMyYTcwMWQxZDVkMmQzMDdjMjdmOGRjYmU4Mzc5ZDAwNTI4YmZiMjg2NGM2NjRjMSJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjIxMTY1NzAzOGRkYWZmZGQ5MWI2OGQxYTQ4NWUzY2RiZDVjNTU3NDdkZTMxM2Y4NzYyYThiMzFiZmE2NzY2In19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGFhNmRjZDBjNmNiZjg4NjM5MDVlMTlkOWUwOTRiZjVjMTQ4N2FhMzZjZWIyMjkzZTdlNjMxZGEwY2NhNTdkIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjdlZjZhZWQzYTUwYjQ3N2UyNTg1NTlmYjA3MDQwYzBmZjEzM2Y0MjNmN2YxNWIzMDNiNTExN2QzODY1ZmZmNSJ9fX0="
                ),
            6,
            1, 1,  //цели  мин / макс
            6, 10,  //радиус  мин / макс
            420, 980,   //здоровье мин / макс
            2, 7,    //сила мин / макс
            12*20, 5*20,       //тики перезарядки станд / мин
            15, 8,       //расход субстанции за действие
            13000, 870      //ЦЕНЫ: покупки / каждое улучшение
        ),
        
        
        Стингер ( 
            false,
            "Самонаводящиеся снаряды",
            Arrays.asList(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2Y0NWMxYjFkOWI4YjlhNmM4MTIwNjI1NDBiMjgwODQ1ODIzNGFiNzcyMzY1YjMwOWM1MGIyYzE3YjVkZGI2YSJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWVlZmE0Y2QyYTU1OGU0YTgxMmUyZWE3NTQxZTYyNzUwYjk2YmExZDgyYzFkYTlmZDVmMmUzZmI5MzA4YzYzNSJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjFiZDI3MTAyZTk2YjY5M2I2MjY1MzUzNmJlNzlkZTMwYzNmMmNkNmE4ZWU4YTg3ZmJkY2VlMmQ4OTkxN2M0MyJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDEzOGQ0MjhiNzM2ZWE5ODdiM2JjYjk1MmY0ZGJjMmM0NDAwOTY3ZGE0YTYyOGVhOWFiODFjYTE1NjEwZjdmOCJ9fX0=",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDkwOTVlZDYzMTIwMWNmMmVhZjY2ZjYyODQ0NWMyM2U4ZTJiNjM3MGVmM2Y1ZmYxMzI3MjE2ODVhOGViYzE5OCJ9fX0="
                ),
            7,
            1, 4,  //цели  мин / макс
            6, 17,  //радиус  мин / макс
            180, 1000,   //здоровье мин / макс
            2, 7,    //сила мин / макс
            22*20, 14*20,       //тики перезарядки станд / мин
            23, 12,       //расход субстанции за действие
            22000, 2400      //ЦЕНЫ: покупки / каждое улучшение
        ),



        ;
        
        
        
        
	//ОГНЕМЁТ("Кидает в противника огненные шары",8,24,     100,1000,    1,10,     1,10,            20000, 2000,        2000, 200,      Material.MAGMA),
	//SOLDIER("Атакует противника монстрами",     8,24,     100,1000,    10,30,    1,15,            30000, 3000,        3000, 300,      Material.ANVIL);
        
        
    public final boolean helpful;
    public final String desk;
    public final List<String> textures;
    public final int factionLevel;
    public final int baseTarget, maxTarget;
    public final int baseRadius, maxRadius;
    public final int baseHealth, maxHealth;
    public final double basePower, maxPower;
    public final int baseCharge, minCharge;
    public final int basesubstRate, minSubstRate;
    public final int buyPrice, upgradePrice;
    

        private TurretType(
                final boolean helpful,
                final String desk,
                final List<String> textures, 
                final int factionLevel, 
                final int baseTarget, final int maxTarget, 
                final int baseRadius, final int maxRadius, 
                final int baseHealth, final int maxHealth, 
                final double basePower, final double maxPower, 
                final int baseCharge, final int minCharge, 
                final int basesubstRate, final int minSubstRate,
                final int buyPrice, final int upgradePrice
            ) {
          this.helpful = helpful;
          this.desk = desk;
          this.textures = textures;
          this.factionLevel = factionLevel;
          this.baseTarget = baseTarget;
          this.maxTarget = maxTarget;
          this.baseRadius = baseRadius;
          this.maxRadius = maxRadius;
          this.baseHealth = baseHealth;
          this.maxHealth = maxHealth;
          this.basePower = basePower;
          this.maxPower = maxPower;
          this.baseCharge = baseCharge;
          this.minCharge = minCharge;
          this.basesubstRate = basesubstRate;
          this.minSubstRate = minSubstRate;
          this.buyPrice = buyPrice;
          this.upgradePrice = upgradePrice;
       }        

    
        public static TurretType fromOrder(final int order) {
            if (order<0 || order>=TurretType.values().length) return null;
            return TurretType.values()[order];
        }        
        public static TurretType fromString(final String type) {
            if (type==null) return null;
            for (TurretType tt : values()) {
                if (tt.name().equalsIgnoreCase(type)) return tt;
            }
            return null;
        }

    }  
