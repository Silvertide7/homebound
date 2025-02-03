package net.silvertide.homebound.client.data;

public class ClientHomeData {
    private static long startHomeTimeStamp;
    private static long finishHomeTimeStamp;
    public static void setHomeTimeStamps(long startTimeStamp, long finishTimeStamp) {
        ClientHomeData.startHomeTimeStamp = startTimeStamp;
        ClientHomeData.finishHomeTimeStamp = finishTimeStamp;
    }
    public static long getStartHomeTimeStamp() {
        return startHomeTimeStamp;
    }
    public static long getFinishHomeTimeStamp() {
        return finishHomeTimeStamp;
    }
    public static boolean isPlayerBindingHome() {
        return startHomeTimeStamp > 0;
    }
}
