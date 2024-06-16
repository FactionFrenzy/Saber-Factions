package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class  CommandRequirements {

    private Permission permission;
    private boolean playerOnly;
    private boolean memberOnly;
    private Role role;
    private PermissableAction action;
    private Class<? extends BrigadierProvider> brigadier;
    private boolean errorOnManyArgs;
    private boolean disableOnLock;

    private CommandRequirements(Permission permission, boolean playerOnly, boolean memberOnly, Role role, PermissableAction action, Class<? extends BrigadierProvider> brigadier) {
        this.permission = permission;
        this.playerOnly = playerOnly;
        this.memberOnly = memberOnly;
        this.role = role;
        this.action = action;
        this.brigadier = brigadier;
    }

    public boolean computeRequirements(CommandContext context, boolean informIfNot) {
        if (permission == null) {
            return true;
        }

        if (context.player != null) {
            if (!context.fPlayer.hasFaction() && memberOnly) {
                if (informIfNot) context.msg(TL.GENERIC_MEMBERONLY);
                return false;
            }

            if (context.fPlayer.isAdminBypassing()) {
                return true;
            }

            if (!FactionsPlugin.getInstance().perm.has(context.sender, permission.node, informIfNot)) {
                return false;
            }

            if (action != null) {
                if (context.fPlayer.getRole() == Role.LEADER) {
                    return true;
                }
                Access access = context.faction.getAccess(context.fPlayer, action);
                if (access == Access.DENY) {
                    if (informIfNot) context.msg(TL.GENERIC_FPERM_NOPERMISSION, action.getName());
                    return false;
                }
                if (access != Access.ALLOW && role != null && !context.fPlayer.getRole().isAtLeast(role)) {
                    if (informIfNot) context.msg(TL.GENERIC_YOUMUSTBE, role.translation);
                    return false;
                }
                return true;
            } else {
                return role == null || context.fPlayer.getRole().isAtLeast(role);
            }
        } else {
            if (playerOnly && informIfNot) {
                context.sender.sendMessage(TL.GENERIC_PLAYERONLY.toString());
            }
            return !playerOnly || context.sender.hasPermission(permission.node);
        }
    }

    public Permission getPermission() {
        return permission;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    public boolean isMemberOnly() {
        return memberOnly;
    }

    public Role getRole() {
        return role;
    }

    public PermissableAction getAction() {
        return action;
    }

    public Class<? extends BrigadierProvider> getBrigadier() {
        return brigadier;
    }

    public boolean isErrorOnManyArgs() {
        return errorOnManyArgs;
    }

    public boolean isDisableOnLock() {
        return disableOnLock;
    }

    public static class Builder {

        private Permission permission;
        private boolean playerOnly = false;
        private boolean memberOnly = false;
        private Role role = null;
        private PermissableAction action;
        private Class<? extends BrigadierProvider> brigadier;
        private boolean errorOnManyArgs = true;
        private boolean disableOnLock = true;

        public Builder(Permission permission) {
            this.permission = permission;
        }

        public Builder playerOnly() {
            playerOnly = true;
            return this;
        }

        public Builder memberOnly() {
            playerOnly = true;
            memberOnly = true;
            return this;
        }

        public Builder withRole(Role role) {
            this.role = role;
            return this;
        }

        public Builder withAction(PermissableAction action) {
            this.action = action;
            return this;
        }

        public Builder brigadier(Class<? extends BrigadierProvider> brigadier) {
            this.brigadier = brigadier;
            return this;
        }

        public Builder noErrorOnManyArgs() {
            errorOnManyArgs = false;
            return this;
        }

        public Builder noDisableOnLock() {
            disableOnLock = false;
            return this;
        }

        public CommandRequirements build() {
            CommandRequirements requirements = new CommandRequirements(permission, playerOnly, memberOnly, role, action, brigadier);
            requirements.errorOnManyArgs = errorOnManyArgs;
            requirements.disableOnLock = disableOnLock;
            return requirements;
        }
    }
}
