package com.github.budgettoaster.infinitystones.commands

import com.github.budgettoaster.infinitystones.InfinityStoneManager
import com.github.budgettoaster.infinitystones.powers.*
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Give : SubCommand(
    true,
    "give",
    "/stones give [Type]",
    "infinitystones.admin.give",
    "Gives the sender an infinity stone."
) {
    override fun execute(sender: CommandSender, args: List<String>): String? {
        val type = when(args.joinToString(" ").lowercase().removeSuffix("stone").trim()) {
            "mind" -> MindStone
            "power" -> PowerStone
            "reality" -> RealityStone
            "soul" -> SoulStone
            "space" -> SpaceStone
            "time" -> TimeStone
            else -> return null
        }
        val entity = InfinityStoneManager.stoneLocations[type]
        return if(entity == null) {
            if((sender as Player).inventory.addItem(type.createItemStack()).isEmpty())
                "${ChatColor.YELLOW}Infinity stone given."
            else
                "${ChatColor.YELLOW}You don't have room for that."
        } else
            "${ChatColor.RED}Cannot give you that infinity stone because it is already in circulation. Use the whereis command instead."
    }
}