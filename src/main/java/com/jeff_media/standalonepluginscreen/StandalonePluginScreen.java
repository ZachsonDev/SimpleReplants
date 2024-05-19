package com.jeff_media.standalonepluginscreen;

import com.allatori.annotations.DoNotRename;
import com.allatori.annotations.StringEncryption;
import com.jeff_media.standalonepluginscreen.CLINagMessage;
import com.jeff_media.standalonepluginscreen.DesktopNagMessage;
import java.awt.GraphicsEnvironment;

@DoNotRename
@StringEncryption(value="disable")
public final class StandalonePluginScreen {
    @DoNotRename
    public static void main(String[] stringArray) {
        new CLINagMessage().show();
        if (!GraphicsEnvironment.isHeadless()) {
            new DesktopNagMessage().show();
        }
    }
}

