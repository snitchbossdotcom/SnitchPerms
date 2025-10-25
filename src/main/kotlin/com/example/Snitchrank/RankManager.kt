package com.example.snitchrank

import org.bukkit.configuration.file.YamlConfiguration

data class Rank(val name: String, val prefix: String, val permissions: List<String>)

class RankManager(private val plugin: SnitchRank) {
    private val ranks = mutableMapOf<String, Rank>()

    init {
        loadRanks()
    }

    private fun loadRanks() {
        val config = plugin.config
        val ranksSection = config.getConfigurationSection("ranks") ?: return
        for (rankName in ranksSection.getKeys(false)) {
            val prefix = ranksSection.getString("$rankName.prefix") ?: ""
            val permissions = ranksSection.getStringList("$rankName.permissions")
            ranks[rankName] = Rank(rankName, prefix, permissions)
        }
    }

    fun getRank(name: String): Rank? = ranks[name.lowercase()]

    fun getRanks(): List<String> = ranks.keys.toList()
}
