package com.github.budgettoaster.infinitystones.powers

import com.github.budgettoaster.infinitystones.containsStone
import com.github.budgettoaster.infinitystones.plugin
import org.bukkit.Bukkit.getServer
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


object SpaceStone : Listener, InfinityStone {
    override val friendlyName: String
        get() = "Space Stone"

    init {
        val scheduler = getServer().scheduler
        scheduler.scheduleSyncRepeatingTask(plugin, {
            for (player in plugin.server.onlinePlayers) {
                if (player.inventory.containsStone(SpaceStone)) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 200, 1))
                    player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 200, 2))
                }
            }
        }, 0L, 200L)
    }

    override fun createItemStack(): ItemStack {
        val item = ItemStack(Material.BLUE_DYE, 1)
        val meta = item.itemMeta!!
        meta.setLocalizedName("space_stone")
        meta.setDisplayName("${ChatColor.GOLD}Space Stone")
        return item.apply { itemMeta = meta }
    }

    override fun isValid(item: ItemStack?): Boolean {
        if(item == null) return false
        return if(item.hasItemMeta()) item.itemMeta!!.localizedName == "space_stone" else false
    }

    override fun toString(): String {
        return "SpaceStone"
    }
}