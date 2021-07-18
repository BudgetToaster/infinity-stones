package com.github.budgettoaster.infinitystones

import com.github.budgettoaster.infinitystones.powers.*
import org.bukkit.entity.Sheep
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

fun ItemStack?.isStone(): Boolean {
    this?.itemMeta ?: return false
    for(infinityStone in listOf(MindStone, PowerStone, RealityStone, SoulStone, SpaceStone, TimeStone)) {
        if(infinityStone.isValid(this)) return true
    }
    return false
}

val ItemStack?.infinityStoneType: InfinityStone? get() {
    this?.itemMeta ?: return null
    for(infinityStone in listOf(MindStone, PowerStone, RealityStone, SoulStone, SpaceStone, TimeStone)) {
        if(infinityStone.isValid(this)) return infinityStone
    }
    return null
}

fun Inventory?.containsStone(): Boolean {
    this ?: return false
    for(slot: ItemStack? in this.contents) {
        if(slot?.isStone() ?: continue) return true
    }
    return false
}

fun Inventory?.containsStone(infinityStone: InfinityStone): Boolean {
    this ?: return false
    for(slot in this.contents) {
        if(infinityStone.isValid(slot)) return true
    }
    return false
}

private val whoDyedMap = WeakHashMap<UUID, UUID>()
var Sheep.whoDyed: UUID?
    get() = whoDyedMap[this.uniqueId]
    set(value) {
        if(value == null) whoDyedMap.remove(this.uniqueId)
        else whoDyedMap[this.uniqueId] = value
    }