package net.app;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {
	private Long id;
	private String username;
	private String password;
	private String name;
	private Date birth;

	protected User() {
	}

	protected User(Long id, String username, String password, String name, Date birth) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.name = name;
		this.birth = birth;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	
	public String getUsername() {
		return username;
	}

	public void setUsername(String name) {
		this.username = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String name) {
		this.password = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date name) {
		this.birth = name;
	}

}
