package ar.edu.itba.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static ar.edu.itba.sim.Constants.*;

/**
 * Hello world!
 *
 */
public class App {
    private static List<Victim> victims = new ArrayList<>();
    private static List<Predator> predators = new ArrayList<>();
    private static FileHandler fileHandler = new FileHandler("resources");
    private static int died=0,lived=0;
    private static double deltaT = 0.01;

    public static void main( String[] args ) {
        int j = 0;
        int[] amountValues = { 10,15,20,25,30,35,40 };
        String filename = "freq-";
        for(int corridas = 0;corridas<4;corridas++) {
            for (int jk = 0; jk < amountValues.length; jk++) {
                FREQ = amountValues[jk];
                predators.clear();
                victims.clear();
                createPredators();
                for (int i = 1; i < 3500; i++) {
                    if (i % FREQ == 0) {
                        injectVictim(j);
                        j += 1;
                    }
                    evolveSystem(i, filename + amountValues[jk]);
                }
                fileHandler.saveMeta(filename + amountValues[jk] + "-metadata", lived, died, VICTIM_VELOCITY, PREDATOR_VELOCITY);
                lived = 0;
                died = 0;
            }
        }
    }

    private static void injectVictim(int id){
        Random r = new Random();
        int res = r.nextInt(2);
        if(res==1)
            victims.add(new Victim(id,spawnX,
                ThreadLocalRandom.current().nextDouble(RADIO_VICTIM+0.2, L - RADIO_VICTIM-0.2),
                0d, 0d, RADIO_VICTIM, 1d, 0d));
        else
            victims.add(new Victim(id,ThreadLocalRandom.current().nextDouble(RADIO_VICTIM+0.2, L - RADIO_VICTIM-0.2),
                spawnY,0d, 0d, RADIO_VICTIM, 1d, 0d));
    }

    private static void evolveSystem(int i, String fileName) {
        //System.out.print(i + " ");
        for(Predator p : predators){
            if(p.eating > 0){
                p.eating --;
            }else{
                Victim victim= findNearestVictim(p);;
                if(p.getChasing() != null ){
                    if(victim != null && p.getChasing()!=null && p.distanceToVictim(p.getChasing()) > p.distanceToVictim(victim))
                        victim = victim;
                    else
                        victim = p.getChasing();
                }
                
                if(victim != null){
                    p.aproachToVictim(victim);
                    victim.beingChased(p);
                }
                //Analizar choques con otros depredadores
                for(Predator p2 : predators){
                    if(!p2.equals(p)){
                        //p.collidePredator(p2);
                    }
                }
                p.updatePosition(deltaT, L);
                p.collisionWithWalls();
            }
        }
        List<Victim> victimsToDelete= new ArrayList<>();
        boolean isEscaping;
        for(Victim p : victims){
            isEscaping = false;
            if(p.reachedTarget()){
                removeAllChasing(p);
                lived++;
                victimsToDelete.add(p);
            }else if(!p.isDead()){
                p.approachToTarget();
                for(Predator n: predators){
                    if(p.collideWithPredator(n)){ 
                        p.killVictim();
                        n.setChasing(null);
                        n.setVx(0d);
                        n.setVy(0d);
                        removeAllChasing(p);
                        died++;
                        victimsToDelete.add(p);
                        n.eating = TIME_EATING;
                    }else if(/*n.isChasing(p) && */p.near(n)){
                        if(!isEscaping){
                            isEscaping=true;
                            //p.normalizeVelocity(); Comento esto
                        }
                        p.escape(n);
                        //System.out.print(" y entre (lpm) + vx= " + p.getvx()+ " vy= " + p.getvy());
                    }else{
                        //p.approachToTarget();
                    }
                }
                for(Victim otherVictim : victims){
                    if(!otherVictim.equals(p)){
                        p.collideVictim(otherVictim);
                    }
                }
                p.normalize();

                p.updatePosition(deltaT, L);
                p.collisionWithWalls();
            }
        }
        victims.removeAll(victimsToDelete);

        fileHandler.saveDynamic(fileName,i, victims, predators);
        //System.out.print("\n");

    }

    private static void removeAllChasing(Victim p) {
        for(Predator predator : predators){
            if(predator.getChasing() != null && predator.getChasing().equals(p)){
                predator.setVx(0d);
                predator.setVy(0d);
                predator.setChasing(null);
            }
        }
    }

    private static Victim findNearestVictim(Predator p){
        double minDistance = 15;
        Victim minVictim = null;
        for(Victim v : victims){
            if(minVictim == null){
                minVictim = v;
                minDistance = p.distanceToVictim(v);
            }else if(p.distanceToVictim(v) < minDistance){
                minVictim = v;
                minDistance = p.distanceToVictim(v);
            }
        }
        if(minDistance < RADIO_INTERACTION)
            return minVictim;
        return null;
    }

    private static void createPredators() {
        /**
         * 8.79749 15.2011 0
         * 2.9315 1.96233 0
         * 5.14891 7.64342 0
         * 5.60048 12.7048 0
         */
        //predators.add(new Predator(0,8.79749,15.2011,0d,0d, RADIO_PREDATOR, 1d,0d));
        //predators.add(new Predator(1,2.9315,1.96233,0d,0d, RADIO_PREDATOR, 1d,0d));
        //predators.add(new Predator(2,5.14891,7.64342,0d,0d, RADIO_PREDATOR, 1d,0d));
        //predators.add(new Predator(3,5.60048,12.7048,0d,0d, RADIO_PREDATOR, 1d,0d));
        double xpos, ypos;
        for (int i = 0; i < AMOUNT_OF_PREDATOR; i++) {
            do {
                xpos = ThreadLocalRandom.current().nextDouble(RADIO_PREDATOR, WIDTH_HALL - RADIO_PREDATOR);
                ypos = ThreadLocalRandom.current().nextDouble(RADIO_PREDATOR, L - RADIO_PREDATOR);
            }while(collidedPredator(xpos, ypos));
            predators.add(new Predator(i,xpos, ypos, 0d, 0d, RADIO_PREDATOR, 1d, 0d));
        }
    }

    private static boolean collidedPredator(double xpos, double ypos) {
        for (Predator p : predators) {
            if(Math.pow(xpos - p.getX(), 2) + Math.pow(ypos - p.getY(), 2) - Math.pow(RADIO_PREDATOR + p.getRadius(), 2) <=0) return true;
        }
        return false;
    }


}
