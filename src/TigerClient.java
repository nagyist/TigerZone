
/**********************************************************************

  “TigerClient” Class will establish a socket connection to the server using a provided IP address and port number. 
  The TigerClient will listen on then socket for messages from the server. 
  The client will provide an output stream for which messages will be sent to the server.   
  ***See below as example of the main application.*** 

***********************************************************************/

/*
public static void main(String[] args) throws IOException 
    {

		int score = 0;
		int tigers = 7;
    	Player player = new Player(score, tigers);
      
    	String username = "Red";
    	String password = "Obiwan77";
    	String tournamentPassword = "PersianRocks!";
    	String pid = null;
    	    	
    	// Get IP address, if none provided use “localhost” 
        String serverAdx = (args.length == 0)?"localhost":args[1];
    	
        if (player.client == null)
        {
        	System.out.println("I am null?");
        }
        
        
        if (player.client.connect(serverAdx) == false)
        {
        	//Cannot establish connection 
        	System.out.println("Cannot establish connection");
        	return;
        }
    	
    	pid = player.client.authenticateProtocol(username, password, tournamentPassword);
    	
    	if (pid == null)
    	{
    		// authentication failed
    		System.out.println("Authentication failed.");
    		return;
    	}
    	
    	// Get the number of rounds to be played 
    	int rounds = player.client.challengeProtocol();
    	
    	if (rounds < 0)
    	{
    		// Invalid: End Game
    		System.exit(0);
    	}
    	
    	System.out.printf("Number of rounds: %d", rounds);
    	
    	for(int i = 0; i < rounds; i++)
    	{
    		// Rounds Protocol
    		
    		// Begin rounds
    		int roundID = player.client.roundProtocol();

    	}

    
    	while(true)
    	{
    	     // Sit here 	
    	}
    	
    	  
    }
    
*/

/* The TigerClient will realize the Tigerzone protocol which is entirely text based. */ 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/* A client for the Tigerzone game */
public class TigerClient 
{
    private static int PORT = 9090;  // TODO: Get port number. For now hardcoded 
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean connected = false; 
	public MatchParam match;

    public TigerClient()
    {
    	// Empty Constructor
    }
    
    /* Constructs the client by connecting to a server */
    public boolean connect(String serverAdx) throws IOException  
    {	
        // Setup connection
        try {
			socket = new Socket(serverAdx, PORT);
		} catch (UnknownHostException e) 
		{
			
			return false;
			
		} catch (IOException e) 
		{
			
			return false;
		}

	    // input stream
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	      
        // output stream
        // Second argument is boolean value. When true: println / printf methods will flush the output buffer
        out = new PrintWriter(socket.getOutputStream(), true);
        
        connected = true;
        
        return connected;
    }

    /* The TigerClient will have a method that will talk with the server and realizes the communication protocols. */

    public String authenticateProtocol(String username, String password, String tournamentPassword) throws IOException
    {
    	String pid = null;
    	
    	String response;
        boolean authenticated = false;
        
        // TODO: display server messages on terminal screen
        
        while(authenticated == false)
        {
        	
        	// Poll the input string until something "pops" up
            response = in.readLine();

            // Authentication Protocol

            if (response.startsWith("THIS IS SPARTA!")) 
            {
	           
            	// display message on terminal screen
            	System.out.println(response);
            	
            	// TODO: NEED NEW LINES!!!!!!
            	out.printf("JOIN %s\n", tournamentPassword);
            	  
            } 
            
            else if (response.startsWith("HELLO!")) 
            {
		          
            	
            	System.out.println(response);
            	
            	// send back “I am ” with username and password  
            	out.printf("I AM %s %s\n", username, password);
            	
            }
            
            else if (response.startsWith("WELCOME")) 
            {
		        // End of authentication
            	
            	System.out.println(response);
            	
		        //Save off passed in PID
            
            	String delims = "[ ]";
            	String[] tokens = response.split(delims);
            	
            	pid = tokens[1];
            	
		        authenticated = true;
	        }

            
            else if (response != null)
            {
            	// Invalid or unexpected response
            	return null;
            }      
        }
    	
    	return pid;
    }
    
    
    public int challengeProtocol() throws IOException
    {
    	String response;
    	String stringRounds = null;
    	boolean newChallenge = false;
    	
    	// TODO:  Do we need to return back the cid? 
    	
    	while(newChallenge == false)
    	{
    		response = in.readLine();
    		
    		if (response.startsWith("NEW CHALLENGE"))
    		{
    			System.out.println(response);
    			
    			String delims = "[ ]";
            	String[] tokens = response.split(delims);
            	
            	stringRounds = tokens[6];
    			
            	newChallenge = true;
    		}
    		else if (response != null)
    		{
    			// Invalid or unexpected response
    			return -1;
    		}
    	
    	}
    	
    	int rounds = Integer.parseInt(stringRounds);

    	return rounds;
    	
    }
    
    public void moveProtocol(int type, int gid, String tile, int x, int y, int orientation, int zone)
    {
    	// type = move type
    	// type = 1:  GAME <gid> PLACE <tile> AT <x> <y> <orientation> NONE
    	// type = 2:  GAME <gid> PLACE <tile> AT <x> <y> <orientation> CROCODILE 
    	// type = 3:  GAME <gid> PLACE <tile> AT <x> <y> <orientation> TIGER <zone>
    	// type = 4:  GAME <gid> TILE <tile> UNPLACEABLE PASS
    	// type = 5:  GAME <gid> TILE <tile> UNPLACEABLE RETRIEVE TIGER AT <x> <y>
    	// type = 6:  GAME <gid> TILE <tile> UNPLACEABLE ADD ANOTHER TIGER TO <x> <y>
    	
    	String moveString = null;
    	
    	switch(type)
    	{
    		case 1:
    			moveString = String.format("GAME %d PLACE %s AT %d %d %d NONE", gid, tile, x, y, orientation); 
    			break;
    		case 2:
    			moveString = String.format("GAME %d PLACE %s AT %d %d %d CROCODILE", gid, tile, x, y, orientation);
    			break;
    		case 3:
    			moveString = String.format("GAME %d PLACE %s AT %d %d %d TIGER %d", gid, tile, x, y, orientation, zone);
    			break;
    		case 4:
    			moveString = String.format("GAME %d PLACE %s UNPLACEABLE PASS", gid, tile);
    			break;
    		case 5:
    			moveString = String.format("GAME %d PLACE %s UNPLACEABLE RETRIEVE TIGER AT %d %d ", gid, tile, x, y);
    			break;
    		case 6:
    			moveString = String.format("GAME %d PLACE %s UNPLACEABLE ADD ANOTHER TIGER TO %d %d", gid, tile, x, y);
    			break;
    	}
    	
    	out.println(moveString);
    	
    }
        
    public int roundProtocol() throws IOException
    {
    	String response;
    	String stringRoundID = null;
    	boolean newRound = false;
    	
    	while(newRound == false)
    	{
    		response = in.readLine();
    		
    		if (response.startsWith("BEGIN ROUND"))
    		{
    			System.out.println(response);
    			
    			String delims = "[ ]";
            	String[] tokens = response.split(delims);
            	
            	stringRoundID = tokens[2];
            	// rounds = tokens[4];
    			
            	newRound = true;
    		}
    		else if (response != null)
    		{
    			// Invalid or unexpected response
    			return -1;
    		}
    	
    	}
    	
    	    	
    	int roundID = Integer.parseInt(stringRoundID);
    	
    	return roundID;
    }
    
    public void matchProtocol()
    {
    	// TODO: Match Protocol
    	
    	
    	
    	
    }
    
    
    
    public boolean isConnected()
    {
    	return connected;
    }
     
    
    public void sendToServer(String message)
    {
    	out.println(message);
    }
    
    public String readFromServer() throws IOException 
    {
    	String response = null;
    	
    	response = in.readLine();
    	
    	return response;
    }
  
}
