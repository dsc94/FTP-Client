
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author devineni
 */
public class Server implements Runnable{
    Socket socket;
    DataOutputStream dos ;
    DataInputStream dis;
    DataInputStream is;
    OutputStreamWriter osw ;
    InputStreamReader isw;
    BufferedWriter bw;
    BufferedReader br;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    Scanner sc = new Scanner(System.in);
    static HashMap<String,String> session = new HashMap<String,String>();//user sessions
    public Server(Socket socket)
    {
        this.socket = socket;
    }
    
     @Override
    public void run() 
    {
           String username="",password="";
           String request = "";
            try
        {
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        is = new DataInputStream(socket.getInputStream());
        osw = new OutputStreamWriter(dos);
        isw = new InputStreamReader(is);
        bw = new BufferedWriter(osw);
        br = new BufferedReader(isw);
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
           try
            {
            System.out.println("client requested : "+ socket.getInetAddress());
            String command = br.readLine();
            while(command != null)
            {
            switch(command)
            {
                //create a new user session . if it exists do not create a session
                case "CREATE" :
                               System.out.println("command requested " + command);
                               System.out.println("inside create");
                               do
            {
                request = br.readLine();
                System.out.println(request);
                if(request.contains("="))
                {
                String usernameandpassword[] = request.split("=");
                System.out.println(usernameandpassword[0]+usernameandpassword[1]);
                if(usernameandpassword[0].equals("USERNAME"))
                {
                   username = usernameandpassword[1];
                }
                if(usernameandpassword[0].equals("PASSWORD"))
                {
                   password = usernameandpassword[1];
                }
                }
            }while(!request.equals("ENDOFREQUEST"));
                                String sendcommand = saveusersession(username,password);
               System.out.println("command is " + sendcommand);
               System.out.println("Information sent");
               //server root files list
               rootfilesystemsfromclient(username);
               System.out.println("after root file systems from client");
               command = br.readLine();
                               break;
                //upload a file to server
                case "UPLOAD" :
                               System.out.println("command requested " + command);
                               System.out.println("inside upload");
                               String filename="",pathname="";
                               boolean overwrite = false;
              int filesize = 0;
              do
              {
                 request = br.readLine();
                System.out.println(request);
                if(request.contains("="))
                {
                String filenames[] = request.split("=");
                System.out.println(filenames[0]+filenames[1]);
                if(filenames[0].equals("FILENAME"))
                {
                   filename = filenames[1];
                }
                if(filenames[0].equals("FILESIZE"))
                {
                   filesize = Integer.parseInt(filenames[1]);
                }
                if(filenames[0].equals("PATHNAME"))
                {
                    pathname = filenames[1];
                }
                if(filenames[0].equals("OVERWRITE"))
                {
                    String fileoverwrite = filenames[1];
                    if(fileoverwrite.equals("YES"))
                    overwrite = true;
                }
                } 
              }while(!request.equals("ENDOFREQUEST"));
              System.out.println("pathname is filename = "+pathname +" "+filename);
              if(overwrite)
              {
                  System.out.println("true");
                  
              }
              else
              {
                  System.out.println("false");
              }
              //create a file in server to upload
              createfileinserver(filename,filesize,pathname,overwrite);
              //refreshclientui(pathname);
              command = br.readLine();
                               break;
                    //list all the files based on the server path
                case "LIST" :
                     System.out.println("command requested " + command);
                               System.out.println("inside list");
                          pathname = "";
                                do
            {
                request = br.readLine();
                System.out.println(request);
                if(request.contains("="))
                {
                String usernameandpassword[] = request.split("=");
                System.out.println(usernameandpassword[0]+usernameandpassword[1]);
                if(usernameandpassword[0].equals("USERNAME"))
                {
                   username = usernameandpassword[1];
                }
                if(usernameandpassword[0].equals("PATHNAME"))
                {
                   pathname = usernameandpassword[1];
                }
                }
            }while(!request.equals("ENDOFREQUEST"));
                               listallfiles(pathname);
                command = br.readLine();
                               break;
                //download files from server 
                case "DOWNLOAD" :
                     pathname="";filename="";
                               System.out.println("command requested " + command);
                               System.out.println("inside download");
                              
              do
              {
                request = br.readLine();
                System.out.println(request);
                if(request.contains("="))
                {
                String filenames[] = request.split("=");
                System.out.println(filenames[0]+filenames[1]);
                if(filenames[0].equals("PATHNAME"))
                {
                    pathname = filenames[1];
                }
                if(filenames[0].equals("FILENAME"))
                {
                    filename = filenames[1];
                }
                } 
              }while(!request.equals("ENDOFREQUEST"));
              //createfileinserver(filename,filesize,pathname);
              System.out.println("pathname is filename = "+pathname +" "+filename);
              downloadfilefromserver(filename,pathname);
              command = br.readLine();
                               break;
                //close this connection when user wants to close
                case "CLOSECONNECTION" :
                            System.out.println("command requested " + command);
                            System.out.println("inside close connection");
                              do
            {
                request = br.readLine();
                System.out.println(request);
                if(request.contains("="))
                {
                String usernameandpassword[] = request.split("=");
                System.out.println(usernameandpassword[0]+usernameandpassword[1]);
                if(usernameandpassword[0].equals("USERNAME"))
                {
                   username = usernameandpassword[1];
                }
                if(usernameandpassword[0].equals("PASSWORD"))
                {
                   password = usernameandpassword[1];
                }
                }
            }while(!request.equals("ENDOFREQUEST"));
                            if(session.get(username).equals(password))
                            {
                                session.remove(username);
                            }
                            closeconnection(this.socket);
                            //command = br.readLine();
                    break;
            }
            
            }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
         //To change body of generated methods, choose Tools | Templates.
    }
    
    public void closeconnection(Socket socket)
    {
        try
        {
        socket.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void downloadfilefromserver(String filename,String pathname)
    {
        File f = new File(pathname+"/"+filename);
        System.out.println("downloadfilefromserver is" + pathname+"/"+filename);
        try
        {
        FileInputStream fis = new FileInputStream(f);
        byte read[] = new byte[(int) f.length()];
        int numberofbytes;
        bw.write("FILESIZE="+f.length()+"\r\n");
        bw.flush();
        while((numberofbytes = fis.read(read)) != -1)
                                    {
                                        dos.write(read,0, read.length);
                                    }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
    public void listallfiles(String pathname)
    {
        String userfilenames[];
        //String dupfilenames[];
        System.out.println("entered with path name " + pathname);
        try
        {
        File userfiles = new File(pathname);
        userfilenames = userfiles.list();
        String dupfilenames[] = new String[userfilenames.length+1];
        dupfilenames[0] = "..";
        for(int i=0;i<userfilenames.length;i++)
        {
            System.out.println(userfilenames[i]);
            dupfilenames[i+1] = userfilenames[i];
        }
        oos.writeObject(dupfilenames);
        oos.writeObject(pathname);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void rootfilesystemsfromclient(String username)
    {
        String userfilenames[];
        //String dupuserfilenames[];
        try
        {
        System.out.println("----------------");
        System.out.println("File Systems from client ");
        System.out.println("----------------");
        System.out.println("Enter file system you want to enter to Ex : ENTER C://");
        System.out.println("Working Directory = " +
              System.getProperty("user.dir"));
        String pathname = System.getProperty("user.dir");
        if(session.containsKey(username))
        {
        System.out.println("pathname is "+pathname);
        File userfiles = new File(pathname);
        userfilenames = userfiles.list();
        String dupuserfilenames[] = new String[userfilenames.length+1];
        dupuserfilenames[0] = "..";
        for(int i=0;i<userfilenames.length;i++)
        {
          dupuserfilenames[i+1] = userfilenames[i];  
        }
        oos.writeObject(dupuserfilenames);
        oos.writeObject(pathname);
        }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public String saveusersession(String username ,String password)
    {
        String returnstatement = "session created";
        try
        {
        //String username = br.readLine();
        System.out.println("username is "+username);
        //String password = br.readLine();
        System.out.println("paswword is"+password);
        //System.out.println("next reading line is"+br.readLine());
        if(session.containsKey(username) && session.get(username).equals(password))
        {
            
            System.out.println("user name exists");
            bw.write("STATUS=400\r\n");
	    bw.flush();
            returnstatement =  "Session exists";
            
        }
        else
        {
        System.out.println("username do not exist");
        System.out.println("Session not exists");   
        session.put(username, password);
        bw.write("STATUS=200\r\n");
	bw.flush();
        }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return returnstatement;
    }
    
    public void  createfileinserver(String filename,int filesize,String pathname1,boolean overwrite)
    {
        String checkwrite="";
        byte writeinto[] = new byte[filesize];
        System.out.println("File to be uploaded is " + pathname1+"/"+filename);
        try
        {
        File f = new File(pathname1+"/"+filename);
        if(f.exists() && !overwrite)
        {
            System.out.println("FILE EXISTS"+" "+overwrite);
            bw.write("FILEEXISTS"+"\r\n");
            bw.flush();
        }
        else if(f.exists() && overwrite)
        {
            System.out.println("FILE EXISTS"+ " "+ overwrite);
            System.out.println(" file overwriting");
            FileOutputStream fos = new FileOutputStream(f);
            int bytes;
            dis.readFully(writeinto);
            System.out.println("dsc");
            fos.write(writeinto);
            refreshclientui(pathname1);
        }
        else
        {
            bw.write("FILENOTEXISTS"+"\r\n");
            bw.flush();
            if(f.createNewFile())
        {
            System.out.println("new file created");
            FileOutputStream fos = new FileOutputStream(f);
            int bytes;
            dis.readFully(writeinto);
            System.out.println("dsc");
            fos.write(writeinto);
            refreshclientui(pathname1);
        }
        }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void refreshclientui(String pathname)
    {
       String serverfiles[];
       try
       {
       System.out.println("refreshing client ui pathname is "+pathname);
      // bw.write("REFRESHSERVER\n");
       File f = new File(pathname);
       serverfiles = f.list();
       String dupfilenames[] = new String[serverfiles.length + 1];
       dupfilenames[0] = "..";
        for(int i=0;i<serverfiles.length;i++)
        {
          dupfilenames[i+1] = serverfiles[i];  
        }
       oos.writeObject(dupfilenames);
       oos.writeObject(pathname);
       //bw.flush();
       }
       catch(Exception e)
       {
           System.out.println(e);
       }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ServerSocket serversocket = null;
        if(args.length == 0)
        {
            System.out.println("Port number not specified");
            System.exit(0);
        }
        else if(args.length >= 2)
        {
            System.out.println(" Multiple Port numbers");
            System.exit(0);
        }
        
        else
        {
            Socket socket;
            int port = Integer.parseInt(args[0]);
            try
            {
            serversocket = new ServerSocket(port);
            while(true)
            {
            socket = serversocket.accept();
            new Thread(new Server(socket)).start();
            }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
