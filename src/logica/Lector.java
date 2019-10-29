package logica;

import com.fazecast.jSerialComm.SerialPort;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CARLOS
 */
public class Lector extends Thread{

    private String port, name;
    private SerialPort sp;
    private String sTemp, sHum;
    private String dbName = "granja";

    public Lector(String port, String name) {
        this.name = name;
        this.port = port;
        
        this.sp = SerialPort.getCommPort(port);
        this.sp.setComPortParameters(9600, 8, 1, 0);
        this.sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        
    }
    
    public DB getConexion() throws UnknownHostException{
        MongoClient mongoClient = new MongoClient("localhost" ,27017);
        DB db = mongoClient.getDB(dbName);
        return db; 
    }
    
    @Override
    public void run() {
        while (true) {
            
            if(this.sp.openPort()) {
                System.out.println("El puerto "+ this.port +" está abierto");
                if(this.sp.bytesAvailable() > 0) {
                    //lector del Arduino
                    BufferedReader br = new BufferedReader (new InputStreamReader(sp.getInputStream()));
                    try {
                        sTemp = br.readLine();
                        //System.out.println("sensor Temperatura: "+sTemp);   //sensor Temperatura
                    

                        sHum = br.readLine();
                        //System.out.println("Sensor Humedad: "+sHum);   //sensor Humedad

                        //String datos[] = {this.name, sTemp, sHum};

                        BasicDBObject doc=new BasicDBObject();

                        long id=System.nanoTime(); 
                        doc.append("_id", id);
                        doc.append("lector",this.name);
                        doc.append("sTemp",sTemp);
                        doc.append("sHum",sHum);

                        DBCollection coll=getConexion().getCollection(dbName);
                        coll.insert(doc);

                        
                        } catch (IOException ex) {
                        Logger.getLogger(Lector.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
            else {
                System.out.println("No se puede abrir el puerto "+ port);
            }
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
       
}
