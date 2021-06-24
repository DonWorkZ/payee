package com.fdmgroup.pilotbank2.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;

@SuperBuilder(toBuilder = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ADMIN_TABLE")
public class Admin extends User {
	
}
