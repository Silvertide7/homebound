package net.silvertide.homebound.client.gui;

// The two channeling bars share one overlay; this enum carries the per-bar label and
// which texture row (multiple of IMAGE_HEIGHT) the progress fill samples from.
public enum ChannelBarType {
    WARP("Returning Home", 2),
    BIND_HOME("Binding Home", 1);

    public final String label;
    public final int progressTexRow;

    ChannelBarType(String label, int progressTexRow) {
        this.label = label;
        this.progressTexRow = progressTexRow;
    }
}
