package com.jeff_media.standalonepluginscreen;

import com.allatori.annotations.StringEncryption;
import com.jeff_media.standalonepluginscreen.NagMessage;

@StringEncryption(value="disable")
public class CLINagMessage
extends NagMessage {
    @Override
    public void show() {
        CLINagMessage.getMessage().stream().map(string -> string.replace("{spigotLink}", CLINagMessage.getSetupSpigotLink()).replace("{discordLink}", CLINagMessage.getDiscordLink())).forEachOrdered(System.out::println);
    }
}

