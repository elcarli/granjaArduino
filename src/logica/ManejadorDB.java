package logica;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.awt.List;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ManejadorDB {
    
    String dbName = "granja";
    public DB getConexion() throws UnknownHostException{
        MongoClient mongoClient = new MongoClient("localhost" ,27017);
        DB db = mongoClient.getDB(dbName);
        return db; 
    }
    
    public DBCursor getHistorico(String lector, String sensor) throws UnknownHostException{
        DBCollection coll = getConexion().getCollection(dbName);
        BasicDBObject whereQuery = new BasicDBObject();
	whereQuery.put("lector", lector);
        
        if (sensor=="Temperatura") {
            sensor = "sTemp";
            
        }
        else if(sensor=="Humedad") {
            sensor = "sHum";
        }
        
        DBCursor cursor = coll.find(whereQuery).sort(new BasicDBObject(sensor, -1));
        
	

        
        return cursor;
//         while (cursor.hasNext()) { 
//            DBObject obj=cursor.next();
//            
//            System.out.println(obj.get("_id")); 
//            System.out.println(obj.get("lector")); 
//            System.out.println(obj.get("sTemp")); 
//            System.out.println(obj.get("sHum")); 
//            System.out.println("========================");
//         }
        
    }
    
    
//    public int[] getAlarma() throws UnknownHostException {
//        DBCollection coll = getConexion().getCollection(dbName);
//        BasicDBObject o1 = new BasicDBObject();
//        o1.put("lector", "lector1");
//        coll.get
//        
//        System.out.println("Del Lecto 1, Este fue el ultimo sTemp: "+o1.get("sTemp")+" sHum: "+o1.get("sHum"));
//         
//         
//         
//         int datos[] = {1, 2};
//        return datos;
//    }
    
    public void borrarBD() throws UnknownHostException {
        DBCollection coll = getConexion().getCollection(dbName);
        coll.drop();
    }
        
        
/*    
    public void borrarDocumento(int valor) throws UnknownHostException{
        
        DBCollection coll = getConexion().getCollection(dbName);
        
        BasicDBObject doc = new BasicDBObject();
        doc.put("valor", valor);
        
        //Elimino
        coll.remove(doc);
    }
*/    

    
     
}