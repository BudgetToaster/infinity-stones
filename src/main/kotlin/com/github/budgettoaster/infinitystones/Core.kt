package com.github.budgettoaster.infinitystones

import com.github.budgettoaster.infinitystones.commands.BaseCommand
import com.github.budgettoaster.infinitystones.powers.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldSaveEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

lateinit var plugin: Core

class Core: JavaPlugin(), Listener {
    override fun onEnable() {
        plugin = this
        saveDefaultConfig()

        registerEvents(this,
            InfinityStoneScatterer, InfinityStoneManager,
            RealityStone, MindStone, SoulStone,
            PowerStone, TimeStone, SpaceStone)

        BaseCommand.init()
        server.scheduler.runTaskLater(this, Runnable {
            InfinityStoneManager.load()
        }, 1)
        startSaveLoop()
        logger.info("Enabled")
    }

    private fun startSaveLoop() {
        object: BukkitRunnable() {
            override fun run() {
                saveAll()
            }
        }.runTaskTimer(this, 20*60L, 20*60L)
    }

    override fun onDisable() {
        for(player in server.onlinePlayers) {
            for(slot in 0 until player.inventory.size) {
                val item = player.inventory.getItem(slot) ?: continue
                if(item.isStone()) {
                    player.world.dropItemNaturally(player.location, item)
                    player.inventory.setItem(slot, null)
                }
            }
        }
        saveAll()
        logger.info("Disabled")
    }

    @EventHandler
    fun onWorldSave(ev: WorldSaveEvent) {
        saveAll()
    }

    fun saveAll() {
        InfinityStoneManager.save()
    }

    fun registerEvents(vararg listeners: Listener) {
        for(listener in listeners) {
            server.pluginManager.registerEvents(listener, this)
        }
    }
}