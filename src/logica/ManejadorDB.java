package logica;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.eq;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;



public class ManejadorDB {
    
    String dbName = "granja";
    public MongoDatabase getConexion() {
        //Obtiene los datos del logeo en mongo
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE); 
        
        //Conexion
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase(dbName);
        return db; 
    }
    
    public FindIterable getHistorico(String lector, String sensor) throws UnknownHostException{
        MongoCollection coll = getConexion().getCollection(dbName);
        
        //BasicDBObject whereQuery = new BasicDBObject();
    //	whereQuery.put("lector", lector);
        
        if ("Temperatura".equals(sensor)) {
            sensor = "sTemp";
            
        }
        else if("Humedad".equals(sensor)) {
            sensor = "sHum";
        }
        
        //Ordenar DESC
        FindIterable cursor = coll.find(eq("lector", lector)).sort(new BasicDBObject(sensor, -1));
        
        return cursor;
        
    }
    
    public AggregateIterable<Document> getProm() throws UnknownHostException {
        MongoCollection coll = getConexion().getCollection(dbName);
        
        AggregateIterable<Document> datos = coll.aggregate(
                                                Arrays.asList(
                                                        //Aggregates.match(Filters.eq("lector", "lector1")),
                                                        Aggregates.group("$lector", Accumulators.avg("promTemp", "$sTemp"), Accumulators.avg("promHum", "$sHum"))
                                                )
                                              );
        for (Document dbObject : datos) {
            System.out.println(dbObject);
        }
        return datos;
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
        MongoCollection coll = getConexion().getCollection(dbName);
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