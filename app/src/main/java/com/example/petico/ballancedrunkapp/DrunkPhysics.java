package com.example.petico.ballancedrunkapp;

/**
 * Created by Petico on 2018. 03. 13..
 *
 *
 * surlódási erő: Fs = mu * Fny  //ahol nyomórerő: Fny= m * g (tömegszer nehézségi gyorsulás)
 *
 *
 *
 */

public class DrunkPhysics {


    private double weight; //tömeg kilógram
    private int moveX, moveY;
    private long lastTime;//egy ciklus egy század másodperc
    private double Fs; //surlódási erő
    private double fx;
    private double fy;
    private double vx;
    private double vy;
    private double Fny; //nyomóreő
    private final double mu = 0.07; //surlódási együttható. default: autógumi szfalton

    public DrunkPhysics(double weight, int height) {
        this.weight = weight;
        Fny = weight * 9.81; //nyomóerő kiszámítása tömeg * gravitációs gyorsulás
        Fs = mu * Fny; //alap surlódási erő kiszámítása
        //nyugalmi helyzet
        moveX = 0;
        moveY =0;
        fx = 0.0;
        fy = 0.0;
        vx = 0.0;
        vy = 0.0;
        lastTime = 0;


/*
        fx=actFProcess(200, fx);
        vx = vProcess(acceleration(fx),0.1,vx);
        System.out.println("Nyomóerő: 200N Gyorsulás 1 másodperc alatt megtett út ezzel a gyorsulással 0 kezdősebességgel: " + path(0.1, vx));
        fx=actFProcess(200, fx);
        vx = vProcess(acceleration(fx),0.1,vx);
        System.out.println("Nyomóerő: 200N Gyorsulás 1 másodperc alatt megtett út ezzel a gyorsulással 0 kezdősebességgel: " + path(0.1, vx));
        fx=actFProcess(0, fx);
        vx = vProcess(acceleration(fx),0.1,vx);
        System.out.println("Nyomóerő: 0N Gyorsulás 1 másodperc alatt megtett út ezzel a gyorsulással az előző kezdősebességgel: " + path(0.1, vx));
        fx=actFProcess(0, fx);
        vx = vProcess(acceleration(fx),0.1,vx);
        System.out.println("Nyomóerő: 0N Gyorsulás 1 másodperc alatt megtett út ezzel a gyorsulással az előző kezdősebességgel: " + path(0.1, vx));
        fx=actFProcess(0, fx);
        vx = vProcess(acceleration(fx),0.1,vx);
        System.out.println("Nyomóerő: 0N Gyorsulás 1 másodperc alatt megtett út ezzel a gyorsulással az előző kezdősebességgel: " + path(0.1, vx));
        fx=actFProcess(0, fx);
        vx = vProcess(acceleration(fx),0.1,vx);
        System.out.println("Nyomóerő: 0N Gyorsulás 1 másodperc alatt megtett út ezzel a gyorsulással az előző kezdősebességgel: " + path(0.1, vx));
        fx=actFProcess(0, fx);
        vx = vProcess(acceleration(fx),0.1,vx);
        System.out.println("Nyomóerő: 0N Gyorsulás 1 másodperc alatt megtett út ezzel a gyorsulással az előző kezdősebességgel: " + path(0.1, vx));
        fx=actFProcess(-200, fx);
        vx = vProcess(acceleration(fx),0.1,vx);
        System.out.println("Nyomóerő: -200N Gyorsulás 1 másodperc alatt megtett út ezzel a gyorsulással az előző kezdősebességgel: " + path(0.1, vx));
        fx=actFProcess(0, fx);
        vx = vProcess(acceleration(fx),0.1,vx);
        System.out.println("Nyomóerő: 0N Gyorsulás 1 másodperc alatt megtett út ezzel a gyorsulással az előző kezdősebességgel: " + path(0.1, vx));
        fx=actFProcess(0, fx);
        vx = vProcess(acceleration(fx),0.1,vx);
        System.out.println("Nyomóerő: 0N Gyorsulás 1 másodperc alatt megtett út ezzel a gyorsulással az előző kezdősebességgel: " + path(0.1, vx));
        */

    }

    public void processMove(float forceX, float forceY)
    {
        //moveX = (int)forceX;
        //moveY = (int)forceY;
        double time = deltaTime();
        System.out.println("deltatime:" + time);
        fx=actFProcess(forceX*100, fx);
        vx = vProcess(acceleration(fx),time,vx);
        fy=actFProcess(forceY*100, fy);
        vy = vProcess(acceleration(fy),time,vy);

        moveX = meterToPixel(path(time, vx));
        moveY = meterToPixel(path(time, vy));
    }


    public int getMoveX() {
        return moveX;
    }

    public void setMoveX(int moveX) {
        this.moveX = moveX;
    }

    public int getMoveY() {
        return moveY;
    }

    public void setMoveY(int moveY) {
        this.moveY = moveY;
    }

    public double getFx() {
        return fx;
    }

    public void setFx(double fx) {
        this.fx = fx;
    }

    public double getFy() {
        return fy;
    }

    public void setFy(double fy) {
        this.fy = fy;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }


    /**
     * Aktuális erő kiszámítása
     *
     * */
    private double actFProcess(double Fny, double Fakt)
    {
        if(Math.abs(Fakt + Fny) > Math.abs(Fs))
        {
            if(Fakt + Fny > 0)
            {
                return Fakt + Fny - Fs;
                //System.out.println(Fakt + " + " + Fny + " - " + Fs);
            }
            else{
                return Fakt + Fny + Fs;
                //System.out.println(Fakt + " + " + Fny + " + " + Fs);
            }
        }
        else{
            //System.out.println("ABS: " + Fakt + " + " + Fny + " < " + Fs + " --> megáll");
            return 0;
        }
    }

    /**
     * Gyorsulás Erő - Surlódási erő osztva a tömeggel
     * */
    private double acceleration(double F)
    {
        return (F / weight);
    }

    /**
     * Megtett út gyorsulás, idő és kezdősebesség alapján
     * */
    private double vProcess(double a, double t, double kezdoV) //a gyorsulás //t idő
    {
        double deltaV = a * t;
        return (kezdoV + deltaV)/2;//átlagolni kell
    }

    /**
     * Megtett út gyorsulás, idő és kezdősebesség alapján
     * */
    private double path(double t, double V) //a gyorsulás //t idő
    {
        return V * t;
    }

    /**
     * Két futás közt eltelt idő
     *
     * */
    private double deltaTime()
    {
        long deltaTime;
        long currentTime;
        //elsőnek
        if(lastTime ==0){
            lastTime = System.nanoTime();
        }

        currentTime = System.nanoTime();
        deltaTime = currentTime - lastTime;

        lastTime = currentTime;

        return  (double) (deltaTime) / 1000000000;
    }

    /**
     * a metrikus távolságot pixelekre váltja egyelőre 100 pixel egy méter
     * */
    private int meterToPixel(double meter)
    {
        return (int) Math.round(meter * 100);
    }


}
