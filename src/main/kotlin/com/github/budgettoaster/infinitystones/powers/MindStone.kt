package com.github.budgettoaster.infinitystones.powers

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
    private val controlledEntities = HashMap<LivingEntity, Player>()
    override val friendlyName: String
        get() = "Mind Stone"

    init {
        object: BukkitRunnable() {
            override fun run() {
                for(entity in HashMap(controlledEntities)) {
                    if(entity.key.isValid) controlledEntities.remove(entity.key)
                }
            }
        }.runTaskTimer(plugin, 0L, 6000L)
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if(!event.entity.isValid) return
        val inHand = (event.damager as? Player)?.inventory?.itemInMainHand ?: return
        if(isValid(inHand)) {
            controlledEntities[event.entity as LivingEntity] = event.damager as Player
        }
    }

    @EventHandler
    fun onEntityTarget(event: EntityTargetLivingEntityEvent) {
        if(event.target !is Player) return
        val controller = controlledEntities[event.entity] ?: return
        if(event.target == controller) {
            event.isCancelled = true
        }
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