package com.github.budgettoaster.infinitystones.powers

import com.github.budgettoaster.infinitystones.plugin
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.time.Duration
import java.time.Instant
import java.util.*

object TimeStone : Listener, InfinityStone {
    private val playerPositions = HashMap<UUID, ArrayList<Pair<Instant, Location>>>()
    private val playerHealths = HashMap<UUID, ArrayList<Pair<Instant, Float>>>()
    private val playerFood = HashMap<UUID, ArrayList<Pair<Instant, Short>>>()
    private val recentUses = HashMap<Player, Instant>()
    private val recentTryUses = HashMap<Player, Instant>()
    private val reversalTime = plugin.config.getLong("time stone reversal time")
    override val friendlyName: String
        get() = "Time Stone"

    init {
        val scheduler = Bukkit.getServer().scheduler
        scheduler.scheduleSyncRepeatingTask(plugin, {
            updatePlayerData()
            cleanup()
        }, 0L, 20L)
    }

    private fun updatePlayerData() {
        val now = Instant.now()
        for (player in plugin.server.onlinePlayers) {
            if(playerHealths[player.uniqueId] == null) playerHealths[player.uniqueId] = ArrayList()
            playerHealths[player.uniqueId]!!.add(Pair(now, player.health.toFloat()))

            if(playerPositions[player.uniqueId] == null) playerPositions[player.uniqueId] = ArrayList()
            playerPositions[player.uniqueId]!!.add(Pair(now, player.location))

            if(playerFood[player.uniqueId] == null) playerFood[player.uniqueId] = ArrayList()
            playerFood[player.uniqueId]!!.add(Pair(now, player.foodLevel.toShort()))
        }
    }

    private fun cleanup() {
        for (entry in playerPositions) {
            playerPositions[entry.key] = playerPositions[entry.key]?.apply {
                removeIf { t -> t.first.isBefore(Instant.now().minusSeconds(reversalTime)) }
            } ?: continue
        }
        for (entry in playerHealths) {
            playerHealths[entry.key] = playerHealths[entry.key]?.apply {
                removeIf { t -> t.first.isBefore(Instant.now().minusSeconds(reversalTime)) }
            } ?: continue
        }
        for (entry in playerFood) {
            playerFood[entry.key] = playerFood[entry.key]?.apply {
                removeIf { t -> t.first.isBefore(Instant.now().minusSeconds(reversalTime)) }
            } ?: continue
        }
    }

    @EventHandler
    fun onInteractEvent(event: PlayerInteractEvent) {
        if(!isValid(event.player.inventory.itemInMainHand)) return
        val timeSinceUse = Duration.between(recentUses[event.player] ?: Instant.EPOCH, Instant.now())
        val timeSinceTryUse = Duration.between(recentTryUses[event.player] ?: Instant.EPOCH, Instant.now())
        if(timeSinceUse.toMillis() < plugin.config.getLong("time stone delay", 5000)) {
            if(timeSinceTryUse.toMillis() < 1000) return // One second between fail message
            recentTryUses[event.player] = Instant.now()
            event.player.sendMessage("${ChatColor.RED}You have to wait to use that again!")
            return
        }
        recentTryUses[event.player] = Instant.now()
        val playerLocation = event.player.location
        event.player.world.spawnParticle(Particle.PORTAL, playerLocation.x - 0.5, playerLocation.y, playerLocation.z - 0.5, 25, 1.0, 2.0, 1.0)
        event.player.world.playSound(event.player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
        event.player.teleport(playerPositions[event.player.uniqueId]?.first()?.second ?: event.player.location)
        event.player.health = playerHealths[event.player.uniqueId]?.first()?.second?.toDouble() ?: event.player.health
        event.player.foodLevel = playerFood[event.player.uniqueId]?.first()?.second?.toInt() ?: event.player.foodLevel
        event.player.world.spawnParticle(Particle.PORTAL, playerLocation.x - 0.5, playerLocation.y, playerLocation.z - 0.5, 25, 1.0, 2.0, 1.0)
        event.player.world.playSound(event.player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
        recentUses[event.player] = Instant.now()
    }

    override fun createItemStack(): ItemStack {
        val item = ItemStack(Material.GREEN_DYE, 1)
        val meta = item.itemMeta!!
        meta.setLocalizedName("time_stone")
        meta.setDisplayName("${ChatColor.GOLD}Time Stone")
        return item.apply { itemMeta = meta }
    }

    override fun isValid(item: ItemStack?): Boolean {
        if(item == null) return false
        return if(item.hasItemMeta()) item.itemMeta!!.localizedName == "time_stone" else false
    }

    override fun toString(): String {
        return "TimeStone"
    }
}