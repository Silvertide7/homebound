package net.silvertide.homebound.client.gui;

public class ClientWarpData {

    private static long startWarpTimeStamp;
    private static long finishWarpTimeStamp;

    public static void setWarpTimeStamps(long startTimeStamp, long finishTimeStamp) {
        ClientWarpData.startWarpTimeStamp = startTimeStamp;
        ClientWarpData.finishWarpTimeStamp = finishTimeStamp;
    }
    public static long getStartWarpTimeStamp() {
        return startWarpTimeStamp;
    }

    public static long getFinishWarpTimeStamp() {
        return finishWarpTimeStamp;
    }
    public static boolean isPlayerWarping() {
        return startWarpTimeStamp > 0;
    }
}
