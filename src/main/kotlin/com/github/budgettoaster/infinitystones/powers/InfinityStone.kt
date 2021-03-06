package com.github.budgettoaster.infinitystones.powers

import org.bukkit.inventory.ItemStack

interface InfinityStone {
    companion object {
        val values: List<InfinityStone> = listOf(MindStone, PowerStone, RealityStone, SoulStone, SpaceStone, TimeStone)

        fun valueOf(name : String) : InfinityStone? {
            return when(name) {
                "MindStone" -> MindStone
                "PowerStone" -> PowerStone
                "RealityStone" -> RealityStone
                "SoulStone" -> SoulStone
                "SpaceStone" -> SpaceStone
                "TimeStone" -> TimeStone
                else -> null
            }
        }
    }

    val friendlyName : String

    fun createItemStack(): ItemStack

    fun isValid(item : ItemStack?) : Boolean
}