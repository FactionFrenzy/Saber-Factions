package com.massivecraft.factions.cmd.points;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPoints extends FCommand {

    /**
     * @author Driftay
     */

    public CmdPointsRemove cmdPointsRemove = new CmdPointsRemove();
    public CmdPointsSet cmdPointsSet = new CmdPointsSet();
    public CmdPointsAdd cmdPointsAdd = new CmdPointsAdd();
    public CmdPointsBalance cmdPointsBalance = new CmdPointsBalance();

    public CmdPoints() {
        super();
        this.getAliases().addAll(Aliases.points_points);
        this.setRequirements(new CommandRequirements.Builder(Permission.POINTS)
                .build());


        this.addSubCommand(this.cmdPointsBalance);
        this.addSubCommand(this.cmdPointsAdd);
        this.addSubCommand(this.cmdPointsRemove);
        this.addSubCommand(this.cmdPointsSet);
        this.addSubCommand(this.cmdPointsBalance);
    }


    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("f-points.Enabled", true)) {
            context.msg(TL.GENERIC_DISABLED, "Faction Points");
            return;
        }

        if (context.fPlayer.isAdminBypassing() || context.player.isOp()) {
            context.commandChain.add(this);
        } else {
            context.commandChain.add(this.cmdPointsBalance);
        }

        FactionsPlugin.getInstance().cmdAutoHelp.execute(context);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_POINTS_DESCRIPTION;
    }


}
