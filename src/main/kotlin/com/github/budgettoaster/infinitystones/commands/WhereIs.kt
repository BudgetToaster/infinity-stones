package com.github.budgettoaster.infinitystones.commands

import com.github.budgettoaster.infinitystones.InfinityStoneManager
import com.github.budgettoaster.infinitystones.powers.*
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class WhereIs : SubCommand(
    true,
    "whereis",
    "/stones whereis [Type]",
    "infinitystones.admin.whereis",
    "Tells you the exact location of an infinity stone."
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
        return when (entity) {
            null -> "${ChatColor.RED}That infinity stone is not yet in circulation."
            is Player -> "${ChatColor.YELLOW}That infinity stone is held by ${entity.displayName}."
            else -> "${ChatColor.YELLOW}That infinity stone is at ${entity.location.blockX}, ${entity.location.blockY}, ${entity.location.blockZ}"
        }
    }
}