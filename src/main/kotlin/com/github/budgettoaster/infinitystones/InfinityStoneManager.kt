package com.github.budgettoaster.infinitystones

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.budgettoaster.infinitystones.powers.InfinityStone
import org.bukkit.Location
import org.bukkit.Material
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
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import java.io.File
import java.io.IOError
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

object InfinityStoneManager : Listener {
    private val mapper = ObjectMapper()
    var stoneLocations = HashMap<InfinityStone, Entity>()

    fun save() {
        val file = File(plugin.dataFolder, "locations.json")
        val root = mapper.createObjectNode()
        for(entry in stoneLocations) {
            val value = entry.value
            if(!value.isValid) continue
            root.with(entry.key.toString()).put("x", value.location.x)
            root.with(entry.key.toString()).put("y", value.location.y)
            root.with(entry.key.toString()).put("z", value.location.z)
            root.with(entry.key.toString()).put("world", value.location.world!!.uid.toString())
        }
        mapper.writeValue(file, root)
    }

    fun load() {
        val file = File(plugin.dataFolder, "locations.json")
        file.createNewFile()
        try {
            val root = mapper.readTree(file)
            for(entry in root.fields()) {
                val location = Location(
                    plugin.server.getWorld(UUID.fromString(entry.value["world"].asText())),
                    entry.value["x"].asDouble(),
                    entry.value["y"].asDouble(),
                    entry.value["z"].asDouble()
                )

                location.chunk.load(false)
            }
        }
        catch(e: IOError) {}
    }

    fun startLocationCheckLoop() {
        plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, { validateLocations() }, 8, 20)
    }

    private fun validateLocations() {
        println(stoneLocations)
        for(entry in HashSet(stoneLocations.entries)) {
            val value = entry.value
            val key = entry.key
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

        // new entries
        for(world in plugin.server.worlds) {
            for(entity in world.getEntitiesByClasses(Item::class.java, HumanEntity::class.java)) {
                if(entity is Item) {
                    val itemStack = entity.itemStack
                    val type = itemStack.infinityStoneType ?: continue
                    stoneLocations[type] = entity
                }
                else {
                    entity as HumanEntity
                    for(itemStack in entity.inventory) {
                        val type = itemStack.infinityStoneType ?: continue
                        stoneLocations[type] = entity
                    }
                }
            }
        }
    }

    // Drop infinity stones on leave
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

    //
    // PREVENT DESTRUCTION
    //

    @EventHandler
    fun onItemDespawn(ev: ItemDespawnEvent) {
        val type = ev.entity.itemStack.infinityStoneType ?: return
        stoneLocations[type] = ev.entity
        ev.isCancelled = true
    }

    @EventHandler
    fun onEntityCombust(ev: EntityCombustEvent) {
        val entity = ev.entity
        if(entity is Item) {
            val type = entity.itemStack.infinityStoneType ?: return
            stoneLocations.remove(type)
        }
    }

    //
    // PREVENT ILLEGAL USAGE
    //

    @EventHandler
    fun onInteract(ev: PlayerInteractEntityEvent) {
        if(ev.player.inventory.itemInMainHand.isStone()) {
            ev.isCancelled = true
        }
    }

    //
    // ITEM TRACKING EVENTS
    //

    @EventHandler
    fun onItemSpawn(ev: ItemSpawnEvent) {
        val type = ev.entity.itemStack.infinityStoneType ?: return
        stoneLocations[type] = ev.entity
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
    fun onItemDrop(ev: PlayerDropItemEvent) {
        val item = ev.itemDrop
        val stoneType = item.itemStack.infinityStoneType ?: return
        stoneLocations[stoneType] = item
    }

    //
    // PREVENT MOVING TO CONTAINER
    //

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
}