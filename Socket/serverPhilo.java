import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class serverPhilo {
    public static void main (String args[]) {
        ServerSocket serverSocket;
        Socket socket;
        
        while (true) {
            try {
                serverSocket=new ServerSocket(1234);
                socket=serverSocket.accept();

                RequestProcessor rp=new RequestProcessor(socket);
                rp.start();

                try {rp.join();} catch (Exception e) {}

                serverSocket.close();
                socket.close();
            }
            catch (Exception e) {}
        }
    }
}

class RequestProcessor extends Thread {
    Socket socket;
    boolean[] forkStatus=new boolean[5];

    public RequestProcessor (Socket socket) {
        this.socket=socket;
        for (int i=0;i<5;i++) forkStatus[i]=false;
    }

    public void run ( ) {
        try {
            InputStream is=socket.getInputStream();
            InputStreamReader ir=new InputStreamReader(is);
            BufferedReader rd=new BufferedReader(ir);

            OutputStream os=socket.getOutputStream();
            OutputStreamWriter ow=new OutputStreamWriter(os);
            BufferedWriter wr=new BufferedWriter(ow);

            while (true) {
                String message=rd.readLine();
                
                if (message==null) continue;
                if (message.equals("END")) break;
                int idx=Integer.parseInt(message);
                int forkLeft=idx,forkRight=(idx+1)%5;
                message=rd.readLine();
                if (message.equals("TAKE")) {
                    if (forkStatus[forkLeft] || forkStatus[forkRight]) {
                        wr.write("NO\n");
                        wr.flush();
                    }
                    else {
                        forkStatus[forkLeft]=true;
                        forkStatus[forkRight]=true;
                        wr.write("YES\n");
                        wr.flush();
                    }
                }
                else if (message.equals("RELEASE")) {
                    forkStatus[forkLeft]=false;
                    forkStatus[forkRight]=false;
                    wr.write("YES\n");
                    wr.flush();
                }
                else {
                    System.out.println("Error occurs");
                }
            }
            
        }
        catch (Exception e) {}
    }
}