package ru.ostrov77.factions.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import ru.ostrov77.factions.Land;


public class SimpleLocation {
    
    public int worldNameLenght;
    public int x;
    public int y;
    public int z;
    
    public Location getLocation() {
        return Bukkit.getWorld(Land.getcWorldName(worldNameLenght)).getBlockAt(x, y, z).getLocation();
    }
    
    public boolean equals(final SimpleLocation sl) {
        return sl.worldNameLenght==worldNameLenght & sl.x==x & sl.y==y & sl.z==z;
    }
    
    public boolean equals(final Location loc) {
        return loc.getWorld().getName().length()==worldNameLenght & loc.getBlockX()==x & loc.getBlockY()==y & loc.getBlockZ()==z;
    }
    
}
