package com.github.budgettoaster.infinitystones.powers

import com.github.budgettoaster.infinitystones.plugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.BoundingBox
import java.time.Duration
import java.time.Instant

object RealityStone: Listener, InfinityStone {
    private var lastUse = Instant.EPOCH
    override val friendlyName: String
        get() = "Reality Stone"

    @EventHandler
    fun onInteractEvent(event: PlayerInteractEvent) {
        if(event.action != Action.RIGHT_CLICK_BLOCK && event.action != Action.LEFT_CLICK_BLOCK) return
        if(!isValid(event.player.inventory.itemInMainHand)) return
        if(Duration.between(lastUse, Instant.now()).toMillis() < 50) return

        if(event.action != Action.RIGHT_CLICK_BLOCK) return
        event.isCancelled = true
        val block: Block = event.clickedBlock?.getRelative(event.blockFace) ?: return
        if(overlapsPlayer(block)) return
        if(block.type != Material.AIR) return

        block.type = Material.BARRIER
        lastUse = Instant.now()

        object: BukkitRunnable() {
            override fun run() {
                block.type = Material.AIR
            }
        }.runTaskLater(plugin, plugin.config.getLong("reality block lifetime"))
    }

    override fun createItemStack(): ItemStack {
        val item = ItemStack(Material.RED_DYE, 1)
        val meta = item.itemMeta!!
        meta.setLocalizedName("reality_stone")
        meta.setDisplayName("${ChatColor.GOLD}Reality Stone")
        return item.apply { itemMeta = meta }
    }

    override fun isValid(item: ItemStack?): Boolean {
        if(item == null) return false
        return if(item.hasItemMeta()) item.itemMeta!!.localizedName == "reality_stone" else false
    }

    override fun toString(): String {
        return "RealityStone"
    }

    private fun overlapsPlayer(block: Block): Boolean {
        val bounds = BoundingBox(block.location.x, block.location.y, block.location.z,
            block.location.x + 1, block.location.y + 1, block.location.z + 1)
        for(player in Bukkit.getOnlinePlayers())
            if(bounds.overlaps(player.boundingBox))
                return true
        return false
    }
}