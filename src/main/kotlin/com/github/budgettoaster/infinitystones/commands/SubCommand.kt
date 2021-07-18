package com.github.budgettoaster.infinitystones.commands

import org.bukkit.command.CommandSender

abstract class SubCommand(
    val playerOnly: Boolean,
    val label: String,
    val usage: String,
    val permission: String,
    val description: String,
    val aliases: List<String> = emptyList()
) {
    fun init(): Boolean {
        return true
    }

    abstract fun execute(sender: CommandSender, args: List<String>): String?
}