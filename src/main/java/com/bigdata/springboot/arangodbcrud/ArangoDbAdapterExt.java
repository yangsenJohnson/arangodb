package com.bigdata.springboot.arangodbcrud;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.arangodb.velocypack.VPackSlice;


/***
 * 给ArangoDbAdapter加入构造函数，使其能在Controller使用相应的database
 * @ClassName ArangoDbAdapterExt
 * @Description
 * @Author yangsen
 * @Date2020/4/27 16:58
 * @Version V1.0
 **/
public class ArangoDbAdapterExt extends ArangoDbAdapter {
        private  ArangoDB arangoDB = null;

        private ArangoDatabase arangoDatabase = null;

        public ArangoDbAdapterExt(ArangoDB arangoDB, ArangoDatabase arangoDatabase){
                this.arangoDB = arangoDB;
                this.arangoDatabase = arangoDatabase;
        }

        public ArangoDbAdapterExt(){ }

        @Override
        public void insertDocument(String collectionName, BaseDocument myObject){
                arangoDatabase.collection(collectionName).insertDocument(myObject);
        }
        @Override
        public VPackSlice getVelocyDocument(String collectionName, String key){
                return arangoDatabase.collection(collectionName).getDocument(key, VPackSlice.class);
        }
        /**
         * 功能描述:根据AQL查询
         * @param query
         * @return: com.arangodb.ArangoCursor<com.arangodb.entity.BaseDocument>
         * @Author: EDZ
         * @Date: 2020/4/28 10:09
         */
        public ArangoCursor<BaseDocument> executeQuery(String query){
                //根据AQL查询
                return arangoDatabase.query(query,  BaseDocument.class);
        }
}
