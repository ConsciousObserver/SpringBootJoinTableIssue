package com.test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class TestJoinTableApplication {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public static void main(String[] args) {
		SpringApplication.run(TestJoinTableApplication.class, args);
	}
	
	@PostConstruct
	public void init () {
		Role role1 = new Role();
		Role role2 = new Role();
		
		roleRepo.save(role1);
		roleRepo.save(role2);
		
		User user = new User();
		user.setRoles(new HashSet<>());
		
		user.getRoles().add(role1);
		user.getRoles().add(role2);
		
		userRepo.save(user);
		
		printJson(roleRepo.findAll());
		printJson(userRepo.findAll());
		printJson(jdbcTemplate.queryForList("select * from user_role_mapping"));
	}
	
	public void printJson(Object obj) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}

@Entity
@SequenceGenerator(name="USER_SEQ")
class User {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="USER_SEQ")
	private long userId;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="UserRoleMapping", joinColumns=@JoinColumn(name="userId"), inverseJoinColumns=@JoinColumn(name="roleId"))
	private Set<Role> roles;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
}

@Entity
@SequenceGenerator(name="ROLE_SEQ")
class Role {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ROLE_SEQ")
	private long roleId;

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
}

@Entity
@SequenceGenerator(name="USER_ROLE_SEQ")
class UserRoleMapping {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="USER_ROLE_SEQ")
	private long userRoleMappingId;
	
	@OneToOne
	@JoinColumn(name="userId")
	private User user;
	
	@OneToOne
	@JoinColumn(name="roleId")
	private Role role;

	private Date createdDate;
	
	public long getUserRoleMappingId() {
		return userRoleMappingId;
	}
	public void setUserRoleMappingId(long userRoleMappingId) {
		this.userRoleMappingId = userRoleMappingId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}

interface UserRepository extends JpaRepository<User, Long> {
	
}

interface RoleRepository extends JpaRepository<Role, Long> {
	
}