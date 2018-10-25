package scripts.abc2;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.WebWalking;
import scripts.Node;
import scripts.Util2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frank on 4-7-2017.
 */
public class Walk extends Node {

    List<Integer> abc2WaitTimes = new ArrayList<>();

    @Override
    public boolean isValid() {

        if(Inventory.isFull() && Util2.playerNearTile(Variables.FISH_TILE, 20))
            return true;
        else if(!Inventory.isFull() && Util2.atBank())
            return true;
        return false;
    }

    @Override
    public void execute() {
        abc2WaitTimes.add(General.random(1200, 2800));
        if(Util2.playerNearTile(Variables.FISH_TILE,20) && Inventory.isFull()){
            walkToBank();
        } else {
            walkToFish();
        }
    }

    void walkToBank(){
        abc2WaitTimes = Util2.sleep(abc2WaitTimes);
        if(Variables.CUSTOM_BANK){
            if(WebWalking.walkTo(Variables.BANK_TILE)){
                Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        General.sleep(100, 200);
                        return Util2.atBank();
                    }
                }, General.random(1500,3000));
            }
        } else {
            if(WebWalking.walkToBank()){
                Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        General.sleep(100, 200);
                        return Util2.atBank();
                    }
                }, General.random(1500,3000));
            }
        }
    }

    void walkToFish(){
        if(WebWalking.walkTo(Variables.FISH_TILE)){
            abc2WaitTimes = Util2.sleep(abc2WaitTimes);
            Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    General.sleep(100, 200);
                    return Player.getPosition().distanceTo(Variables.FISH_TILE) < 20;
                }
            }, General.random(1500,3000));
        }
    }
}
