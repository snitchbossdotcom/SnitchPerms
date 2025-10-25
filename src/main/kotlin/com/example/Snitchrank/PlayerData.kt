package com.example.snitchrank

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.UUID

class PlayerData(private val plugin: SnitchRank) {
    private val dataFile = File(plugin.dataFolder, "players.yml")
    private val dataConfig: YamlConfiguration

    init {
        if (!dataFile.exists()) {
            dataFile.parentFile.mkdirs()
            dataFile.createNewFile()
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile)
    }

    fun setRank(playerUUID: UUID, rank: Rank) {
        dataConfig.set("$playerUUID.rank", rank.name)
        save()
    }

    fun removeRank(playerUUID: UUID) {
        dataConfig.set("$playerUUID.rank", null)
        save()
    }

    fun getRank(playerUUID: UUID): Rank? {
        val rankName = dataConfig.getString("$playerUUID.rank") ?: return null
        return plugin.rankManager.getRank(rankName)
    }

    fun addPermission(playerUUID: UUID, permission: String) {
        val permissions = dataConfig.getStringList("$playerUUID.permissions").toMutableList()
        if (!permissions.contains(permission)) {
            permissions.add(permission)
            dataConfig.set("$playerUUID.permissions", permissions)
            save()
        }
    }

    fun removePermission(playerUUID: UUID, permission: String) {
        val permissions = dataConfig.getStringList("$playerUUID.permissions").toMutableList()
        permissions.remove(permission)
        dataConfig.set("$playerUUID.permissions", permissions)
        save()
    }

    fun getPermissions(playerUUID: UUID): List<String> {
        val rank = getRank(playerUUID)
        val playerPermissions = dataConfig.getStringList("$playerUUID.permissions")
        return if (rank != null) {
            (rank.permissions + playerPermissions).distinct()
        } else {
            playerPermissions
        }
    }

    fun save() {
        dataConfig.save(dataFile)
    }
}
