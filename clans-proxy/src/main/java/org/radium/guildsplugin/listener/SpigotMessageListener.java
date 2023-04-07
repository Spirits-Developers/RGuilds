package org.radium.guildsplugin.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.radium.guildsplugin.Core;
import org.radium.guildsplugin.manager.object.guild.Guild;
import org.radium.guildsplugin.manager.object.member.GuildMember;

public class SpigotMessageListener implements Listener {
    @EventHandler
    public void onReceive(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase("BungeeCord"))
            return;
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();
        if (!subChannel.equals("Guilds"))
            return;
        String command = in.readUTF();
        switch (command){
            case "UpdateGuildStats":
                String killer = in.readUTF();
                String victim = in.readUTF();
                GuildMember guildVictimMember = Core.getInstance().getGuildMemberManager().getGuildMember(victim);
                if (guildVictimMember == null){
                    return;
                }
                Guild victimGuild = Core.getInstance().getGuildManager().getGuild(guildVictimMember.getGuildId());
                if (victimGuild == null){
                    return;
                }
                victimGuild.getSettings().getGuildStats().addStat("deaths", 1);
                victimGuild.getSettings().getGuildStats().addStat("points",
                        Core.getInstance().getSettings().getConfig().getInt("Points.PointsPerDeath"));
                if (!Core.getInstance().getSettings().getConfig().getBoolean("Points.PointsCanBeNegative:")){
                    if (victimGuild.getSettings().getGuildStats().getGlobalPoints() <= 0){
                        victimGuild.getSettings().getGuildStats().setGlobalPoints(0);
                    }
                }
                GuildMember guildKillerMember = Core.getInstance().getGuildMemberManager().getGuildMember(killer);
                if (guildKillerMember == null){
                    return;
                }
                Guild killerGuild = Core.getInstance().getGuildManager().getGuild(guildKillerMember.getGuildId());
                if (killerGuild == null){
                    return;
                }
                killerGuild.getSettings().getGuildStats().addStat("kills", 1);
                killerGuild.getSettings().getGuildStats().addStat("points",
                        Core.getInstance().getSettings().getConfig().getInt("Points.PointsPerKill"));
        }
    }
}
