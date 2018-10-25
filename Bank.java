package scripts.abc2;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Inventory;
import scripts.Node;
import scripts.Util2;


/**
 * Created by frank on 3-7-2017.
 */
public class Bank extends Node {

    @Override
    public boolean isValid() {
        return (Inventory.isFull()
                && Util2.atBank()) || Banking.isBankScreenOpen() || !Util2.hasGear();
    }

    @Override
    public void execute() {
        General.println("banking");
        if(!Banking.isBankScreenOpen()){
            openBank();
        } else {
            depositItems();
            if(!Util2.hasGear()){
                Banking.depositAll();
                if(gearInBank()){
                    Banking.withdraw(1,Variables.FISHING_GEAR_IDS);
                } else {
                    Variables.DESTROY = true;
                }
            }
            Banking.close();
        }

    }



    boolean openBank(){
        if(Banking.openBank()){
            Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    General.sleep(100, 200);
                    return Banking.isBankScreenOpen();
                }
            }, General.random(2500,3500));
        }
        return Banking.isBankScreenOpen();
    }

    void depositItems(){
        Banking.depositAllExcept(Variables.FISHING_GEAR_IDS);
        Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                General.sleep(100,200);
                return Inventory.getAll().length < 5;
            }
        }, General.random(2000,3000));
    }





    boolean gearInBank(){
        for(int i : Variables.FISHING_GEAR_IDS){
            if(Banking.find(i).length == 0)
                return false;
        }
        return true;
    }

}
