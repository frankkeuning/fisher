package scripts.abc2;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;

/**
 * Created by frank on 1-7-2017.
 */
public class Utility {
    public static ABCUtil abc = new ABCUtil();

    public boolean should_hover = false;
    public boolean is_hovering = false;
    public boolean open_menu = false;
    public boolean was_under_attack = false;
    public RSNPC next_target = null;
    public RSObject next_object = null;
    public long runtime = 0;

    public Utility(){}


    public RSNPC findTarget(int... ids){
        final RSNPC[] targets = NPCs.findNearest(ids);
        if(targets == null || targets.length < 1)
            return null;

        final RSNPC target = (RSNPC) this.abc.selectNextTarget(targets);
        return target;
    }

    //finds a target rsnpc
    //uses a filter to only detect npc's which aren't in combat.
    //abc2 implemented
    public RSNPC findTarget(String targetName){
        final String name = targetName;
        final RSNPC[] targets = NPCs.findNearest(new Filter<RSNPC>() {
            @Override
            public boolean accept(RSNPC rsnpc) {
                return !rsnpc.isInCombat() && rsnpc.getName().equals(name);
            }
        });
        if(targets == null || targets.length < 1)
            return null;

        return (RSNPC) this.abc.selectNextTarget(targets);
        //return target;
    }

    //clicks target RSNPC
    public boolean clickTarget(RSNPC target, String option){
        for (int i = 0; i < 5; i++){
            if(isReachable(target.getPosition(), false)){
                break;
            } else {
                navigateToPosition(target.getPosition());
            }
        }
        if(DynamicClicking.clickRSNPC(target, option)){
            this.should_hover = this.abc.shouldHover();
            this.open_menu = this.abc.shouldOpenMenu();
            Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    return Player.isMoving();
                }
            },General.random(500,1000));
            next_target = null;
            return true;
        }
        return false;
    }

    public boolean clickObject(RSObject object, String option){
        for (int i = 0; i < 5; i++){
            if(isReachable(object.getPosition(), false)){
                break;
            } else {
                navigateToPosition(object.getPosition());
            }
        }
        if(isReachable(object.getPosition(), true)){
            if(DynamicClicking.clickRSObject(object, option)){
                this.should_hover = this.abc.shouldHover();
                this.open_menu = this.abc.shouldOpenMenu();
                next_object = null;
                Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        return Player.isMoving();
                    }
                },General.random(500,1000));
                next_target = null;
                return true;
            }
        }
        return false;
    }

    //checks to see if player can click target
    public boolean isOnscreen(RSTile tile){
        return tile.isOnScreen() && tile.isClickable();
    }


    String readInterface(int id, int childId){
        RSInterface inFace = Interfaces.get(id,childId);
        return inFace != null ? inFace.getText() : null;
    }

    public boolean isReachable(RSTile tile, boolean isObject){
        return PathFinding.canReach(tile, isObject);
    }

    //navigates to target. used when the path between player and the target is blocked.
    public void navigateToPosition(RSTile tile){
        DPathNavigator dpath = new DPathNavigator();
        if(dpath.traverse(tile)){
            Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    return Player.isMoving();
                }
            }, General.random(500, 1000));
        }
    }

    //finds an object with abc2 implementation
    public RSObject findObject(int distance,int... ids){
        final RSObject[] objects = Objects.find(distance,ids);
        if(objects == null || objects.length < 1)
            return null;
        final RSObject object = (RSObject) this.abc.selectNextTarget(objects);
        return object;
    }

    public RSObject findObject(int distance,String... names){
        final RSObject[] objects = Objects.find(distance,names);
        if(objects == null || objects.length < 1)
            return null;
        final RSObject object = (RSObject) this.abc.selectNextTarget(objects);
        return object;
    }

    public void handleActionWhile(){
        if(is_hovering && Mouse.isInBounds()){
            hoverNextTarget();
            if (this.open_menu)
                openMenuForNextTarget();
        }
        else if(this.should_hover) {
            is_hovering = true;
        }
    }

    public void hoverNextTarget(){
        if(next_target == null)
            next_target = findTarget("Chicken");
        next_target.hover();
    }

    public void openMenuForNextTarget(){
        RSNPC target = findTarget("Chicken");
        DynamicClicking.clickRSNPC(target, 3);
    }

    //performs abc2's timed actions
    //only call this method when player is idling.
    public void performTimedActions(){
        if (this.abc.shouldCheckTabs())
            this.abc.checkTabs();

        if (this.abc.shouldCheckXP())
            this.abc.checkXP();

        if (this.abc.shouldExamineEntity())
            this.abc.examineEntity();

        if (this.abc.shouldMoveMouse())
            this.abc.moveMouse();

        if (this.abc.shouldPickupMouse())
            this.abc.pickupMouse();

        if (this.abc.shouldRightClick())
            this.abc.rightClick();

        if (this.abc.shouldRotateCamera())
            this.abc.rotateCamera();

        if (this.abc.shouldLeaveGame())
            this.abc.leaveGame();
    }
}
