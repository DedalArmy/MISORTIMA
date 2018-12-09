package fr.imt.ales.msr.LoggerUtils;

import org.apache.logging.log4j.Logger;

public class LaunchBar {

    private static final int SIZE_LAUNCHBAR = 40;
    private static final char CHAR_FILL_LAUNCHBAR = '=';

    /**
     * Displays a launch bar
     * @param loggerLog4J
     * @param message
     * @param currentValue
     * @param maxValue
     */
    public static void displayLaunchBar(Logger loggerLog4J, String message, Integer currentValue, Integer maxValue){
        loggerLog4J.info(message);

        StringBuilder launchBarString  = new StringBuilder("[");

        double percentage = (int)(Math.ceil((currentValue*1.0/maxValue)*100));
        int lenLaunchBarToFill = (int)(Math.floor(percentage / (100.0/SIZE_LAUNCHBAR)));

        for (int i = 0; i < lenLaunchBarToFill && lenLaunchBarToFill <= SIZE_LAUNCHBAR; i++) {
            launchBarString.append(CHAR_FILL_LAUNCHBAR);
        }

        for (int i = 0; i < SIZE_LAUNCHBAR - lenLaunchBarToFill && lenLaunchBarToFill <= SIZE_LAUNCHBAR; i++) {
            launchBarString.append(" ");
        }

        launchBarString.append("] ").append(percentage).append("%");
        loggerLog4J.info(launchBarString.toString());
        //loggerLog4J.info("[====================] 100%");
    }

    public static int getSizeLaunchbar() {
        return SIZE_LAUNCHBAR;
    }

    public static char getCharFillLaunchbar() {
        return CHAR_FILL_LAUNCHBAR;
    }
}
