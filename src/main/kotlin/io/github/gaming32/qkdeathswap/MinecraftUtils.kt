package io.github.gaming32.qkdeathswap

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.MessageType
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import net.minecraft.world.World
import net.minecraft.world.chunk.WorldChunk
import org.quiltmc.qkl.wrapper.minecraft.brigadier.RequiredArgumentAction

fun MinecraftServer.broadcast(message: String) {
    broadcast(Text.literal(message))
}

fun MinecraftServer.broadcast(message: Text) {
    playerManager.broadcastSystemMessage(message, MessageType.SYSTEM)
}

val World.spawnLocation: Location
    get() = Location(this, spawnPos.x.toDouble(), spawnPos.y.toDouble(), spawnPos.z.toDouble(), spawnAngle)

fun ServerPlayerEntity.teleport(location: Location) {
    if (isSleeping) {
        wakeUp(true, true)
    }
    stopRiding()
    teleport(
        (location.world ?: world) as ServerWorld,
        location.x,
        location.y,
        location.z,
        location.yaw ?: yaw,
        location.pitch ?: pitch
    )
}

val PlayerEntity.location: Location
    get() = Location(world, x, y, z, yaw, pitch)

fun ticksToMinutesSeconds(ticks: Int): String {
    val minutes = ticks / 1200
    val seconds = ticks / 20 - minutes * 60
    return "${minutes}:${seconds.toString().padStart(2, '0')}"
}

fun WorldChunk.getTopBlock(x: Int, z: Int): Int {
    return getHeightmap(Heightmap.Type.WORLD_SURFACE).get(x, z)
}

fun <S, A, T : ArgumentBuilder<S, T>?> ArgumentBuilder<S, T>.argument(
    name: String,
    type: ArgumentType<A>,
    action: RequiredArgumentAction<S>
) {
    val argument = RequiredArgumentBuilder.argument<S, A>(
        name,
        type
    )
    argument.apply(action)
    then(argument)
}

