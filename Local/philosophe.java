import java.util.Random;
import java.io.FileWriter;

public class philosophe {
    public static void main (String args[]) {
        System.out.println("Execution starts. Please wait...");

        boolean writeFlag;
        if (args.length==0 || args[0].equals("0")) {
            //write to console
            writeFlag=false;
        }
        else writeFlag=true;//write to file

        if (writeFlag) {
            try {
                FileWriter fout=new FileWriter("PhilosopherOutput.txt");
                fout.close();
            }
            catch (Exception e) {}
        }

        table tab=new table(writeFlag);
        philosopheThread phi0=new philosopheThread(tab,0,writeFlag);
        philosopheThread phi1=new philosopheThread(tab,1,writeFlag);
        philosopheThread phi2=new philosopheThread(tab,2,writeFlag);
        philosopheThread phi3=new philosopheThread(tab,3,writeFlag);
        philosopheThread phi4=new philosopheThread(tab,4,writeFlag);

        timeThread ttt=new timeThread();

        phi0.start();
        phi1.start();
        phi2.start();
        phi3.start();
        phi4.start();

        ttt.start();

        try {ttt.join();} catch (Exception e) {}
        
        if (writeFlag) {
            try {
                FileWriter fout=new FileWriter("PhilosopherOutput.txt",true);

                fout.write("\n");

                fout.write("Philosopher 0's eating times = "+phi0.eatTimes+"\n");
                fout.write("Philosopher 1's eating times = "+phi1.eatTimes+"\n");
                fout.write("Philosopher 2's eating times = "+phi2.eatTimes+"\n");
                fout.write("Philosopher 3's eating times = "+phi3.eatTimes+"\n");
                fout.write("Philosopher 4's eating times = "+phi4.eatTimes+"\n");

                fout.close();
            }
            catch (Exception e) {}
        }
        else {
            System.out.println("");

            System.out.println("Philosopher 0's eating times = "+phi0.eatTimes);
            System.out.println("Philosopher 1's eating times = "+phi1.eatTimes);
            System.out.println("Philosopher 2's eating times = "+phi2.eatTimes);
            System.out.println("Philosopher 3's eating times = "+phi3.eatTimes);
            System.out.println("Philosopher 4's eating times = "+phi4.eatTimes);
        }

        System.out.println("End of execution!");

        System.exit(0);
    }
}

class table {

    boolean[] forkStatus=new boolean[5];
    boolean[] starveStatus=new boolean[5];
    long[] starvationTime=new long[5];
    boolean writeFlag;

    public table (boolean writeFlag) {
        for (int i=0;i<5;i++) {
            forkStatus[i]=false;
            starveStatus[i]=false;
        }
        this.writeFlag=writeFlag;
    }

    public synchronized void takeFork (int idx) {
        try {
            while (true) {
                int forkLeft=idx,forkRight=(idx+1)%5;
                if (forkStatus[forkLeft] || forkStatus[forkRight]) {
                    if (!starveStatus[idx]) {
                        if (writeFlag) {
                            try {
                                FileWriter fout=new FileWriter("PhilosopherOutput.txt",true);
                                fout.write("Philosopher "+idx+" is starving...\n");
                                fout.close();
                            }
                            catch (Exception e) {}
                        }
                        else {
                            System.out.println("Philosopher "+idx+" is starving...");
                        }
                        starvationTime[idx]=System.currentTimeMillis();
                    }
                    starveStatus[idx]=true;
                    wait();
                }
                else {
                    forkStatus[forkLeft]=true;
                    forkStatus[forkRight]=true;
                    if (starveStatus[idx]) {
                        long curTime=System.currentTimeMillis();
                        if (writeFlag) {
                            try {
                                FileWriter fout=new FileWriter("PhilosopherOutput.txt",true);
                                fout.write("Philosopher "+idx+"'s starvation time: "+(int)(curTime-starvationTime[idx])+"ms.\n");
                                fout.close();
                            }
                            catch (Exception e) {}
                        }
                        else {
                            System.out.println("Philosopher "+idx+"'s starvation time: "+(int)(curTime-starvationTime[idx])+"ms.");
                        }
                    }
                    starveStatus[idx]=false;
                    if (writeFlag) {
                        try {
                            FileWriter fout=new FileWriter("PhilosopherOutput.txt",true);
                            fout.write("Philosopher "+idx+" begins eating!\n");
                            fout.close();
                        }
                        catch (Exception e) {}
                    }
                    else {
                        System.out.println("Philosopher "+idx+" begins eating!");
                    }
                    notifyAll();
                    return;
                }
            }
        }
        catch (Exception e) {}
    }

    public synchronized void releaseFork (int idx) {
        try {
            while (true) {
                int forkLeft=idx,forkRight=(idx+1)%5;
                if (!forkStatus[forkLeft] || !forkStatus[forkRight]) {
                    wait();
                }
                else {
                    forkStatus[forkLeft]=false;
                    forkStatus[forkRight]=false;
                    if (writeFlag) {
                        try {
                            FileWriter fout=new FileWriter("PhilosopherOutput.txt",true);
                            fout.write("Philosopher "+idx+" ends eating!\n");
                            fout.close();
                        }
                        catch (Exception e) {}
                    }
                    else {
                        System.out.println("Philosopher "+idx+" ends eating!");
                    }
                    notifyAll();
                    return;
                }
            }
        }
        catch (Exception e) {}
    }
}

class philosopheThread extends Thread {
    int idx;
    table tab;
    boolean writeFlag;
    int eatTimes;

    public philosopheThread (table tab,int idx,boolean writeFlag) {
        this.tab=tab;
        this.idx=idx;
        this.eatTimes=0;
        this.writeFlag=writeFlag;
    }

    public void run ( ) {
        Random random=new Random();
        while (true) {
            int thinkTime=random.nextInt(257);

            if (writeFlag) {
                try {
                    FileWriter fout=new FileWriter("PhilosopherOutput.txt",true);
                    fout.write("Philosopher "+idx+" begins thinking!\n");
                    try {Thread.sleep(thinkTime);} catch (Exception e) {}
                    fout.write("Philosopher "+idx+" ends thinking!\n");
                    fout.close();
                }
                catch (Exception e) {}
            }
            else {
                System.out.println("Philosopher "+idx+" begins thinking!");
                try {Thread.sleep(thinkTime);} catch (Exception e) {}
                System.out.println("Philosopher "+idx+" ends thinking!");
            }

            tab.takeFork(idx);

            eatTimes++;

            int eatTime=random.nextInt(257);
            try {Thread.sleep(eatTime);} catch (Exception e) {}

            tab.releaseFork(idx);
        }
    }
}

class timeThread extends Thread {
    public void run ( ) {
        try {Thread.sleep(1000);} catch (Exception e) {}
    }
}