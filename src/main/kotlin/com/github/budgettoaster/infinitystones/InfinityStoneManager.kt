package com.github.budgettoaster.infinitystones

import com.github.budgettoaster.infinitystones.powers.InfinityStone
import org.bukkit.entity.Entity
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.PlayerInventory

object InfinityStoneManager : Listener {
    var stoneLocations = HashMap<InfinityStone, Entity>()

    fun startLocationCheckLoop() {
        plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, { validateLocations() }, 8, 20)
    }

    private fun validateLocations() {
        for(entry in HashSet(stoneLocations.entries)) {
            val value = entry.value
            val key = entry.key
            if(!value.isValid) {
                stoneLocations.remove(key)
            }
            else {
                if(value is HumanEntity) {
                    if(!value.inventory.containsStone(key)) {
                        stoneLocations.remove(key)
                    }
                }
                else if(value is Item) {
                    if(!key.isValid(value.itemStack)) {
                        stoneLocations.remove(key)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerLeave(ev: PlayerQuitEvent) {
        val inv = ev.player.inventory
        for(slot in 0 until inv.size) {
            val item = inv.getItem(slot) ?: continue
            val stoneType = item.infinityStoneType ?: continue
            val itemEntity = ev.player.location.world!!.dropItem(ev.player.location, item)
            stoneLocations[stoneType] = itemEntity
            inv.setItem(slot, null)
        }
    }

    @EventHandler
    fun onItemDrop(ev: PlayerDropItemEvent) {
        val item = ev.itemDrop
        val stoneType = item.itemStack.infinityStoneType ?: return
        stoneLocations[stoneType] = item
    }

    @EventHandler
    fun onItemPickup(ev: EntityPickupItemEvent) {
        val type = ev.item.itemStack.infinityStoneType ?: return
        if(ev.entity is Player) {
            ev.entity.isEmpty
            stoneLocations[type] = ev.entity
        }
        else {
            ev.isCancelled = true
        }
    }

    @EventHandler
    fun onItemPickup(ev: InventoryPickupItemEvent) {
        val type = ev.item.itemStack.infinityStoneType ?: return
        if(ev.inventory.holder is Player) {
            stoneLocations[type] = ev.inventory.holder as Player
        }
        else {
            ev.isCancelled = true
        }
    }

    @EventHandler
    fun onItemDespawn(ev: ItemDespawnEvent) {
        val type = ev.entity.itemStack.infinityStoneType ?: return
        stoneLocations[type] = ev.entity
        ev.isCancelled = true
    }

    @EventHandler
    fun onItemMove(ev: InventoryMoveItemEvent) {
        val type = ev.item.infinityStoneType ?: return
        if(ev.destination != ev.source) ev.isCancelled = true
    }

    @EventHandler
    fun onInventoryClick(ev: InventoryClickEvent) {
        val type = ev.cursor.infinityStoneType ?: return
        if(ev.clickedInventory !is PlayerInventory) ev.isCancelled = true
    }

    @EventHandler
    fun onItemDrag(ev: InventoryDragEvent) {
        val type = ev.oldCursor.infinityStoneType ?: return
        val top = ev.view.topInventory
        val bottom = ev.view.bottomInventory
        if(top.type != InventoryType.PLAYER) {
            for(slot in 0 until top.size) {
                if(ev.newItems.containsKey(slot)) {
                    ev.isCancelled = true
                    return
                }
            }
        }
        if(bottom.type != InventoryType.PLAYER) {
            for(slot in 0 until top.size) {
                if(ev.newItems.containsKey(slot + top.size)) {
                    ev.isCancelled = true
                    return
                }
            }
        }
    }

    @EventHandler
    fun onEntityCombust(ev: EntityCombustEvent) {
        val entity = ev.entity
        if(entity is Item) {
            val type = entity.itemStack.infinityStoneType ?: return
            stoneLocations.remove(type)
        }
    }

    @EventHandler
    fun onEntityDeath(ev: EntityDeathEvent) {
        val entity = ev.entity
        if(entity is Item) {
            val type = entity.itemStack.infinityStoneType ?: return
            stoneLocations.remove(type)
        }
    }

    @EventHandler
    fun onInteract(ev: PlayerInteractEntityEvent) {
        if(ev.player.inventory.itemInMainHand.isStone()) {
            ev.isCancelled = true
        }
    }

    @EventHandler
    fun onEntityLoad(ev: ItemSpawnEvent) {
        val type = ev.entity.itemStack.infinityStoneType ?: return
        if(stoneLocations.containsKey(type)) ev.isCancelled = true
        else stoneLocations[type] = ev.entity
    }
}