package com.github.budgettoaster.infinitystones

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.budgettoaster.infinitystones.powers.InfinityStone
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import java.util.*

object InfinityStoneScatterer: Listener {
    private val mapper = ObjectMapper()
    private var random = Random()
    private val blockWhitelist: ArrayList<Material> = ArrayList()
    private val maxHeight = plugin.config.getInt("max infinity stone spawn height", 32)
    private val infinityStoneSpawnChance = plugin.config.getDouble("infinity stone spawn chance", 0.0)

    init {
        val blockWhitelistNames = plugin.config.getStringList("holding blocks")
        for(str in blockWhitelistNames) {
            try {
                blockWhitelist.add(Material.valueOf(str))
            }
            catch(e : IllegalArgumentException) {
                plugin.logger.warning("Invalid block type $str. Check your config")
            }
        }
    }

    @EventHandler
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val stoneTypes = ArrayList(InfinityStone.values).filter { !InfinityStoneManager.stoneLocations.containsKey(it) }
        if(stoneTypes.isEmpty()) return
        if(!blockWhitelist.contains(event.block.type)) return
        if(event.block.y > maxHeight) return

        // Prevents player from picking up ore, the placing it again, and repeating until they gets an infinity stone
        if(event.player.inventory.itemInMainHand.containsEnchantment(Enchantment.SILK_TOUCH) ||
                event.player.gameMode == GameMode.CREATIVE) return

        val isInfinityStone = random.nextDouble() <= infinityStoneSpawnChance
        if(isInfinityStone) {
            val stoneType = stoneTypes[random.nextInt(stoneTypes.size)]
            val item = stoneType.createItemStack()
            if(event.player.inventory.addItem(item).isEmpty()) {
                InfinityStoneManager.stoneLocations[stoneType] = event.player
            }
        }
    }
}
