package scripts.abc2;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.util.ThreadSettings;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;
import org.tribot.util.Util;
import scripts.AntiBan;
import scripts.Node;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by frank on 3-7-2017.
 */
public class Fisher extends Script implements Painting, MessageListening07 {
//    @ScriptManifest(authors = { "gef30" }, category = "Fishing", name = "ABC2 Fisher",
//            description = "fishes.")

    public ArrayList<Node> nodes = new ArrayList<>();
    long startTime;

    @Override
    public void run() {
        Variables.path = new File(Util.getWorkingDirectory().getAbsolutePath(), "multifisher" + Player.getRSPlayer().getName() + "settings.ini");
        doGUI();
        startTime = System.currentTimeMillis();
        General.useAntiBanCompliance(true);
        ThreadSettings.get().setClickingAPIUseDynamic(true);
        init();
        while(!Variables.DESTROY){
            if(Login.getLoginState() == Login.STATE.INGAME){
                for(final Node n : nodes){
                    if(n.isValid())
                        n.execute();
                    else
                        AntiBan.timedActions();
                    General.sleep(20,35);
                }

            }
            General.sleep(10,20);
        }
    }

    private void init(){
        nodes.add(new Fish());
        if(Variables.IS_POWERFISHING)
            nodes.add(new Drop());
        else
            nodes.add(new Bank());
        nodes.add(new Walk());

    }

    public final Ui gui = new Ui();
    public void doGUI(){
        General.println("running ui");
        try{
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try{
                        gui.setVisible(true);
                    }catch(Exception e){
                        General.println("error1");
                        e.printStackTrace();
                    }
                }
            });
        }catch(Exception e){
            General.println("error2");
            e.printStackTrace();
        }
        if(gui.active)
            gui.loadSettings();
        while(gui.active){
            sleep(100);
        }



        General.println("done");
    }

    final int startXP = Skills.getXP(Skills.SKILLS.FISHING);
    @Override
    public void onPaint(Graphics g) {
        long runTime = System.currentTimeMillis() - startTime;

        int currentXP = Skills.getXP(Skills.SKILLS.FISHING);
        int gainedXp = currentXP - startXP;
        long xpPerHr = (gainedXp) * (3600000 / runTime);

        g.drawString("Runtime: "+ Timing.msToString(runTime),300,360);
        g.drawString("Fish Caught: "+ Variables.FISH_COUNT + " ("+(Variables.FISH_COUNT * (3600000 / runTime))+")",300,375);
        g.drawString("XP Gained: "+ gainedXp +" ("+xpPerHr+")",300,390);
    }

    @Override
    public void personalMessageReceived(String s, String s1) {
        General.println(s1);
    }

    @Override
    public void clanMessageReceived(String s, String s1) {

    }

    @Override
    public void serverMessageReceived(String s) {
        if(s.contains("catch"))
            Variables.FISH_COUNT++;
        General.println(s);
    }

    @Override
    public void playerMessageReceived(String s, String s1) {

    }

    @Override
    public void duelRequestReceived(String s, String s1) {

    }

    @Override
    public void tradeRequestReceived(String s) {

    }
}
