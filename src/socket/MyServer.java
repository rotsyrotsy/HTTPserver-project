package socket;
import java.io.IOException;

public class MyServer {
	
	public static void main(String[] args) throws InterruptedException {
		ReadConf config=new ReadConf();
		int portNumber = Integer.parseInt(config.getPort()) ;
		try {
			// initializing the Socket Server
			MultiThreadedServer socketServer = new MultiThreadedServer(portNumber);
			socketServer.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}