package fr.red_spash.murder.players;

import com.craftmend.openaudiomc.api.interfaces.AudioApi;
import com.craftmend.openaudiomc.api.interfaces.Client;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class VoiceManager {

    private final UUID playerUUID;
    private boolean lockMute = false;
    private final AudioApi audioApi = AudioApi.getInstance();

    public VoiceManager(UUID playerUUID){
        this.playerUUID = playerUUID;
        this.forceUnMute();
    }

    public void forceMute(){
        Client client = this.audioApi.getClient(this.playerUUID);
        if(client != null){
            client.forcefullyDisableMicrophone(true);
        }
    }

    public void forceUnMute(){
        Client client = this.audioApi.getClient(this.playerUUID);
        if(client != null){
            client.forcefullyDisableMicrophone(false);
        }
    }

    public boolean isLockMute() {
        return lockMute;
    }
}
