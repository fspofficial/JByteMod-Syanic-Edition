package me.grax.jbytemod.discord;

import java.time.OffsetDateTime;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;

import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import lombok.Getter;
import lombok.SneakyThrows;
import me.grax.jbytemod.JByteMod;

@Getter
public class Discord {
    private OffsetDateTime startTimestamp;
    private final String applicationId;
    private RichPresence.Builder builder;
    private IPCClient client;

    public Discord(String applicationId) {
        this.applicationId = applicationId;
    }

    @SneakyThrows
    public void init() {
        startTimestamp = OffsetDateTime.now();
        client = new IPCClient(Long.parseLong(applicationId));
        builder = new RichPresence.Builder();
        client.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client) {
                updatePresence("Idle...", "");
            }
        });
        try {
            client.connect();
        } catch (Exception e) {
            System.out.println("Failed to hook discord");
        }
    }

    /**
     * Updates Discord presence based on application state.
     *
     * @param details additional details
     * @param state   current state
     */
    public void updatePresence(String details, String state) {
        String discordState = JByteMod.ops.get("discord_state").getBoolean() ? state : "Editing hidden class";
        String discordDetails = JByteMod.ops.get("discord_state").getBoolean() ? details : "Working on hidden file";

        builder.setState(discordState)
                .setDetails(discordDetails)
                .setStartTimestamp(startTimestamp)
                .setLargeImage("icon", "JByteMod-Remastered");
        if(client.getStatus().equals(PipeStatus.CONNECTED))
            client.sendRichPresence(builder.build());
    }

    /**
     * Shutdown Discord IPC client.
     */
    public void shutdown() {
        if (client != null && client.getStatus().equals(PipeStatus.CONNECTED)) {
            client.close();
        }
    }
}
