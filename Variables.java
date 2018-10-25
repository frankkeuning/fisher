package scripts.abc2;

import org.tribot.api2007.types.RSTile;

import java.io.File;
import java.util.Properties;

/**
 * Created by frank on 4-7-2017.
 */
public class Variables {
    public static int[] FISHING_SPOT_IDS;
    public static int[] FISHING_GEAR_IDS;

    public static RSTile BANK_TILE;
    public static RSTile FISH_TILE;
    public static String ACTION;

    public static boolean IS_POWERFISHING;
    public static boolean DESTROY = false;
    public static boolean CUSTOM_BANK = true;

    public static int FISH_COUNT = 0;

    public static Properties prop = new Properties();
    public static File path;

}
