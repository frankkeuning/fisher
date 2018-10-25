package scripts;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;
import scripts.AntiBan;
import scripts.abc2.Variables;

import java.util.List;

/**
 * Created by frank on 2-7-2017.
 */
public class Util2 {

    public static double FACTOR = 0.3;

    public static RSObject[] findObjects(String name, int distance){
        final RSObject[] objects = Objects.findNearest(distance,name);
        if(objects == null || objects.length < 1)
            return null;
        return objects;
    }

    public static RSNPC[] findNpcs(final String name, int distance){
        final RSNPC[] targets = NPCs.findNearest(new Filter<RSNPC>() {
            @Override
            public boolean accept(RSNPC rsnpc) {
                return !rsnpc.isInCombat() && rsnpc.getName().equals(name);
            }
        });
        if(targets == null || targets.length < 1)
            return null;
        return targets;
    }

    public static int average(List<Integer> times){
        Integer total = 0;
        if(!times.isEmpty()){
            for(Integer i : times)
                total += i;
            return total.intValue() / times.size();
        }
        return total;
    }

    public static boolean inComabat(){
        return Combat.getTargetEntity() != null || Combat.getAttackingEntities().length > 0;
    }

    public static boolean playerNearTile(RSTile tile, int distance){
        return Player.getPosition().distanceTo(tile) <= distance;
    }

    public static boolean atBank(){
        return Objects.find(10,"Bank Booth").length > 0;
    }


    public static List<Integer> sleep(List<Integer> waitTimes){
        if(waitTimes.isEmpty())
            AntiBan.generateTrackers(General.random(800, 1200), false);
        if(!waitTimes.isEmpty())
            AntiBan.generateTrackers(average(waitTimes), false);
        final int reactionTime = (int) (AntiBan.getReactionTime() * FACTOR);
        waitTimes.add(reactionTime);
        General.println("sleeping for: "+reactionTime);
        AntiBan.sleepReactionTime(reactionTime);
        return waitTimes;
    }

    public static boolean hasGear(){
        for(int i : Variables.FISHING_GEAR_IDS){
            if(Inventory.find(i).length == 0){
                General.println("no");
                return false;
            }

        }
        return true;
    }

    public static void walkToSpot(RSTile tile){
        Walking.walkTo(tile);
        Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                General.sleep(200, 400);
                return Player.isMoving();
            }
        }, General.random(3500, 5500));
    }

    public static void navigateToPosition(final RSTile tile){
        DPathNavigator dpath = new DPathNavigator();
        if(dpath.traverse(tile)){
            Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    General.random(100,200);
                    return Player.isMoving() && Player.getPosition().distanceTo(tile) > 7;
                }
            }, General.random(500, 1000));
        }
    }

    public static boolean isReachable(RSTile tile, boolean isObject){
        return PathFinding.canReach(tile, isObject);
    }

    public static boolean clickObject(RSObject object,String option){
        boolean reachable = isReachable(object.getPosition(),true);
        for (int i = 0; i < 5; i++){
            if(reachable){
                break;
            } else {
                navigateToPosition(object.getPosition());
            }
        }
        if(reachable){
            if(DynamicClicking.clickRSObject(object,option)){
                Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        General.sleep(200,400);
                        return Player.isMoving() || Player.getAnimation() == -1;
                    }
                }, General.random(2000,3000));
            }
            return true;
        }

        return false;
    }

}
