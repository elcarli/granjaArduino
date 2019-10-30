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

    private final String port;
    private final String name;
    private final SerialPort sp;
    private String sTemp, sHum;
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
    
    public DB getConexion() throws UnknownHostException{
        MongoClient mongoClient = new MongoClient("localhost" ,27017);
        DB db = mongoClient.getDB(dbName);
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
                        sTemp = br.readLine();
                        
                        sHum = br.readLine();
                        
                        BasicDBObject doc=new BasicDBObject();

                        long id=System.nanoTime(); 
                        doc.append("_id", this.name+id);
                        doc.append("lector",this.name);
                        doc.append("sTemp",sTemp);
                        doc.append("sHum",sHum);

                        DBCollection coll=getConexion().getCollection(dbName);
                        coll.insert(doc);
                        
                        
                        int aux1 = Integer.parseInt(sTemp);
                        int aux2 = Integer.parseInt(sHum);
                        
                        //Si la medición sobrepasa los limites, se activa la alarma
                        if((aux1<limInf1 || aux1>limSup1) || (aux2<limInf2 || aux2 > limSup2)) {
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
