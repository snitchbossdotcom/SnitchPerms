package com.example.snitchrank

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.UUID

class SnitchRank : JavaPlugin() {
    private lateinit var rankManager: RankManager
    private lateinit var playerData: PlayerData

    override fun onEnable() {
        saveDefaultConfig()
        rankManager = RankManager(this)
        playerData = PlayerData(this)
        logger.info("SnitchRank enabled!")
    }

    override fun onDisable() {
        playerData.save()
        logger.info("SnitchRank disabled!")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("rank", ignoreCase = true)) {
            return handleRankCommand(sender, args)
        } else if (command.name.equals("permission", ignoreCase = true)) {
            return handlePermissionCommand(sender, args)
        }
        return false
    }

    private fun handleRankCommand(sender: CommandSender, args: Array<out String>): Boolean {
        if (!sender.hasPermission("snitchrank.admin")) {
            sender.sendMessage("§cNo permission!")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§cUsage: /rank <set|remove|list> [player] [rank]")
            return true
        }

        when (args[0].lowercase()) {
            "set" -> {
                if (args.size < 3) {
                    sender.sendMessage("§cUsage: /rank set <player> <rank>")
                    return true
                }
                val player = server.getPlayer(args[1]) ?: run {
                    sender.sendMessage("§cPlayer not found!")
                    return true
                }
                val rank = rankManager.getRank(args[2]) ?: run {
                    sender.sendMessage("§cRank not found!")
                    return true
                }
                playerData.setRank(player.uniqueId, rank)
                sender.sendMessage("§aSet ${player.name}'s rank to ${rank.name}")
            }
            "remove" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cUsage: /rank remove <player>")
                    return true
                }
                val player = server.getPlayer(args[1]) ?: run {
                    sender.sendMessage("§cPlayer not found!")
                    return true
                }
                playerData.removeRank(player.uniqueId)
                sender.sendMessage("§aRemoved rank from ${player.name}")
            }
            "list" -> {
                sender.sendMessage("§aAvailable ranks: ${rankManager.getRanks().joinToString(", ")}")
            }
            else -> sender.sendMessage("§cUnknown subcommand!")
        }
        return true
    }

    private fun handlePermissionCommand(sender: CommandSender, args: Array<out String>): Boolean {
        if (!sender.hasPermission("snitchrank.admin")) {
            sender.sendMessage("§cNo permission!")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§cUsage: /permission <add|remove|list> [player] [permission]")
            return true
        }

        when (args[0].lowercase()) {
            "add" -> {
                if (args.size < 3) {
                    sender.sendMessage("§cUsage: /permission add <player> <permission>")
                    return true
                }
                val player = server.getPlayer(args[1]) ?: run {
                    sender.sendMessage("§cPlayer not found!")
                    return true
                }
                playerData.addPermission(player.uniqueId, args[2])
                sender.sendMessage("§aAdded permission ${args[2]} to ${player.name}")
            }
            "remove" -> {
                if (args.size < 3) {
                    sender.sendMessage("§cUsage: /permission remove <player> <permission>")
                    return true
                }
                val player = server.getPlayer(args[1]) ?: run {
                    sender.sendMessage("§cPlayer not found!")
                    return true
                }
                playerData.removePermission(player.uniqueId, args[2])
                sender.sendMessage("§aRemoved permission ${args[2]} from ${player.name}")
            }
            "list" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cUsage: /permission list <player>")
                    return true
                }
                val player = server.getPlayer(args[1]) ?: run {
                    sender.sendMessage("§cPlayer not found!")
                    return true
                }
                val permissions = playerData.getPermissions(player.uniqueId)
                sender.sendMessage("§aPermissions for ${player.name}: ${permissions.joinToString(", ")}")
            }
            else -> sender.sendMessage("§cUnknown subcommand!")
        }
        return true
    }
}
