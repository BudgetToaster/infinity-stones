package com.github.budgettoaster.infinitystones.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class BaseCommandTabCompleter : TabCompleter {
    private val completionList = ArrayList<String>()

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        return when {
            args.isEmpty() -> completionList
            args.size > 1 -> emptyList()
            else -> completionList.filter { str: String -> str.startsWith(args[0]) }
        }
    }

    init {
        for (command in BaseCommand.subCommands) {
            completionList.add(command.label)
            completionList.addAll(command.aliases)
        }
    }
}