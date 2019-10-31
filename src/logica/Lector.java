package logica;

import com.fazecast.jSerialComm.SerialPort;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;

/**
 *
 * @author CARLOS
 */
public class Lector extends Thread{

    private final String port;
    private final String name;
    private final SerialPort sp;
    private int sTemp, sHum;
    private final String dbName = "granja";
    //Limites temperatura
    private int limInf1 = 20;
    private int limSup1 = 45;
    //Limites humedad
    private int limInf2 = 30;
    private int limSup2 = 80;
    private boolean alarma = false;
    

    public Lector(String port, String name) {
        this.name = name;
        this.port = port;
        
        this.sp = SerialPort.getCommPort(port);
        this.sp.setComPortParameters(9600, 8, 1, 0);
        this.sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        
    }
    
    public MongoDatabase getConexion() throws UnknownHostException{
        //Obtiene los datos del logeo en mongo
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE); 
        
        //Conexion
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase(dbName);
        return db;
    }
    
    @Override
    public void run() {
        while (true) {
            
            if(this.sp.openPort()) {
                //System.out.println("El puerto "+ this.port +" está abierto");
                if(this.sp.bytesAvailable() > 0) {
                    //lector del Arduino
                    BufferedReader br = new BufferedReader (new InputStreamReader(sp.getInputStream()));
                    try {
                        sTemp = Integer.parseInt(br.readLine());
                        
                        sHum = Integer.parseInt(br.readLine());
                        
                        Document row = new Document();

                        long id=System.nanoTime(); 
                        row.append("_id", this.name+id);
                        row.append("lector",this.name);
                        row.append("sTemp",sTemp);
                        row.append("sHum",sHum);

                        MongoCollection coll=getConexion().getCollection(dbName);
                        coll.insertOne(row);
                        
                        //Si la medición sobrepasa los limites, se activa la alarma
                        if((sTemp<limInf1 || sTemp>limSup1) || (sHum<limInf2 || sHum > limSup2)) {
                            this.alarma = true;
                        }
                        else {
                            this.alarma = false;
                        }
                        
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

    public boolean isAlarma() {
        return alarma;
    }

}
