package logica;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.io.IOException;
import java.net.UnknownHostException;

public class ManejadorDB {
    
    String dbName = "granja";
    public DB getConexion() throws UnknownHostException{
        MongoClient mongoClient = new MongoClient("localhost" ,27017);
        DB db = mongoClient.getDB(dbName);
        return db; 
    }
    
    public void getHistorico() throws UnknownHostException{
         DBCollection coll = getConexion().getCollection(dbName);
         DBCursor cursor = coll.find();
         while (cursor.hasNext()) { 
            DBObject obj=cursor.next();
            System.out.println("========================");
            System.out.println(obj.get("_id")); 
            System.out.println(obj.get("lector")); 
            System.out.println(obj.get("sTemp")); 
            System.out.println(obj.get("sHum")); 
            System.out.println("========================");
         }
    }
    
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