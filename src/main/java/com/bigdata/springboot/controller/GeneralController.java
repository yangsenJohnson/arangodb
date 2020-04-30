package com.bigdata.springboot.controller;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.bigdata.springboot.arangodbcrud.ArangoDbAdapter;
import com.bigdata.springboot.arangodbcrud.ArangoDbAdapterExt;
import com.bigdata.springboot.config.ExperimentConfig;
import com.bigdata.springboot.repository.ClusterRepository;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用的Controller
 */

@RestController
@RequestMapping("/api/v1")
//@ComponentScan()
public class GeneralController {

//    @Value("${param.host}")
//    String host;
//    @Value("${param.port}")
//    int port;
//    @Value("${param.dbName}")
//    String dbName;
//    @Value("${param.collectionName}")
//    String collectionName;
        @Autowired
        public ExperimentConfig experimentConfig;
        //        @Autowired
//        private  ArangoDB arangoDB;
        private ArangoDB arangoDB = new ArangoDB.Builder().host(experimentConfig.getHost(), experimentConfig.getPort()).build();

        private ArangoDatabase arangoDatabase = arangoDB.db(experimentConfig.getDbName());

        private ArangoDbAdapterExt arangoDbAdapter = new ArangoDbAdapterExt(arangoDB, arangoDatabase);

        @Autowired
        private ClusterRepository clusterRepository;

        @PostMapping("/aql")
//        @RequestMapping(value = "/aql", method = RequestMethod.POST, consumes = "application/json")
        public DocumentCreateEntity<String> aql() {
                DocumentCreateEntity<String> document = null;
                Map<String, Object> properties = new HashMap<String, Object>();
                try {
                        String queryCmmd = "{\"Select a Compute\":[{\"id\":\"3177\",\"name\":\"Allen3\",\"arr1\":{\"id\":\"41\",\"name\":\"Allen41\",\"arr2\":{\"id\":\"42\",\"name\":\"Allen42\",\"arr3\":{\"id\":\"43\",\"name\":\"Allen43\",\"arr4\":{\"id\":\"4\",\"name\":\"Allen4\",\"arr5\":[{\"id\":\"3\",\"name\":\"Allen3\",\"arr1\":{\"id\":\"41\",\"name\":\"Allen41\",\"arr2\":{\"id\":\"42\",\"name\":\"Allen42\",\"arr3\":{\"id\":\"43\",\"name\":\"Allen43\",\"arr4\":{\"id\":\"4\",\"name\":\"Allen4\",\"arr5\":[\"a\",\"b\",\"v\",\"v2\"]}}}}},{\"id\":\"4\",\"name\":\"Allen4\"}]}}}}},{\"id\":\"4\",\"name\":\"Allen4\"}],\"arr\":[{\"Author\":\"Jones\",\"Categoris\":\"xwlkxkn\",\"Comments\":111,\"Publish Date\":\"1990-08-13 21:46:32\"},{\"Author\":\"Hernandez\",\"Categoris\":\"hiuqdgpbwu\",\"Comments\":186,\"Publish Date\":\"1981-12-20 12:13:18\"}],\"Experiment Name\":\"test1\",\"Data Source \":[{\"id\":\"5\",\"name\":\"Allen5\"},{\"id\":\"6\",\"name\":\"Allen6\"}],\"Select Notebook\":[{\"id\":\"7\",\"name\":\"Allen7\"},{\"id\":\"8\",\"name\":\"Allen86\"}],\"Use first row as header\":false}";

                        document = (DocumentCreateEntity<String>) arangoDatabase.collection(experimentConfig.getCollectionName()).insertDocument(queryCmmd);

                        for (Map.Entry<String, Object> entry : properties.entrySet()) {
                                System.out.println("Attribute " + entry.getKey() + ": " + entry.getValue());
                        }
                } catch (Exception ex) {
                        System.out.println("arangoDbAdapter Exception");
                } finally {
                }
                return document;

        }
        @GetMapping("/aql2")
        public void aql2() {

                String queryCmmd = "#{tenantProvider.getId()}";

              System.out.println(queryCmmd);

        }
        @PostMapping("/init")
        public void init() {

                try {
                        arangoDbAdapter = new ArangoDbAdapterExt();
                        arangoDbAdapter.Init(experimentConfig.getHost().toString(), Integer.valueOf(experimentConfig.getPort()));

                } catch (Exception ex) {
                        System.out.println("arangoDbAdapter Exception");
                } finally {
                }

        }

        @PostMapping("/database")
        public boolean createDatabase(@PathVariable(value = "dbName") String dbName) {
                boolean successful = arangoDbAdapter.createDatabase(dbName);
                return successful;
        }

        /**
         * create Collection
         */
//        @ApiOperation(value = "创建collection", notes = "根据collectionName创建collection")
//        @ApiImplicitParam(name = "collectionName", value = "String", required = true, dataType = "String")
        @PostMapping(value = "/collection/{collectionName}")
        public boolean createCollection(@PathVariable(value = "collectionName") String collectionName) {
                CollectionEntity myArangoCollection = null;
                boolean successful = false;
                try {
                        myArangoCollection = arangoDbAdapter.createCollection(collectionName);
                        System.out.println("Collection created: " + myArangoCollection.getName());
                        successful = true;
                } catch (ArangoDBException e) {
                        System.err.println("Failed to create collection: " + collectionName + "; " + e.getMessage());
                }
                return successful;
        }

        /**
         * DELETE database
         */

        @RequestMapping(value = "/database/{dbName}", method = RequestMethod.DELETE, consumes = "application/json")
        public boolean dropDatabase(@PathVariable(value = "dbName") String dbName) {
                boolean successful = false;
                try {
                        successful = arangoDbAdapter.dropDatabase(dbName);
                } catch (ArangoDBException e) {
                        System.err.println("Failed to drop collection. " + e.getMessage());
                }
                return successful;
        }

        /**
         * DELETE Collection
         */
        @ApiOperation(value = "删除collection", notes = "根据collectionName删除collection")
        @ApiImplicitParam(name = "collectionName", value = "String", required = true, dataType = "String")
        @RequestMapping(value = "/collection/{collectionName}", method = RequestMethod.DELETE, consumes = "application/json")
        public boolean dropCollection(@PathVariable(value = "collectionName") String collectionName) {
                boolean successful = false;
                try {
                        arangoDbAdapter.dropCollection(collectionName);
                        successful = true;
                } catch (ArangoDBException e) {
                        System.err.println("Failed to drop collection. " + e.getMessage());
                }
                return successful;
        }
        /**
         * 查询collection中所有document
         *
         * 配合cluster,datasourc,notebook使用
         * @param collectionName
         * @return
         * @Auther yangsen
         */
        @GetMapping("/document/{collectionName}")
        public List<HashMap<String, Object>> getDocument(@PathVariable(value = "collectionName") String collectionName) {
                ArangoCursor<BaseDocument> document = null;
                List<HashMap<String, Object>> propertiesList = new ArrayList<HashMap<String, Object>>();
                String str=null;
                String aql="FOR C IN "+collectionName+"  RETURN C ";
                try {
                        document =     (ArangoCursor<BaseDocument>)arangoDatabase.query(aql,BaseDocument.class);
                        while (document.hasNext()) {
                                BaseDocument object = document.next();
                                String key =object.getKey();
//                                String type =object.getAttribute("type")==null?"": object.getAttribute("type").toString();
                                String name = object.getAttribute("name")==null?"": object.getAttribute("name").toString();
                                HashMap<String, Object> properties = new HashMap<String, Object>();
                                properties.put("key",key);
//                                properties.put("type",type);
                                properties.put("name",name);
                                propertiesList.add(properties);
                        }
                } catch (ArangoDBException e) {
                        System.err.println("Failed to get document; " + e.getMessage());
                }

                return  propertiesList;
        }
        /**
         * READ  Document
         * 查询collection中指定document
         * @param key
         * @return
         */
        @GetMapping("/document/{collectionName}/{key}")
        public BaseDocument getDocument(@PathVariable(value = "collectionName") String collectionName,@PathVariable(value = "key") String key) {
                BaseDocument document = null;
                Map<String, Object> properties = new HashMap<String, Object>();
                try {
                        document = arangoDbAdapter.getDocument(collectionName, key);
                        System.out.println("Key: " + document.getKey());
                        properties = document.getProperties();
                        for (Map.Entry<String, Object> entry : properties.entrySet()) {
                                System.out.println("Attribute " + entry.getKey() + ": " + entry.getValue());
                        }
                } catch (ArangoDBException e) {
                        System.err.println("Failed to get document: myKey; " + e.getMessage());
                }
                return document;
        }
        /**
         * CREATE Document
         * 插入数据
         *
         * @param { "id": "888",
         *          "revision": "2",
         *          "key": "999",
         *          "properties":
         *          {
         *          "name": "jack8",
         *          "age": 28
         *          }
         *          }
         * @return
         */
        @RequestMapping(value = "/document/{collectionName}", method = RequestMethod.POST, consumes = "application/json")
        public String insertDocument(@PathVariable(value = "collectionName") String collectionName,@RequestBody BaseDocument myObject) {

                String msg = "";
                try {
                        arangoDbAdapter.insertDocument(collectionName, myObject);

                        System.out.println("Document created:" + myObject.getKey() + myObject.getProperties());
                        msg = "Document created" + myObject.getKey() + myObject.getProperties();
                } catch (ArangoDBException e) {
                        System.err.println("Failed to create document. " + e.getMessage());
                        msg = "Failed to create document. " + e.getMessage();
                }
                return msg;
        }

        /**
         * 更新
         * UPDATE
         *
         * @param key
         * @param myObject
         * @return
         */
        @ApiOperation(value = "更新Document详细信息", notes = "根据Document的key来指定更新对象，并根据传过来的Document信息来更新详细信息")
        @ApiImplicitParams({
                @ApiImplicitParam(name = "key", value = "document key", required = true, dataType = "String"),
                @ApiImplicitParam(name = "myObject", value = "详细实体BaseDocument", required = true, paramType = "body", dataType = "BaseDocument")//«Map«String, Object»»
        })
        @RequestMapping(value = "/document/{key}", method = RequestMethod.PUT, consumes = "application/json")
        public boolean updateDocument(@PathVariable(value = "key") String key, @Valid @RequestBody BaseDocument myObject) {
                boolean successful = false;
//                BaseDocument myObject = new BaseDocument();
//                myObject.addAttribute("job", "Bartender");
//                Map<String, Object> properties1=new HashMap<String, Object>();
//                properties1.put("job3", "Bartend3er");
//                myObject.setProperties(properties1);
                try {
                        arangoDbAdapter.updateDocument(experimentConfig.getCollectionName(), key, myObject);
                        successful = true;
                } catch (ArangoDBException e) {
                        System.err.println("Failed to update document. " + e.getMessage());
                }
                return successful;
        }


        /**
         * DELETE Document
         */
        @RequestMapping(value = "/document/{key}", method = RequestMethod.DELETE, consumes = "application/json")
        public boolean deleteDocument(@PathVariable(value = "key") String key) {
                boolean successful = false;
                try {
                        arangoDbAdapter.deleteDocument(experimentConfig.getCollectionName(), key);
                        successful = true;
                } catch (ArangoDBException e) {
                        System.err.println("Failed to delete document. " + e.getMessage());
                }
                return successful;
        }

        @GetMapping("/document/{key}")
        public String getVelocyDocumentTest(@PathVariable(value = "key") String key) {
                VPackSlice document = null;
                String properties = new String();
                try {
                        document = arangoDbAdapter.getVelocyDocument(experimentConfig.getCollectionName(), key);
//                        properties = document.getAsString();

                        System.out.println("Key: " + document.get("_key").getAsString());
                        System.out.println("Attribute a: " + document.get("tenantId").getAsString());
//                        System.out.println("Attribute a: " +document.get("status")==null?"": document.get("status").getAsString());
//                        System.out.println("1: " +document.getAsStringSlice()==null?"":document.getAsStringSlice().toString());
//                        System.out.println("Attribute b: " + document.get("age").getAsInt());
                        properties="Key: " +document.get("_key").getAsString()+"Attribute a: " +document.get("tenantId").getAsString();
                } catch (ArangoDBException | VPackException e) {
                        System.err.println("Failed to get document: myKey; " + e.getMessage());
                }
                return properties;
        }
}
