package net.silvertide.homebound.client.gui;

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
