package com.github.budgettoaster.infinitystones.commands

import com.github.budgettoaster.infinitystones.plugin
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.text.MessageFormat

object BaseCommand : CommandExecutor {
    val subCommands: MutableList<SubCommand> = ArrayList()

    fun init() {
        subCommands.add(Locate())
        subCommands.add(WhereIs())
        subCommands.add(Give())
        for (c in subCommands) {
            if (!c.init()) return
        }
        plugin.getCommand("stones")!!.setExecutor(this)
        plugin.getCommand("stones")!!.tabCompleter = BaseCommandTabCompleter()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val secondaryCommand: String = if (args.isNotEmpty()) args[0].lowercase() else "help"
        val otherArgs: List<String> = if(args.isEmpty()) emptyList() else listOf(*args).subList(1, args.size)

        for (subCommand in subCommands) {
            if (secondaryCommand != subCommand.label && !subCommand.aliases.contains(secondaryCommand)) continue
            if (!sender.hasPermission(subCommand.permission)) sender.sendMessage(
                ChatColor.RED.toString() + "You don't have permission to use that command."
            ) else if (subCommand.playerOnly && sender !is Player) sender.sendMessage(
                ChatColor.RED.toString() + "Only players can execute that command."
            ) else {
                val out = subCommand.execute(sender, otherArgs)
                if (out == null) sender.sendMessage(
                    MessageFormat.format(
                        "{0}Usage: {1}",
                        ChatColor.RED,
                        subCommand.usage
                    )
                )
                else sender.sendMessage(out)
            }
            return true
        }
        sender.sendMessage("Unknown command. Type /religion help for help.")
        return true
    }
}