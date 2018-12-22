package fr.imt.ales.msr.LoggerUtils;

import org.apache.logging.log4j.Logger;

import java.util.Map;

public class LoggerPrintUtils {

    private static final int SIZE_LAUNCHBAR = 40;
    private static final char CHAR_FILL_LAUNCHBAR = '=';

    /**
     * Displays a launch bar on the logger given in parameter
     * @param loggerLog4J2 Logger instance to use
     * @param message Message to print before the launchbar
     * @param currentValue Integer value of the current process
     * @param maxValue Integer value which giving the end of the process
     */
    public static void printLaunchBar(Logger loggerLog4J2, String message, Integer currentValue, Integer maxValue){
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
        loggerLog4J2.info(message);
        loggerLog4J2.info(launchBarString.toString());
        //loggerLog4J.info("[====================] 100%");
    }

    /**
     * Print a Map as an associated table into the given logger
     * @param loggerLog4J2
     * @param mapToDisplay
     */
    public static void printMapAsTable(Logger loggerLog4J2, Map<String,Object> mapToDisplay){
        int tableLen = 0;

        for (Map.Entry<String, Object> entry : mapToDisplay.entrySet())
        {
            int currentLen = (entry.getKey() + " : " + entry.getValue()).length();
            if(currentLen > tableLen)
                tableLen = currentLen;
        }

        StringBuilder stringBuilderTable  = new StringBuilder("\r\n|");
        for (int i = 0; i < tableLen+2; i++) {
            stringBuilderTable.append("-");
        }
        stringBuilderTable.append("|\r\n");

        for (Map.Entry<String, Object> entry : mapToDisplay.entrySet()){

            StringBuilder lineToPrint = new StringBuilder().append("| ").append(entry.getKey()).append(" : ").append(entry.getValue().toString());
            for (int i = lineToPrint.length(); i < tableLen+2; i++) {
                lineToPrint.append(" ");
            }
            lineToPrint.append(" |\r\n");
            stringBuilderTable.append(lineToPrint);
        }

        stringBuilderTable.append("|");
        for (int i = 0; i < tableLen+2; i++) {
            stringBuilderTable.append("-");
        }
        stringBuilderTable.append("|\r\n");
        loggerLog4J2.info(stringBuilderTable);
    }

    /**
     * Getter for the size of the launch bar
     * @return Integer size of the launchbar
     */
    public static int getSizeLaunchbar() {
        return SIZE_LAUNCHBAR;
    }

    /**
     * Getter for the char to fill the launchbar
     * @return the char to fill the launchbar
     */
    public static char getCharFillLaunchbar() {
        return CHAR_FILL_LAUNCHBAR;
    }
}
