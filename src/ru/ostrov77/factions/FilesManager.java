package ru.ostrov77.factions;

import java.nio.channels.FileChannel;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import org.bukkit.Bukkit;
import java.io.InputStream;
import java.io.IOException;
import org.bukkit.configuration.Configuration;
import java.io.Reader;
import java.io.InputStreamReader;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;

public class FilesManager {
    
    
    private final HashMap<String, FileConfiguration> configurations;
    private final Main plugin;
    
    
    
    public FilesManager(final Main plugin) {
        configurations = new HashMap<>();
        (this.plugin = plugin).reloadConfig();
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
        //registerConfig("challenges.yml");

        for (final String s : configurations.keySet()) {
            reloadConfig(s);
            configurations.get(s).options().copyDefaults(true);
            saveConfig(s);
        }
    }
    
    private void registerConfig(final String s) {
        configurations.put(s, (FileConfiguration)YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), s)));
    }
    
    public FileConfiguration getConfig(final String s) {
        return this.configurations.get(s);
    }
    
    private void reloadConfig(final String s) {
        final InputStream resource = this.plugin.getResource(s);
        if (resource != null) {
            final InputStreamReader inputStreamReader = new InputStreamReader(resource);
            this.configurations.get(s).setDefaults((Configuration)YamlConfiguration.loadConfiguration((Reader)inputStreamReader));
            try {
                inputStreamReader.close();
                resource.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void saveConfig(final String s) {
        try {
            this.configurations.get(s).save(new File(this.plugin.getDataFolder(), s));
        }
        catch (IOException ex) {
            Main.log_err("не удалось сохранить файл "+s+" : "+ex.getMessage());
        }
    }
    
    public static void deleteFile(final File file) {
        if (file.exists()) {
            File[] listFiles;
            for (int length = (listFiles = file.listFiles()).length, i = 0; i < length; ++i) {
                final File file2 = listFiles[i];
                if (file2.isDirectory()) {
                    deleteFile(file2);
                }
                else {
                    file2.delete();
                }
            }
        }
        file.delete();
    }
    
    public void copyFile(final File file, final File file2) {
        try {
            if (file.isDirectory()) {
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                String[] list;
                for (int length = (list = file.list()).length, i = 0; i < length; ++i) {
                    final String s = list[i];
                    this.copyFile(new File(file, s), new File(file2, s));
                }
            }
            else {
                final FileInputStream fileInputStream = new FileInputStream(file);
                final FileOutputStream fileOutputStream = new FileOutputStream(file2);
                final FileChannel channel = fileInputStream.getChannel();
                final FileChannel channel2 = fileOutputStream.getChannel();
                try {
                    channel.transferTo(0L, channel.size(), channel2);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }
                finally {
                    if (channel != null) {
                        channel.close();
                    }
                    if (channel2 != null) {
                        channel2.close();
                    }
                    fileInputStream.close();
                    fileOutputStream.close();
                }
                if (channel != null) {
                    channel.close();
                }
                if (channel2 != null) {
                    channel2.close();
                }
                fileInputStream.close();
                fileOutputStream.close();
            }
        }
        catch (IOException ex) {
            //Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.customization.prefix) + "Failed to copy files!");
            Main.log_err("не удалось скопировать "+file.getName()+" -> "+file2.getName()+" : "+ex.getMessage());
        }
    }
    
    public long getSize(final File file) {
        long length = 0L;
        if (file.isDirectory()) {
            String[] list;
            for (int length2 = (list = file.list()).length, i = 0; i < length2; ++i) {
                length += this.getSize(new File(file, list[i]));
            }
        }
        else {
            length = file.length();
        }
        return length;
    }
}
