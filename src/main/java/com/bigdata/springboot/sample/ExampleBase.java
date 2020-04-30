package com.bigdata.springboot.sample;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class ExampleBase {

    protected static final String DB_NAME = "json_example_db";
    protected static final String COLLECTION_NAME = "json_example_collection";

    protected static ArangoDB arangoDB;
    protected static ArangoDatabase db;
    protected static ArangoCollection collection;

    @BeforeClass
    public static void setUp() {
        arangoDB = new ArangoDB.Builder().build();
        try {
            arangoDB.db(DB_NAME).drop();
        } catch (final ArangoDBException e) {
        }
        arangoDB.createDatabase(DB_NAME);
        db = arangoDB.db(DB_NAME);
        db.createCollection(COLLECTION_NAME);
        collection = db.collection(COLLECTION_NAME);
    }

    @AfterClass
    public static void tearDown() {
        db.drop();
        arangoDB.shutdown();
    }

}