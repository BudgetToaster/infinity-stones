package com.github.budgettoaster.infinitystones

import com.github.budgettoaster.infinitystones.powers.InfinityStone
import org.bukkit.Location
import org.bukkit.World

class InfinityStoneLocation(
    val stone: InfinityStone,
    world: World,
    x: Double,
    y: Double,
    z: Double
) : Location(world, x, y, z) {
    fun update() {
        val loc = InfinityStoneManager.stoneLocations[stone]?.location ?: return
        x = loc.x
        y = loc.y
        z = loc.z
        world = loc.world
    }
}