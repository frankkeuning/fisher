package scripts.abc2;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;
import scripts.AntiBan;
import scripts.Node;
import scripts.Util2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frank on 2-7-2017.
 */
public class Fish extends Node {
    final RSTile FISH_TILE = new RSTile(3241,3152,0);
    List<Integer> waitTimes = new ArrayList<>();


    @Override
    public boolean isValid() {
        return !Inventory.isFull()
                && Player.getPosition().distanceTo(Variables.FISH_TILE) < 20
                && Player.getAnimation() == -1 && Util2.hasGear();

    }

    @Override
    public void execute() {
        waitTimes = Util2.sleep(waitTimes);
        RSNPC[] spots = NPCs.findNearest(Variables.FISHING_SPOT_IDS);
        if(spots.length > 0){
            General.println("clicked");
            RSNPC spot = AntiBan.selectNextTarget(spots);
            if(!spot.isOnScreen() && !spot.isClickable()){
                if(Util2.playerNearTile(spot.getPosition(),5))
                    Camera.turnToTile(spot.getPosition());
                else
                    walkToSpot(spot.getPosition());

            }


            if(Player.getAnimation() == -1){
                if(AntiBan.activateRun()){
                    Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.random(100,200);
                            return Game.isRunOn();
                        }
                    },General.random(1000,2000));
                }
                if(DynamicClicking.clickRSNPC(spot, Variables.ACTION)){

                    Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.sleep(300, 600);
                            return Player.getAnimation() != -1 && !Player.isMoving();
                        }
                    }, General.random(2000, 3500));
                }
                long startTime = System.currentTimeMillis();

                doWhileFishing(spots,spot);

                if(Player.getAnimation() == -1){
                    AntiBan.generateTrackers((int) (System.currentTimeMillis() - startTime), false);
                    waitTimes.add(AntiBan.getReactionTime());
                }
            }
        }
    }

    void walkToSpot(RSTile tile){
        Walking.walkTo(tile);
        Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                General.sleep(200,400);
                return Player.isMoving();
            }
        }, General.random(3500,5500));
    }

    void doWhileFishing(RSNPC[] spots, RSNPC spot){
        while(Player.getAnimation() != -1){
            General.sleep(20,40);
            AntiBan.timedActions();
            if(AntiBan.getShouldHover()){
                AntiBan.hoverEntity(spots);
                AntiBan.resetShouldHover();
            }
            if(AntiBan.getShouldOpenMenu()){
                if(DynamicClicking.clickRSNPC(spot, 3)){
                    Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.sleep(100,200);
                            return ChooseOption.isOpen();
                        }
                    }, General.random(2500,3700));
                }
                AntiBan.resetShouldOpenMenu();
            }
        }
    }




}
