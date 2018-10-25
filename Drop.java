package scripts.abc2;

import org.tribot.api2007.Inventory;
import scripts.Node;

/**
 * Created by frank on 3-7-2017.
 */
public class Drop extends Node {


    @Override
    public boolean isValid() {
        return Inventory.isFull();
    }

    @Override
    public void execute() {
        Inventory.dropAllExcept(Variables.FISHING_GEAR_IDS);
    }
}
