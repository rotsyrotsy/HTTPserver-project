/*
    Project S3
	HTTP SERVER by Rafamantanantsoa Rotsy Vonimanitra & Radamatsihoharana Lova Mihaja
	IT University Madagascar
*/
package socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileNotFoundException;

public class SocketClientHandler implements Runnable {

	private Socket client;
	private ReadConf config=new ReadConf();

	public SocketClientHandler(Socket client) {
		this.client = client;
	}
	public boolean textExtension(String str) {
		if(str.contains("txt") || str.contains("html") ||  str.contains("css") ||  str.contains("ico") || str.contains("php")) {
			return true;
		}
		return false;
	}
	public boolean imageExtension(String str) {
		if(str.contains("png") || str.contains("jpg") || str.contains("jpeg") || str.contains("gif")) {
			return true;
		}
		return false;
	}
	@Override
	public void run() {
		try {
			System.out.println("Thread started with name:"+ Thread.currentThread().getName());
				readResponse();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public String executePhp(String path) throws IOException {
	
		String command = "cmd.exe /c \"php.exe " + "\"" + path + "\"";
		Process proc = Runtime.getRuntime().exec(command);			
		BufferedReader request = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String requestHeader = "";
		String temp = ".";
		while (!temp.equals("")) {
			temp = request.readLine();
			if(temp==null) break ;
			requestHeader += temp + "\n";
		}
		return requestHeader;
	}
	private void readResponse() throws IOException, InterruptedException {
		try {
			OutputStream os = client.getOutputStream();
			BufferedReader request = new BufferedReader(new InputStreamReader(client.getInputStream()));
			BufferedWriter response = new BufferedWriter(new OutputStreamWriter(os));

			String requestHeader = "";
			String temp = ".";
			while (!temp.equals("")) {									//get the request from client
				temp = request.readLine();
					if(temp==null) return ;								//exit loop when it's over
				System.out.println(temp);
				requestHeader += temp + "\n";		
			}
			
			String method = requestHeader.split(" ")[0];				// Get the method from HTTP header
			StringBuilder sb = new StringBuilder();
			String file = requestHeader.split("\n")[0].split(" ")[1];	// get the page's name
			String path="";
			if(file.compareTo("/")==0){
				file="/index.html";
			}
			path=config.getDocumentRoot()+file;							// get the page's path
			String extension="";
			
			if(checkURL(path)){		

				extension=file.split("\\.")[1];							// get the page's extension
				if (method.equals("GET")) {
					/// to do in case of text type
					if(this.textExtension(file)) {
						constructResponseHeader(200, sb,"GET",file);
						response.write(sb.toString());
						if (extension.equals("php")){
							response.write(executePhp(path));
						}else {
							response.write(getData(path));
						}
						sb.setLength(0);
						response.flush();
					} 
					// to do in case of image type
					else if (imageExtension(file)) {
						 	FileInputStream is = new FileInputStream(path);
		                    String header = generateHeader(true,file);
		                    int a;
		                    // write the header
		                    for (char c : header.toCharArray()) {
		                        os.write(c);
		                    }
		                    // write the image data
		                    while ((a = is.read()) > -1) {
		                        os.write((byte)a);
		                    }
		                    os.flush();
		                    is.close();
		                    os.close();
					}
				} else if (method.equals("POST")) {
		                StringBuilder builder = new StringBuilder();
		                while (request.ready())
		                    builder.append((char) request.read());

		                // decode the url & convert the special characters
		                //System.out.println(URLDecoder.decode(builder.toString(), "UTF-8"));

		                getDataPhp(URLDecoder.decode(builder.toString(), "UTF-8"),path,method);

						constructResponseHeader(200, sb,"POST",file);
						response.write(sb.toString());
						response.write(executePhp(path));
						sb.setLength(0);
						response.flush();
		            }
			} else{
				if(method.equals("GET") && file.contains("?")) {
					extension=file.split("\\?")[0].split("\\.")[1];
					String f=path.split("\\?")[0];
					path=f;
					
					getDataPhp(file,f,method);

					constructResponseHeader(200, sb,"GET",file);
					response.write(sb.toString());
					response.write(executePhp(path));
					sb.setLength(0);
					response.flush();
				}
				else{
					// 404 page not found
					constructResponseHeader(404, sb,"","");
					response.write(sb.toString());
					response.write(getData(config.getDocumentRoot()+"/pageNotFound.html"));
					sb.setLength(0);
					response.flush();
				}
			}
			
			request.close();
			response.close();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getDataPhp(String url,String file,String method) {
    	try {
    		file=file.split("/")[8];
    		String p=config.getDocumentSrc()+file;
        	String page=getData(p);
        	String vaovao="";
        	String decoded="";
            try {
            	if(method.equals("GET")) {
            		decoded = url.split("\\?")[1];
            	}else if(method.equals("POST")) {
            		decoded = java.net.URLDecoder.decode(url, "UTF-8");
            	}
                String[] pairs = decoded.split("&");
                for (int i=0 ;i<pairs.length; i++) {
                	String variable=pairs[i].split("=")[0];
                	String valeur=pairs[i].split("=")[1];
                	page=page.replace("$_"+method+"['"+variable+"']", "\""+valeur+"\"");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            vaovao=page;
            String path=config.getDocumentRoot()+"/"+file;
            File entree = new File(path);
            BufferedWriter bw = new BufferedWriter(new FileWriter(entree,false));
                 bw.write(vaovao);
                 bw.flush();
            
            bw.close();
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    }
	// Check the URL from the Request header to the server's database
	private static boolean checkURL(String file) {
		File myFile = new File(file);
		return myFile.exists() && !myFile.isDirectory();
	}

	// Construct Response Header
	public void constructResponseHeader(int responseCode,StringBuilder sb,String method, String file) {

		String extension="";
		
		// check if the sentence of the file directory is available
		if(file.compareTo("")!=0) {
			extension=file.split("\\.")[1];
		}
		
		if (responseCode == 200) {
				sb.append("HTTP/1.1 200 OK\r\n");
				sb.append("Date:" + getTimeStamp() + "\r\n"); 
				sb.append("Server:"+config.getIpHost()+":"+config.getPort()+"\r\n");
				sb.append("Allow: "+method+"\r\n");
				if(extension.contains("css")) {
					sb.append("Content-Type: text/css\r\n");
				}else {
					sb.append("Content-Type: text/html\r\n");													// php pages must be in text/html content-type for the browser's interpretation
				}
				sb.append("Connection: Closed\r\n\r\n");
		} else if (responseCode == 404) {

			sb.append("HTTP/1.1 404 Not Found\r\n");
			sb.append("Date:" + getTimeStamp() + "\r\n");
			sb.append("Server:"+config.getHostName()+":"+config.getPort()+"\r\n");
			sb.append("\r\n");
			
		} else if (responseCode == 304) {
			sb.append("HTTP/1.1 304 Not Modified\r\n");
			sb.append("Date:" + getTimeStamp() + "\r\n");
			sb.append("Server:"+config.getHostName()+":"+config.getPort()+"\r\n");
			sb.append("\r\n");
		}
	}

	/// get the text inside the page's link
	public String getData(String file) throws IOException {

		file=file.replace("\\", "/");
		String extension=file.split("\\.")[1];
		
		
		StringBuilder stringBuilder = new StringBuilder();
		if(!extension.contains(("ico"))){
			BufferedReader reader;
			try {
				
				reader = new BufferedReader(new FileReader(file));
				String line = "";
				String ls = System.getProperty("line.separator");
					while ((line = reader.readLine())!=null) {
						stringBuilder.append(line);
						stringBuilder.append(ls);
					} 
				
				reader.close();
				
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
				System.err.println(e.getMessage());
				System.out.println("Client side issue with file name");
				System.exit(1);
				
			} catch (IOException e) {
				
				e.printStackTrace();
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}
		return stringBuilder.toString();
	}

	// TimeStamp
	private static String getTimeStamp() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		String formattedDate = sdf.format(date);
		return formattedDate;
	}
	
	/// header for an image
	private String generateHeader(boolean OK, String file) throws IOException {
		String extension=file.split("\\.")[1];
        if (!OK) {
            return "HTTP/1.1 404 \r\n";
        }

        String response = "";

        response += "HTTP/1.1 200 \r\n";
        response += "Date:" + getTimeStamp() + "\r\n";
        response += "Server:"+config.getHostName()+":"+config.getPort()+"\r\n";
        response += "Accept-Ranges: bytes\r\n";
        response += "Content-Type:image/"+extension+"\r\n";
        response += "Connection: close \r\n";
        response += "Content-Length:" +(new File(config.getDocumentRoot() +file).length())+ "\r\n";
        response += "\r\n";

        return response;
    }
}
