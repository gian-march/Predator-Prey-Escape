package ar.edu.itba.sim;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static ar.edu.itba.sim.Constants.*;
import static ar.edu.itba.sim.Constants.L;

public class Victim implements Comparable<Victim> {

    private Integer id; //id of particle
    private Double x; //x position of particle
    private Double prevx;
    private Double y; //y position of particle
    private Double prevy;
    private Double vx; //x velocity of particle
    private Double vy; //y velocity of particle
    private Double radius; //radius of particle
    private Double mass; //mass of particle
    private Double angle; //value of angle
    private Double targetX;
    private Double targetY;
    private Set<Victim> neighbours; //list of neighbours
    private Double speed;
    private boolean willCollide;
    //TODO: Importante cambiar esto
    private boolean dead = false;
    private boolean escaping = false;
    private Predator predator = null;

    private List<Victim> victimSameCell;

    public Victim(int id, Double x, Double y, Double vx, Double vy, Double radius, Double mass, Double angle) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.radius = radius;
        this.angle = angle;
        this.prevx = 0.0;
        this.prevy = 0.0;
        this.speed = 0d;
        this.targetX = 0d;
        this.targetY = 0d;
        this.neighbours = new TreeSet<>();
        this.victimSameCell = new LinkedList<>();
        this.victimSameCell.add(this);
    }

    public boolean equals(Object o){
        if(o == null || o.getClass() != this.getClass()) return false;
        if(o == this) return true;
        Victim p = (Victim) o;
        return this.id.equals(p.id);
    }

    @Override
    public int compareTo(Victim victim) {
        return id.compareTo(victim.getId());
    }


    public double angleBetweenVictim(Victim p){
        return Math.atan2(p.getY() - y, p.getX() - x);
    }

    public double wdistanceToVictim(Victim p){
        double deltaX = p.getX() - x;
        double deltaY = p.getY() - y;
        return Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2));
    }
    public void beingChased(Predator p){
        escaping = true;
        predator = p;
    }

    public double moduleSpeed(){
        return Math.sqrt(vx*vx+vy*vy);
    }

    public double[] velVersor(){
        double speed = moduleSpeed();
        return new double[] { (vx / speed), (vy / speed)};
    }

    public void updateRadius(double maxR, double tau, double deltaT){
        if(willCollide) return;
        if(radius < maxR) this.radius = Math.min(radius + maxR * deltaT / tau,maxR); // Formula 8 de EZ10712_published.pdf
    }

    public void updateSpeed(double vdmax, double minR, double maxR, double beta){
        //Formula 1 de EZ10712_published.pdf
        if(willCollide) return;
        speed = vdmax * Math.pow(((radius - minR) / (maxR - minR)), beta);
    }

    public void updatePosition(double deltaT, double L){
        x = this.x + VICTIM_VELOCITY * vx * deltaT;
        y = this.y + VICTIM_VELOCITY * vy * deltaT;
        if(x - radius <= 0)
            x = radius;
        if(x + radius >= L)
            x = L-radius;
        if(y + radius >= L)
            y = L-radius;
        if(y - radius <= 0)
            y = radius;
    }

    public boolean reachedTarget(){
        double deltaX = exitX - x;
        double deltaY = exitY - y;
        if(Math.sqrt(deltaX*deltaX+deltaY*deltaY)  < 2 * radius)
            return true;
        return false;
    }
    public boolean isDead(){
        return dead;
    }

    public void approachToTarget(){
        double deltaX = exitX-x;
        double deltaY = exitY-y;
        double h = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        vx = deltaX/(h);
        vy = deltaY/(h);
    }

    public boolean beingChasedByPredator(Predator p){
        double deltaX = p.getX() - x;
        double deltaY = p.getY() - y;
        double dist = Math.sqrt(deltaX*deltaX+deltaY*deltaY);
        if(dist < RADIO_INTERACTION)
            return true;
        return false;

    }
    public void collideVictim(Victim other) {
        double deltaX = other.getX() - x;
        double deltaY = other.getY() - y;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if(distance - other.getRadius() - radius < 0.0001){
            double sigma = radius + other.radius;
            double dx = other.getX() - x;
            double dy = other.getY() - y;
            double dvx = other.getvx() - vx;
            double dvy = other.getvy() - vy;
            double dvdr = dvx*dx +dvy*dy;
            double J = (2 * mass * other.mass*dvdr)/(sigma*(mass + other.mass));
            double Jx = J*dx/sigma;
            double Jy = J*dy/sigma;

            vx = vx + Jx/mass;
            vy = vy + Jy/mass;

            vx = other.vx - Jx/other.mass;
            other.vy = other.vy - Jy/other.mass;
        }

    }

    public void escape(Predator predator){
        double deltaX = predator.getX() - x;
        double deltaY = predator.getY() - y;
        double h = Math.sqrt(deltaX*deltaX + deltaY*deltaY);

        //Esta bien??
        vx -= deltaX/(h);
        vy -= deltaY/(h);

        //Normaliza
        //double aux = Math.sqrt(vx*vx+vy*vy);
        //vx = vx/aux;
        //vy = vy/aux;
    }
    public void normalize() {
        double aux = Math.sqrt(vx*vx+vy*vy);
        vx = vx/aux;
        vy = vy/aux;
    }
    //Setea peso a objetivo
    public void normalizeVelocity() {
        double deltaX = x - exitX;
        double deltaY = y - exitY;
        double r = Math.sqrt(Math.pow(deltaX,2)+Math.pow(deltaY,2));
        //double aux = Math.sqrt(vx*vx+vy*vy);
        vx = vx/(r);
        vy = vy/(r);
        double aux = Math.sqrt(vx*vx+vy*vy);
        vx = vx/aux;
        vy = vy/aux;
    }

    public boolean isColliding(Victim p2){
        return distanceToVictim(p2) < (radius + p2.radius);
    }
    public double distanceToVictim(Victim p){
        double deltaX = p.getX() - x;
        double deltaY = p.getY() - y;
        return Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2));
    }

    public void collisionWithWalls(){
        if(x - radius <= 0 || x + radius >= L) {
            vx = -1 * vx;
        }
        if(y + radius >= L   || y - radius <= 0) {
            vy = -1 * vy;
        }
    }

    public boolean collideWithPredator(Predator p){
        double deltaX = p.getX() - x;
        double deltaY = p.getY() - y;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if(distance - p.getRadius() - radius < 0.0001)
            return true;
        return false;
    }

    public void collideWithParticle(Victim p, double rmin, double ve){
        //calcular versor hacia la partícula
        //No verifica choque condicion periodica de contorno
        double deltaX = p.getX() - x;
        double deltaY = p.getY() - y;
        double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        //utilizar la dirección contraria
        collide(rmin, ve, -deltaX / length, -deltaY / length);
    }

    public void collide(double rmin, double ve, double vx, double vy){
        //cambiar el radio
        setRadius(rmin);
        //cambiar la rapidez
        setSpeed(ve);
        //cambiar los versores de la velocidad
        setvx(vx);
        setvy(vy);
    }

    public Double getTargetX() {
        return targetX;
    }

    public void setTargetX(Double targetX) {
        this.targetX = targetX;
    }

    public Double getTargetY() {
        return targetY;
    }

    public void setTargetY(Double targetY) {
        this.targetY = targetY;
    }

    public boolean isWillCollide() {
        return willCollide;
    }

    public void setWillCollide(boolean willCollide) {
        this.willCollide = willCollide;
    }

    public List<Victim> getVictimSameCell() {
        return victimSameCell;
    }

    public void setVictimSameCell(List<Victim> victimSameCell) {
        this.victimSameCell = victimSameCell;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.prevx = this.getX();
        this.x = x;
    }

    public Double getPrevx() {
        return prevx;
    }

    public void setPrevx(Double prevx) {
        this.prevx = prevx;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.prevy = this.getY();
        this.y = y;
    }

    public Double getPrevy() {
        return prevy;
    }

    public void setPrevy(Double prevy) {
        this.prevy = prevy;
    }

    public Double getvx() {
        return vx;
    }

    public void setvx(Double vx) {
        this.vx = vx;
    }

    public Double getvy() {
        return vy;
    }

    public void setvy(Double vy) {
        this.vy = vy;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public Double getMass() {
        return mass;
    }

    public void setMass(Double mass) {
        this.mass = mass;
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public Set<Victim> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(Set<Victim> neighbours) {
        this.neighbours = neighbours;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public double getVelocityNotCollided() {
        if(!willCollide) return getSpeed();
        return 0;
    }
    public void killVictim(){
        dead=true;
    }

    

    public boolean near(Predator n) {
        double deltaX = x - n.getX();
        double deltaY = y - n.getY();
        double r = Math.sqrt(Math.pow(deltaX,2)+Math.pow(deltaY,2));
        //System.out.print(r + " vx= "+ vx + " vy= " + vy + " ");
        return r < RADIO_INTERACTION_VICTIM;
    }


}
