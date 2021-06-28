package ar.edu.itba.sim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static ar.edu.itba.sim.Constants.L;
import static ar.edu.itba.sim.Constants.WIDTH_HALL;

public class FileHandler {

    private String basePath;
    private String staticInputFile = "RandomStaticInput";
    private String dynamicInputfile = "RandomDynamicInput";
    private final String dynamicFile = "dynamicOutput";
    private final String velocity = "velocity";
    private final String position = "position";

    public FileHandler(String basePath){
        this.basePath = basePath;
        staticInputFile = basePath + "/" + staticInputFile + ".txt";
        dynamicInputfile = basePath + "/" + dynamicInputfile + ".txt";
    }

    public void saveDynamic(String filename, int i, List<Victim> victimList, List<Predator> predatorList){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                    new File(basePath + "/" + filename + ".txt"), true));
            writer.write(Integer.toString(victimList.size()+predatorList.size()+4) + "\n"); //cant particle mas las 4 para fijar los bordes
            writer.newLine();
            writer.write(0 + " " + 0 + " " + 0.32 + " "  + 0+ " 123 0 123");
            writer.newLine();
            writer.write(0 + " " + L + " "  + 0.32 + " "  + 0+ " 123 0 123");
            writer.newLine();
            writer.write(WIDTH_HALL + " "  + 0 + " "  + 0.32 + " "  + 0+ " 123 0 123");
            writer.newLine();
            writer.write(WIDTH_HALL + " "  + L + " " + 0.32 + " "  + 0 + " 123 0 123");
            writer.newLine();
            for (Victim p : victimList) {
                String color = "255 0 0";
                String builder =
                        String.format(Locale.US, "%6.7e", p.getX()) + " " +
                                String.format(Locale.US, "%6.7e", p.getY()) + " " +
                                String.format(Locale.US, "%6.7e", p.getRadius()) + " "+
                                color;
                writer.write(builder);
                writer.newLine();
            }
            for (Predator p : predatorList) {
                String color = "0 0 255";
                String builder =
                        String.format(Locale.US, "%6.7e", p.getX()) + " " +
                                String.format(Locale.US, "%6.7e", p.getY()) + " " +
                                String.format(Locale.US, "%6.7e", p.getRadius()) + " "+
                                color;
                writer.write(builder);
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
/*

    public void saveDynamicPredator(String filename, int i, List<Predator> predatorList){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                    new File(basePath + "/" + filename + ".txt"), true));
            writer.write(Integer.toString(AMOUNT_OF_PREDATOR+4) + "\n"); //cant particle mas las 4 para fijar los bordes
            writer.newLine();
            writer.write(0 + "    " + 0 + "    " + 0.01 + "    " + 0);
            writer.newLine();
            writer.write(0 + "    " + L + "    " + 0.01 + "    " + 0);
            writer.newLine();
            writer.write(WIDTH_HALL + "    " + 0 + "    " + 0.01 + "    " + 0);
            writer.newLine();
            writer.write(WIDTH_HALL + "    " + L + "    " + 0.01 + "    " + 0);
            writer.newLine();
            for (Predator p : predatorList) {
                Double aux = Math.sqrt(Math.pow(p.getVx(), 2) + Math.pow(p.getVy(), 2));
                String color;
                double epsilon = 0.0001;
                if(p.getRadius()>= RADIO_PREDATOR - epsilon)
                    color = "255 0 0";
                else if(p.getRadius()<= RADIO_PREDATOR + epsilon)
                    color = "0 0 255";
                else
                    color = "0 255 0";
                String builder =
                        String.format(Locale.US, "%6.7e", p.getX()) + " " +
                                String.format(Locale.US, "%6.7e", p.getY()) + " " +
                                String.format(Locale.US, "%6.7e", p.getRadius()) + " " +
                                String.format(Locale.US, "%6.7e", p.getVelocityNotCollided()
                                )+ " "+ color;
                writer.write(builder);
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }*/

    /*public void saveAverageVelocity(String filename, int i, List<Pedestrian> pedestrianList){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                    new File(basePath + "/" + filename + "_velocity.txt"), true));
            int total = 0;
            double avgSpeed = 0;
            for (Pedestrian p : pedestrianList) {
                if(!p.isWillCollide()){
                    avgSpeed += p.getVelocityNotCollided();
                    total++;
                }
            }

            avgSpeed = avgSpeed/pedestrianList.size() ;
            writer.write(i + " " + avgSpeed);
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }*/

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getStaticInputFile() {
        return staticInputFile;
    }

    public void setStaticInputFile(String staticInputFile) {
        this.staticInputFile = staticInputFile;
    }

    public String getDynamicInputfile() {
        return dynamicInputfile;
    }

    public void setDynamicInputfile(String dynamicInputfile) {
        this.dynamicInputfile = dynamicInputfile;
    }

    public String getDynamicFile() {
        return dynamicFile;
    }

    public String getVelocity() {
        return velocity;
    }

    public String getPosition() {
        return position;
    }

    public void saveMeta(String s, int lived, int died, double victimVelocity, double predatorVelocity) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                    new File(basePath + "/" + s + ".txt"), true));
            writer.write(died+","+lived+","+(double)died/(died+lived)+","+victimVelocity+","+predatorVelocity);
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}