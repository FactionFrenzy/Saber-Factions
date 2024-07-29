package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.ChatColor;

public class CmdColeader extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdColeader() {
        super();
        this.getAliases().addAll(Aliases.coleader);

        this.getRequiredArgs().add("name");

        this.setRequirements(new CommandRequirements.Builder(Permission.COLEADER)
                .memberOnly()
                .withRole(Role.LEADER)
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        FPlayer you = context.argAsBestFPlayerMatch(0);
        if (you == null) {
            Component msg = TL.COMMAND_COLEADER_CANDIDATES.toComponent().color(TextUtil.kyoriColor(ChatColor.GOLD));
            for (FPlayer player : context.faction.getFPlayersWhereRole(Role.NORMAL)) {
                String s = player.getName();
                msg.append(Component.text(s + " ").color(TextUtil.kyoriColor(ChatColor.WHITE)).hoverEvent(TL.COMMAND_MOD_CLICKTOPROMOTE.toComponent().append(Component.text(s))).clickEvent(ClickEvent.runCommand("/" + Conf.baseCommandAliases.get(0) + " coleader " + s)));
            }
            for (FPlayer player : context.faction.getFPlayersWhereRole(Role.MODERATOR)) {
                String s = player.getName();
                msg.append(Component.text(s + " ").color(TextUtil.kyoriColor(ChatColor.WHITE)).hoverEvent(TL.COMMAND_MOD_CLICKTOPROMOTE.toComponent().append(Component.text(s))).clickEvent(ClickEvent.runCommand("/" + Conf.baseCommandAliases.get(0) + " coleader " + s)));
            }

            context.sendComponent(msg);
            return;
        }

        boolean permAny = Permission.COLEADER_ANY.has(context.sender, false);
        Faction targetFaction = you.getFaction();

        if (targetFaction != context.faction && !permAny) {
            context.msg(TL.COMMAND_MOD_NOTMEMBER, you.describeTo(context.fPlayer, true));
            return;
        }

        if (you.isAlt()) {
            return;
        }

        if (context.fPlayer != null && context.fPlayer.getRole() != Role.LEADER && !permAny) {
            context.msg(TL.COMMAND_COLEADER_NOTADMIN);
            return;
        }

        if (you == context.fPlayer && !permAny) {
            context.msg(TL.COMMAND_COLEADER_SELF);
            return;
        }

        if (you.getRole() == Role.LEADER) {
            context.msg(TL.COMMAND_COLEADER_TARGETISADMIN);
            return;
        }

        if (you.getRole() == Role.COLEADER) {
            // Revoke
            you.setRole(Role.MODERATOR);
            targetFaction.msg(TL.COMMAND_COLEADER_REVOKED, you.describeTo(targetFaction, true));
            context.msg(TL.COMMAND_COLEADER_REVOKES, you.describeTo(context.fPlayer, true));
        } else {
            // Give
            you.setRole(Role.COLEADER);
            targetFaction.msg(TL.COMMAND_COLEADER_PROMOTED, you.describeTo(targetFaction, true));
            context.msg(TL.COMMAND_COLEADER_PROMOTES, you.describeTo(context.fPlayer, true));
            FactionsPlugin.instance.getFlogManager().log(targetFaction, FLogType.RANK_EDIT, context.fPlayer.getName(), you.getName(), ChatColor.RED + "Co-Leader");
        }

    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_COLEADER_DESCRIPTION;
    }
}

