package net.example.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TradeManager {
    private final Map<UUID, UUID> invitations = new HashMap<>();

    public void invite(UUID inviter, UUID target) {
        invitations.put(inviter, target);
    }

    public UUID getInviterFor(UUID target) {
        for (Map.Entry<UUID, UUID> e : invitations.entrySet()) {
            if (e.getValue().equals(target)) return e.getKey();
        }
        return null;
    }

    public void removeInvitation(UUID inviter) {
        invitations.remove(inviter);
    }
}
