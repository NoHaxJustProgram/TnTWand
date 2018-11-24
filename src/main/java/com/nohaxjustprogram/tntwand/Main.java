package com.nohaxjustprogram.tntwand;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener
{

    @Override
    public void onEnable()
    {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("tntwand").setExecutor(new TnTWand(this));
        this.getServer().getPluginManager().registerEvents(new Events(this), this);
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable()
    {

    }

}
