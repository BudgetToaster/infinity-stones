package com.github.budgettoaster.infinitystones.commands

import com.github.budgettoaster.infinitystones.InfinityStoneLocation
import com.github.budgettoaster.infinitystones.InfinityStoneManager
import com.github.budgettoaster.infinitystones.plugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class Locate : SubCommand(
    true,
    "locate",
    "/stones locate",
    "infinitystones.basic.locate",
    "Points your compass towards the nearest infinity stone."
) {
    private val locationCache = HashMap<UUID, InfinityStoneLocation>()

    init {
        plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, {
            for(player in Bukkit.getOnlinePlayers()) {
                val cached = locationCache[player.uniqueId] ?: continue
                // Not perfect, but necessary because compassTarget is not saved locally
                if(player.compassTarget.blockX == cached.blockX && player.compassTarget.blockY == cached.blockY && player.compassTarget.blockZ == cached.blockZ) {
                    cached.update()
                    player.compassTarget = cached
                }
                else locationCache.remove(player.uniqueId)
            }
        }, 3, 20)
    }

    override fun execute(sender: CommandSender, args: List<String>): String {
        sender as Player

        if(sender.compassTarget is InfinityStoneLocation) {
            sender.compassTarget = sender.bedSpawnLocation ?: sender.server.worlds[0].spawnLocation
            return "${ChatColor.YELLOW}No longer tracking infinity stones."
        }
        else {
            val playerLocation = sender.location
            val nearestStone = InfinityStoneManager.stoneLocations.entries.maxWithOrNull(Comparator.comparingDouble {
                if(it.value != sender) it.value.location.distanceSquared(playerLocation) else Double.MIN_VALUE
            })

            return if(nearestStone == null) {
                "${ChatColor.RED}No infinity stones to be tracked."
            }
            else {
                val currentLoc = nearestStone.value.location
                val tracker = InfinityStoneLocation(nearestStone.key,
                    currentLoc.world!!, currentLoc.x, currentLoc.y, currentLoc.z)
                sender.compassTarget = tracker
                locationCache[sender.uniqueId] = tracker
                "${ChatColor.YELLOW}Now tracking nearest infinity stone."
            }
        }
    }
}