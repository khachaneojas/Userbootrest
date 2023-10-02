package com.user.test.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.user.test.enums.Authority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "authority")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityModel {
	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "authorityid")
//	private Long authorityID;
	
	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private Authority authority;
	
	@ManyToMany(mappedBy = "authorities", fetch = FetchType.LAZY)
	private Set<UserModel> users = new HashSet<>();
}
