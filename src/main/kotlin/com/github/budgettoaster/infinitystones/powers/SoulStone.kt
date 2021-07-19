package com.github.budgettoaster.infinitystones.powers

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.budgettoaster.infinitystones.plugin
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import java.io.File
import java.io.IOError
import java.util.*

object SoulStone: Listener, InfinityStone {
    val souls = HashSet<UUID>()
    private var mapper = ObjectMapper()
    override val friendlyName: String
        get() = "Soul Stone"

    init {
        load()
    }

    @EventHandler
    fun onDeathEvent(event: PlayerDeathEvent) {
        val attacker = event.entity.killer ?: return
        if(souls.add(event.entity.uniqueId)) save()
        for(slot: ItemStack? in attacker.inventory) {
            if(isValid(slot ?: continue)) {
                val itemMeta = slot.itemMeta
                itemMeta!!.lore = listOf("${ChatColor.YELLOW}Souls: ${souls.size}")
                slot.itemMeta = itemMeta
            }
        }
    }

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        if(event.damager !is Player) return
        if(souls.contains(event.damager.uniqueId))
            event.damage *= 0.667
    }

    fun save() {
        val dataFile = File(plugin.dataFolder, "soulStones.json")
        dataFile.createNewFile()
        if(dataFile.readText().isEmpty()) dataFile.writeText("{}")
        val root = mapper.createArrayNode()
        for(id in souls) {
            root.add(id.toString())
        }
        val prettyPrinter = mapper.writerWithDefaultPrettyPrinter()
        prettyPrinter.writeValue(dataFile, root)
    }

    fun load(dataFile: File = File(plugin.dataFolder, "soulStones.json")) {
        try {
            dataFile.createNewFile()
            val root = mapper.readTree(dataFile)
            for(id in root)
                souls.add(UUID.fromString(id.asText()))
        }
        catch (e: JsonMappingException) {}
        catch(e : IOError) {
            e.printStackTrace()
        }
    }

    override fun createItemStack(): ItemStack {
        val item = ItemStack(Material.ORANGE_DYE, 1)
        val meta = item.itemMeta!!
        meta.setLocalizedName("soul_stone")
        meta.setDisplayName("${ChatColor.GOLD}Soul Stone")
        return item.apply { itemMeta = meta }
    }

    override fun isValid(item: ItemStack?): Boolean {
        if(item == null) return false
        return if(item.hasItemMeta()) item.itemMeta!!.localizedName == "soul_stone" else false
    }

    override fun toString(): String {
        return "SoulStone"
    }
}