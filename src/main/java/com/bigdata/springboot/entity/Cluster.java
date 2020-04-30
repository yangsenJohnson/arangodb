package com.bigdata.springboot.entity;

import java.util.Collection;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.HashIndex;
import com.arangodb.springframework.annotation.Relations;

/**
 * @author Mark Vollmary
 *
 */
//@Document("Cluster")
@Document("#{tenantProvider.getId()}_Cluster")
public class Cluster {

	@Id
	private String id;

	private String name;




	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return "Cluster [id=" + id + ", name=" + name +  "]";
	}

}
