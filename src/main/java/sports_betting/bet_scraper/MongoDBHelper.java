package main.java.sports_betting.bet_scraper;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.util.List;
import java.util.Map;

public class MongoDBHelper {
    private static String MONGO_HOST;
    private static int MONGO_PORT;
    private static MongoClient mongoClient;

    public static void init() {
        MONGO_HOST = System.getenv().getOrDefault("MONGO_HOST", "localhost");
        MONGO_PORT = Integer.parseInt(System.getenv().getOrDefault("MONGO_PORT", "27017"), 10);
        mongoClient = new MongoClient(MONGO_HOST, MONGO_PORT);
    }


    public static void upsert(String databaseName, String collectionName, Map<String, Object> data, List<String> indexFields) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        MongoCollection<Document> collection = database.getCollection(collectionName);

        BasicDBObject document = new BasicDBObject();
        document.putAll(data);

        BasicDBObject update = new BasicDBObject();
        update.put("$set", document);

        BasicDBObject query = new BasicDBObject();
        for (String field : indexFields) {
            query.put(field, data.get(field));
        }

        UpdateOptions options = new UpdateOptions().upsert(true);

        collection.updateOne(query, update, options);
    }

    public static void close() {
        mongoClient.close();
    }
}
