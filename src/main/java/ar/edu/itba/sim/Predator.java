package ar.edu.itba.sim;

import static ar.edu.itba.sim.Constants.L;
import static ar.edu.itba.sim.Constants.PREDATOR_VELOCITY;

public class Predator implements Comparable<Predator> {

    private Integer id; //id of particle
    private Double x; //x position of particle
    private Double prevX;
    private Double y; //y position of particle
    private Double prevY;
    private Double vx; //x velocity of particle
    private Double vy; //y velocity of particle
    private Double radius; //radius of particle
    private Double mass; //mass of particle
    private Double angle; //value of angle
    private Double targetX;
    private Double targetY;
    private Double speed;
    private boolean willCollide;
    private Victim chasing = null;
    public int eating = 0;

    public Predator(int id, Double x, Double y, Double vx, Double vy, Double radius, Double mass, Double angle) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.radius = radius;
        this.angle = angle;
        this.prevX = 0.0;
        this.prevY = 0.0;
        this.speed = 0d;
        this.targetX = 0d;
        this.targetY = 0d;
    }

    public boolean equals(Object o){
        if(o == null || o.getClass() != this.getClass()) return false;
        if(o == this) return true;
        Predator p = (Predator) o;
        return this.id.equals(p.id);
    }

    @Override
    public int compareTo(Predator predator) {
        return id.compareTo(predator.getId());
    }


    public double angleBetweenPredator(Predator p){
        return Math.atan2(p.getY() - y, p.getX() - x);
    }

    public double distanceToPredator(Predator p){
        double deltaX = p.getX() - x;
        double deltaY = p.getY() - y;
        return Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2));
    }
    public double distanceToVictim(Victim p){
        double deltaX = p.getX() - x;
        double deltaY = p.getY() - y;
        return Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2));
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
        x = this.x + PREDATOR_VELOCITY * vx * deltaT;
        y = this.y + PREDATOR_VELOCITY * vy * deltaT;

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

    public Victim getChasing() {
        return chasing;
    }

    public void setChasing(Victim chasing) {
        this.chasing = chasing;
    }

    public int getEating() {
        return eating;
    }

    public void setEating(int eating) {
        this.eating = eating;
    }

    public void follow(Victim victim){
        double deltaX = victim.getX() - x;
        double deltaY = victim.getY() - y;
        double h = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        vx = deltaX/h;
        vy = deltaY/h;

    }
    public void collisionWithWalls(){
        if(x - radius <= 0 || x + radius >= L) {
            vx = -1 * vx;
        }
        if(y + radius >= L   || y - radius <= 0) {
            vy = -1 * vy;
        }
    }
    public void aproachToVictim(Victim v){
        //Defino target
        follow(v);
        chasing = v;
    }
    public boolean isChasing(Victim v){
        if(chasing !=null && chasing.equals(v))
            return true;
        return false;
    }
    public boolean isColliding(Predator p2){
        return distanceToPredator(p2) < (radius + p2.radius);
    }

    public int collisionWithWalls(double w){
        //colisión pared izquierda x = 0
        if(x < radius) return 1;
        //colisión pared derecha x = w
        if(w - x < radius) return 2;
        return 0;
    }

    public void collidePredator(Predator other) {
        double sigma = radius + other.radius;
        double dx = other.getX() - x;
        double dy = other.getY() - y;
        double dvx = other.getVx() - vx;
        double dvy = other.getVy() - vy;
        double dvdr = dvx*dx +dvy*dy;
        double J = (2 * mass * other.mass*dvdr)/(sigma*(mass + other.mass));
        double Jx = J*dx/sigma;
        double Jy = J*dy/sigma;

        vx = vx + Jx/mass;
        vy = vy + Jy/mass;

        vx = other.vx - Jx/other.mass;
        other.vy = other.vy - Jy/other.mass;

    }

    public void collide(double rmin, double ve, double vx, double vy){
        //cambiar el radio
        setRadius(rmin);
        //cambiar la rapidez
        setSpeed(ve);
        //cambiar los versores de la velocidad
        setVx(vx);
        setVy(vy);
    }

    public void collideWithParticle(Predator p, double rmin, double ve){
        //calcular versor hacia la partícula
        //No verifica choque condicion periodica de contorno
        double deltaX = p.getX() - x;
        double deltaY = p.getY() - y;
        double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        //utilizar la dirección contraria
        collide(rmin, ve, -deltaX / length, -deltaY / length);
    }

    public void collideWithWall(int wcol){
        //cambiar el versor x correspondiente depende de que pared era
        //collide(rmin, ve,(wcol == 1)? 1d : -1d, vy);
    }

    public Double gettargetX() {
        return targetX;
    }

    public void settargetX(Double targetX) {
        this.targetX = targetX;
    }

    public Double gettargetY() {
        return targetY;
    }

    public void settargetY(Double targetY) {
        this.targetY = targetY;
    }

    public boolean isWillCollide() {
        return willCollide;
    }

    public void setWillCollide(boolean willCollide) {
        this.willCollide = willCollide;
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
        this.prevX = this.getX();
        this.x = x;
    }

    public Double getPrevX() {
        return prevX;
    }

    public void setPrevX(Double prevX) {
        this.prevX = prevX;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.prevY = this.getY();
        this.y = y;
    }

    public Double getPrevY() {
        return prevY;
    }

    public void setPrevY(Double prevY) {
        this.prevY = prevY;
    }

    public Double getVx() {
        return vx;
    }

    public void setVx(Double vx) {
        this.vx = vx;
    }

    public Double getVy() {
        return vy;
    }

    public void setVy(Double vy) {
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
}
