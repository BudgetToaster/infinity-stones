package com.github.budgettoaster.infinitystones.powers

import com.github.budgettoaster.infinitystones.containsStone
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

object PowerStone: Listener, InfinityStone {
    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        if (event.damager !is Player) return
        if ((event.damager as Player).inventory.containsStone(PowerStone)) {
            event.damage *= 1.5
        }
    }

    override val friendlyName: String
        get() = "Power Stone"

    override fun createItemStack(): ItemStack {
        val item = ItemStack(Material.PURPLE_DYE, 1)
        val meta = item.itemMeta!!
        meta.setLocalizedName("power_stone")
        meta.setDisplayName("${ChatColor.GOLD}${friendlyName}")
        return item.apply { itemMeta = meta }
    }

    override fun isValid(item: ItemStack?): Boolean {
        if(item == null) return false
        return if(item.hasItemMeta()) item.itemMeta!!.localizedName == "power_stone" else false
    }

    override fun toString(): String {
        return "PowerStone"
    }
}