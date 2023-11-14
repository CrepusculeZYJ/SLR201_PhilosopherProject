import java.util.Random;
import java.io.*;
import java.net.Socket;

public class clientPhilo {
    public static void main (String args[]) {
        System.out.println("Execution starts. Please wait...");

        boolean writeFlag;
        if (args.length<2 || args[1].equals("0")) {
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

        try {
            String serverAddress="localhost";
            if (args.length>0) serverAddress=args[0];
            Socket socket=new Socket(serverAddress,1234);
            table tab=new table(writeFlag);
            philosopheThread phi0=new philosopheThread(tab,0,writeFlag,socket);
            philosopheThread phi1=new philosopheThread(tab,1,writeFlag,socket);
            philosopheThread phi2=new philosopheThread(tab,2,writeFlag,socket);
            philosopheThread phi3=new philosopheThread(tab,3,writeFlag,socket);
            philosopheThread phi4=new philosopheThread(tab,4,writeFlag,socket);

            timeThread ttt=new timeThread();

            phi0.start();
            phi1.start();
            phi2.start();
            phi3.start();
            phi4.start();

            ttt.start();

            try {ttt.join();} catch (Exception e) {}

            phi0.interrupt();
            phi1.interrupt();
            phi2.interrupt();
            phi3.interrupt();
            phi4.interrupt();
            
            try {Thread.sleep(600);} catch (Exception e) {}
            
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

            OutputStream os=socket.getOutputStream();
            OutputStreamWriter ow=new OutputStreamWriter(os);
            BufferedWriter wr=new BufferedWriter(ow);

            wr.write("END\n");
            wr.flush();
            socket.close();
            System.exit(0);
        }
        catch (Exception e) {}
    }
}

class table {

    boolean[] starveStatus=new boolean[5];
    long[] starvationTime=new long[5];
    boolean writeFlag;

    public table (boolean writeFlag) {
        for (int i=0;i<5;i++) {
            starveStatus[i]=false;
        }
        this.writeFlag=writeFlag;
    }

    public synchronized void takeFork (int idx,Socket socket) {
        try {
            while (true) {
                InputStream is=socket.getInputStream();
                InputStreamReader ir=new InputStreamReader(is);
                BufferedReader rd=new BufferedReader(ir);
    
                OutputStream os=socket.getOutputStream();
                OutputStreamWriter ow=new OutputStreamWriter(os);
                BufferedWriter wr=new BufferedWriter(ow);

                wr.write(idx+"\n");
                wr.flush();

                wr.write("TAKE\n");
                wr.flush();

                String message=rd.readLine();

                if (message.equals("NO")) {
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
                else if (message.equals("YES")) {
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
                else {
                    System.out.println("Error occurs!");
                }
            }
        }
        catch (Exception e) {}
    }

    public synchronized void releaseFork (int idx,Socket socket) {
        try {
            while (true) {
                InputStream is=socket.getInputStream();
                InputStreamReader ir=new InputStreamReader(is);
                BufferedReader rd=new BufferedReader(ir);
    
                OutputStream os=socket.getOutputStream();
                OutputStreamWriter ow=new OutputStreamWriter(os);
                BufferedWriter wr=new BufferedWriter(ow);

                wr.write(idx+"\n");
                wr.flush();

                wr.write("RELEASE\n");
                wr.flush();

                String message=rd.readLine();

                if (message.equals("YES")) {
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
    Socket socket;

    public philosopheThread (table tab,int idx,boolean writeFlag,Socket socket) {
        this.tab=tab;
        this.idx=idx;
        this.eatTimes=0;
        this.writeFlag=writeFlag;
        this.socket=socket;
    }

    public void run ( ) {
        Random random=new Random();
        while (!Thread.currentThread().isInterrupted()) {
            int thinkTime=random.nextInt(257);

            if (writeFlag) {
                try {
                    FileWriter fout=new FileWriter("PhilosopherOutput.txt",true);
                    fout.write("Philosopher "+idx+" begins thinking!\n");
                    try {Thread.sleep(thinkTime);} catch (InterruptedException e) {currentThread().interrupt();}
                    fout.write("Philosopher "+idx+" ends thinking!\n");
                    fout.close();
                }
                catch (Exception e) {}
            }
            else {
                System.out.println("Philosopher "+idx+" begins thinking!");
                try {Thread.sleep(thinkTime);} catch (InterruptedException e) {currentThread().interrupt();}
                System.out.println("Philosopher "+idx+" ends thinking!");
            }

            tab.takeFork(idx,socket);

            eatTimes++;

            int eatTime=random.nextInt(257);
            try {Thread.sleep(eatTime);} catch (InterruptedException e) {currentThread().interrupt();}

            tab.releaseFork(idx,socket);
        }
    }
}

class timeThread extends Thread {
    public void run ( ) {
        try {Thread.sleep(10000);} catch (Exception e) {}
    }
}