/*
    Project S3
	HTTP SERVER by Rafamantanantsoa Rotsy Vonimanitra & Radamatsihoharana Lova Mihaja
	IT University Madagascar
*/
package socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
 
 import javax.json.Json;
 import javax.json.JsonObject;
 import javax.json.JsonReader;

public class ReadConf {
	public JsonObject getConfig() {
		File jsonInputFile = new File("-------------------path to json file");
        InputStream is;
        JsonObject empObj = null;
        try {
            is = new FileInputStream(jsonInputFile);
            // Create JsonReader from Json.
            JsonReader reader = Json.createReader(is);
            // Get the JsonObject structure from JsonReader.
            empObj = reader.readObject();
            reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return empObj;
	}
	public String getIpHost(){
		JsonObject obj=this.getConfig();
		return obj.getString("ip");
	}
	public String getDocumentRoot(){
		JsonObject obj=this.getConfig();
		return obj.getString("DocumentRoot");
	}
	public String getHostName(){
		JsonObject obj=this.getConfig();
		return obj.getString("hostName");
	}
	public String getPort(){
		JsonObject obj=this.getConfig();
		return obj.getString("port");
	}
	public String getDocumentSrc(){
		JsonObject obj=this.getConfig();
		return obj.getString("DocumentSrc");
	}
	
}