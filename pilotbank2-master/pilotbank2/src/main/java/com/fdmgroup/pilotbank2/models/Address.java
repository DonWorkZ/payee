package com.fdmgroup.pilotbank2.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Address")
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "add_id_sequence")
	@SequenceGenerator(name = "add_id_sequence", sequenceName = "address_pk_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "ADDRESS_ID", nullable = false)
	private Long addressId;

	@Column(name = "STREET_NAME", nullable = false)
	private String streetName;

	@Column(name = "STREET_NUMBER", nullable = false)
	private String streetNumber;

	@Column(name = "SUITE_NUMBER")
	private String suiteNumber;

	@Column(nullable = false)
	private String province;

	@Column(nullable = false)
	private String city;

	@Column(nullable = false)
	private String postalCode;

	@ManyToOne
	@JoinColumn(name = "USR_ID")
	@JsonBackReference(value = "user-to-address")
	private User user;

	@Override
	public String toString() {
		return "Address [addressId=" + addressId + ", streetName=" + streetName + ", streetNumber=" + streetNumber
				+ ", suiteNumber=" + suiteNumber + ", province=" + province + ", city=" + city + ", postalCode="
				+ postalCode + "]";
	}

}
