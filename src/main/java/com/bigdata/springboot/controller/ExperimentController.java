package com.bigdata.springboot.controller;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.bigdata.springboot.arangodbcrud.ArangoDbAdapterExt;
import com.bigdata.springboot.config.ExperimentConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
  * ExperimentController
  *
  * @Author: yangsen
  * @Date: 2020/4/20 11:43
 */
@RestController
@RequestMapping("/api1/{tenantId}")
public class ExperimentController {

        @Autowired
        public ExperimentConfig experimentConfig;

        //配置文件获取host
        private ArangoDB arangoDB = new ArangoDB.Builder().host(experimentConfig.getHost(), experimentConfig.getPort()).build();

        private ArangoDatabase arangoDatabase = arangoDB.db(experimentConfig.getDbName());
        //构造ArangoDbAdapterExt，注入arangoDB，arangoDatabase
        private ArangoDbAdapterExt arangoDbAdapter = new ArangoDbAdapterExt(arangoDB, arangoDatabase);



        /**
          * 功能描述:Computer Cluster 名称列表查询
          * @param tenantId
          * @return: java.util.Map<java.lang.String,java.lang.Object>
          * @Author: yangsen
          * @Date: 2020/4/27 15:44
         */
        @GetMapping("/computer/queryComputers")
        public Map<String, Object> queryComputers(@PathVariable(value="tenantId") String tenantId) throws SQLException {

                //加入多租户筛选
                String aql = getAqlString(tenantId, experimentConfig.getComputerClusterName());

                return getDocMaps(aql, tenantId);
        }



        /**
          * 功能描述:DataSource 列表查询
          * @param tenantId
          * @return: java.util.Map<java.lang.String,java.lang.Object>
          * @Author: yangsen
          * @Date: 2020/4/27 15:43
         */
        @GetMapping("/dataApplicance/listDataSource")
        public Map<String, Object> listDataSource(@PathVariable(value="tenantId") String tenantId) throws SQLException {

                //加入多租户筛选
                String aql = getAqlString(tenantId, experimentConfig.getDsCollectionName());

                return getDocMaps(aql, tenantId);
        }



        /**
          * 功能描述:查看notebook详细信息
          * @param tenantId
          * @return: java.util.Map<java.lang.String,java.lang.Object>
          * @Author: yangsen
          * @Date: 2020/4/27 15:44
         */
        @GetMapping("/notebook/viewNotebook")
        public Map<String, Object> viewNotebook(@PathVariable(value="tenantId") String tenantId) throws SQLException {

                //加入多租户筛选
                String aql = getAqlString(tenantId, experimentConfig.getNoteBookCllectionName());

                return getDocMaps(aql, tenantId);
        }

       /**
        * 功能描述:保存experiment
        * @param myObject
        * @param tenantId
        * @return: java.util.Map<java.lang.String,java.lang.Object>
        * @Author: yangsen
        * @Date: 2020/4/27 15:45
        */
        @RequestMapping(value = "/experiment/saveExperiment", method = RequestMethod.POST, consumes = "application/json")
        public Map<String, Object> saveExperiment(@RequestBody BaseDocument myObject,@PathVariable(value="tenantId") String tenantId) {
                Map<String, Object> result = new HashMap<String, Object>();
                String msg = "";
                try {
                        Date d = new Date();
                        //写入当前时间，格式"yyyy-MM-dd kk:mm:ss"
                        myObject.addAttribute("createTime",getTimeSec(d));
                        //加入多租户tenantId
                        myObject.addAttribute("tenantId",tenantId);
                        //是否已删除标志字段，删除的数据可以将dr更新为1表示已删除，但查询时需要配合FILTER dr=0
                        myObject.addAttribute("dr",0);
                        arangoDbAdapter.insertDocument(experimentConfig.getCollectionName(), myObject);

                        System.out.println("Document created:" + myObject.getKey() + myObject.getProperties());
                        msg = "Document created key:" + myObject.getKey() + " properties:" + myObject.getProperties();
                        result.put("status", "success");
                        result.put("reason", msg);
                } catch (ArangoDBException e) {
                        System.err.println("Failed to create document. " + e.getMessage());
                        msg = "Failed to create document. " + e.getMessage();
                        result.put("status", "failed");
                        result.put("reason", msg);
                }
                return result;
        }



        /**
         * 功能描述:根据时间筛选 Experiment列表
         * @param lastDays
         * @param tenantId
         * @return: java.util.Map<java.lang.String,java.lang.Object>
         * @Author: yangsen
         * @Date: 2020/4/27 15:46
         */
        @GetMapping("/experiment/queryExperimentsByDate/{lastDays}")
        public Map<String, Object> queryExperimentsByDate(@PathVariable(value = "lastDays") int lastDays,@PathVariable(value="tenantId") String tenantId) throws SQLException {
                ArangoCursor<BaseDocument> document = null;
                Map<String, Object> result = new HashMap<String, Object>();
                List<Map<String, Object>> propertiesList = new ArrayList<Map<String, Object>>();
                String str = null;
                Date d = new Date();
                //转换格式+计算提前的日期
                String datatimeBefore = getTime(getDay(d, lastDays));
                String datatimeNow = getTime(getDay(d, -1));
                //加入多租户筛选
                String filterAql= tenantId==null?"":" C.tenantId=='"+tenantId+"'&&";
                String aql = "FOR C IN " + experimentConfig.getCollectionName() + " FILTER "+filterAql+"C.createTime!=null&&C.createTime>='" + datatimeBefore + "'&&C.createTime<='" + datatimeNow + "' RETURN C ";
                Map<String,Object> listBody = new HashMap<String,Object>();
                try {
                        document = (ArangoCursor<BaseDocument>) arangoDbAdapter.executeQuery(aql);
                        //ArangoCursor需遍历
                        while (document.hasNext()) {
                                BaseDocument object = document.next();
                                String key = object.getKey();
                                //暂时展示所有字段，供前端选择使用
//                                String dataSource = object.getAttribute("dataSource") == null ? "" : object.getAttribute("dataSource").toString();
//                                String name = object.getAttribute("experimentName") == null ? "" : object.getAttribute("experimentName").toString();
                                Map<String, Object> properties =object.getProperties();
                                properties.put("id", key);
                                propertiesList.add(properties);
                        }
                        listBody.put("data|"+String.valueOf(lastDays),propertiesList);
                        listBody.put("tenantId",tenantId);
                        result.put("status", "success");
                        result.put("reason", "success");
                        result.put("LIST_body", listBody);
                } catch (ArangoDBException e) {
                        result.put("status", "failed");
                        result.put("reason", e.getMessage().toString());
                        System.err.println("Failed to get document; " + e.getMessage());
                }

                return result;
        }


        /**
         * 功能描述:组装AQL公共方法
         * @param tenantId
         * @param collectionName
         * @return: java.lang.String
         * @Author: EDZ
         * @Date: 2020/4/27 17:13
         */

        private String getAqlString(@PathVariable("tenantId") String tenantId, String collectionName) {
                String filterAql = tenantId == null ? "" : " FILTER C.tenantId=='" + tenantId + "'";
                return "FOR C IN " + collectionName + filterAql + "  RETURN C ";
        }
        private Map<String, Object> getDocMaps(String aql,@PathVariable("tenantId") String tenantId) {
                ArangoCursor<BaseDocument> document = null;
                Map<String, Object> result = new HashMap<String, Object>();
                List<HashMap<String, Object>> propertiesList = new ArrayList<HashMap<String, Object>>();
                Map<String,Object> listBody = new HashMap<String,Object>();
                try {
                        document = (ArangoCursor<BaseDocument>)arangoDbAdapter.executeQuery(aql);
                        while (document.hasNext()) {
                                BaseDocument object = document.next();
                                String key = object.getKey();
                                String name = object.getAttribute("name").toString();
                                HashMap<String, Object> properties = new HashMap<String, Object>();
                                properties.put("id", key);
                                properties.put("name", name);
                                propertiesList.add(properties);
                        }
                        listBody.put("data",propertiesList);
                        listBody.put("tenantId",tenantId);
                        result.put("status", "success");
                        result.put("reason", "success");
                        result.put("LIST_body", listBody);
                } catch (ArangoDBException e) {
                        result.put("status", "failed");
                        result.put("reason", e.getMessage().toString());
                        System.err.println("Failed to get document; " + e.getMessage());
                }

                return result;
        }
        /**
         * 转换指定日期
         * @author yangsen
         */
        public String getTime(Date d) {
                System.out.println("获取时间" + new Date());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                System.out.println("格式化输出：" + sdf.format(d));

                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                System.out.println("Asia/Shanghai:" + sdf.format(d));

                return sdf.format(d);
        }
        /**
         * 转换指定日期--时分秒
         * @author yangsen
         */
        public String getTimeSec(Date d) {
                System.out.println("获取时间" + new Date());


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                System.out.println("格式化输出：" + sdf.format(d));

                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                System.out.println("Asia/Shanghai:" + sdf.format(d));

                return sdf.format(d);
        }

        /**
         * 获取指定日期前后num天的日期
         *
         * @param date
         * @param num   负数多少天之后的日期   正数 多少天之后的日期
         * @return
         * @author yangsen
         */
        public static Date getDay(Date date, int num) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - num);
                return calendar.getTime();
        }




}
