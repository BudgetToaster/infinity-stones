package com.github.budgettoaster.infinitystones.powers

import com.github.budgettoaster.infinitystones.InfinityStoneManager
import com.github.budgettoaster.infinitystones.plugin
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable


object MindStone: Listener, InfinityStone {
    override val friendlyName: String
        get() = "Mind Stone"

    @EventHandler
    fun onEntityTarget(event: EntityTargetLivingEntityEvent) {
        if(event.target == InfinityStoneManager.stoneLocations[this])
            event.isCancelled = true
    }

    override fun createItemStack(): ItemStack {
        val item = ItemStack(Material.YELLOW_DYE, 1)
        val meta = item.itemMeta!!
        meta.setLocalizedName("mind_stone")
        meta.setDisplayName("${ChatColor.GOLD}Mind Stone")
        return item.apply { itemMeta = meta }
    }

    override fun isValid(item: ItemStack?): Boolean {
        if(item == null) return false
        return if(item.hasItemMeta()) item.itemMeta!!.localizedName == "mind_stone" else false
    }

    override fun toString(): String {
        return "MindStone"
    }
}