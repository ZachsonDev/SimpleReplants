package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.InvalidCommandArgument;
import java.util.ArrayList;
import java.util.List;

public class ShowCommandHelp
extends InvalidCommandArgument {
    List<String> searchArgs = null;
    boolean search = false;

    public ShowCommandHelp() {
    }

    public ShowCommandHelp(boolean bl) {
        this.search = bl;
    }

    public ShowCommandHelp(List<String> list) {
        this(true);
        this.searchArgs = new ArrayList<String>(list);
    }
}

