package com.beanbeanjuice.beanmoderation.command.freeze;

import com.beanbeanjuice.beanmoderation.utility.Helper;
import com.beanbeanjuice.beanmoderation.utility.chat.MovementManager;
import com.beanbeanjuice.beanmoderation.utility.command.ISubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class FreezeIndividualPlayerSubCommand implements ISubCommand {

    @Override
    public boolean handle(@NotNull CommandSender sender, @NotNull String[] args) {
        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            Helper.sendMessage(sender, Helper.getParsedConfigString("player-not-found").replace("%player%", args[0]));
            return false;
        }

        if (player.hasPermission("beanmoderation.freeze.bypass") || player.isOp()) {
            Helper.sendMessage(sender, Helper.getParsedConfigString("freeze-not-allowed").replace("%player%", player.getName()));
            return false;
        }

        MovementManager.toggleFreezeOnPlayer(player);

        if (MovementManager.isPlayerFrozen(player)) {
            Bukkit.getScheduler().runTaskAsynchronously(Helper.getPlugin(), () -> {
                String title = Helper.getParsedConfigString("freeze-title-message");
                String subtitle = Helper.getParsedConfigString("freeze-secondary-message");
                while (MovementManager.isPlayerFrozen(player)) {
                    player.sendTitle(title, subtitle, 20*5, 20*6, 20*2);
                    try { Thread.sleep(Duration.ofSeconds(5)); }
                    catch (InterruptedException ignored) { }
                }
            });
            Helper.sendMessage(sender, Helper.getParsedConfigString("freeze-frozen").replace("%player%", player.getName()));
            return true;
        }

        Helper.sendMessage(sender, Helper.getParsedConfigString("freeze-unfrozen").replace("%player%", player.getName()));
        return true;
    }

    @Override
    public String getPermission() {
        return "beanmoderation.freeze";
    }

}
