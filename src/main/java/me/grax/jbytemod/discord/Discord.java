package me.grax.jbytemod.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import lombok.Getter;
import me.grax.jbytemod.JByteMod;
@Getter

public class Discord {
    private DiscordRPC discordRPC;
    private long startTimestamp;
    private final String applicationId;

    public Discord(String applicationId) {
        this.applicationId = applicationId;
    }

    public void init() {
        discordRPC = DiscordRPC.INSTANCE;
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> JByteMod.LOGGER.log("Discord is now ready.");

        discordRPC.Discord_Initialize(applicationId, handlers, true, "");

        startTimestamp = System.currentTimeMillis();
        updatePresence("Idle ...", "");

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                discordRPC.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
            }
        }, "RPC-Callback-Handler").start();
    }

    public void updatePresence(String details, String state) {
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.details = details;
        presence.largeImageKey = "icon";
        presence.startTimestamp = startTimestamp;
        presence.largeImageText  = "JByteMod-Remastered";
        if (!state.equals("") && JByteMod.ops.get("discord_state").getBoolean()) {
            presence.state = state;
        }

        discordRPC.Discord_UpdatePresence(presence);
    }
}
