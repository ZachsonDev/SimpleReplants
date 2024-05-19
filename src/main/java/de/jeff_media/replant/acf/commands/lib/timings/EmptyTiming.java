package de.jeff_media.replant.acf.commands.lib.timings;

import de.jeff_media.replant.acf.commands.lib.timings.MCTiming;

class EmptyTiming
extends MCTiming {
    EmptyTiming() {
    }

    @Override
    public final MCTiming startTiming() {
        return this;
    }

    @Override
    public final void stopTiming() {
    }
}

