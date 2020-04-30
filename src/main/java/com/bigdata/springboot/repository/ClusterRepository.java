package com.bigdata.springboot.repository;

import com.arangodb.ArangoCursor;

import com.arangodb.entity.BaseDocument;
import com.arangodb.springframework.annotation.BindVars;
import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.annotation.QueryOptions;
import com.arangodb.springframework.repository.ArangoRepository;
import com.bigdata.springboot.entity.Cluster;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ClusterRepository extends ArangoRepository<Cluster, String> {



    /**
     * 查找所有
     *
     * @return
     */
//    @Query("FOR c IN @collectionName  RETURN c")
//    Iterable<Cluster> getAllDoc(@Param("collectionName") String collectionName);Cluster
    @Query("FOR c IN @Characters  RETURN c")
    Iterable<BaseDocument> getAllDoc();

    @Query("FOR c IN characters FILTER c.surname == surname SORT c.age ASC RETURN c")
    Iterable<Character> getWithSurname(@Param("surname") String value);

    @Query("FOR c IN @@col FILTER c.surname == @surname AND c.age > @age RETURN c")
    @QueryOptions(count = true)
    ArangoCursor<Character> getWithSurnameOlderThan(@Param("age") int value, @BindVars Map<String, Object> bindvars);

    /**
     * 关于 INBOUND、OUTBOUND、ANY 飞区别，可以查看文章 https://blog.csdn.net/yuzongtao/article/details/76061897
     * @param id
     * @param edgeCollection
     * @return
     */
    @Query("FOR v IN 1..2 INBOUND @id @@edgeCol SORT v.age DESC RETURN DISTINCT v")
    Set<Character> getAllChildsAndGrandchilds(@Param("id") String id, @Param("@edgeCol") Class<?> edgeCollection);

}
