package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSpawnerLock extends FCommand {

    /**
     * @author Illyria Team
     */

    public CmdSpawnerLock() {
        super();
        this.getAliases().addAll(Aliases.spawnerlock);

        this.setRequirements(new CommandRequirements.Builder(Permission.LOCKSPAWNERS)
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        Conf.spawnerLock = !Conf.spawnerLock;
        context.msg(TL.COMMAND_SPAWNER_LOCK_TOGGLED, Conf.spawnerLock ? CC.translate("&4Disabled") : CC.translate("&aEnabled"));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SPAWNER_LOCK_DESCRIPTION;
    }
}
