package com.bosanskilonac.szak.model;

import java.sql.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Karta {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@NotNull
	@Column(name="korisnik_id")
	private Long korisnikId;
	@NotNull
	@Column(name="let_id")
	private Long letId;
	@NotNull
	@Column(name="datum_kupovine")
	private Date datumKupovine;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getKorisnikId() {
		return korisnikId;
	}
	public void setKorisnikId(Long korisnikId) {
		this.korisnikId = korisnikId;
	}
	public Long getLetId() {
		return letId;
	}
	public void setLetId(Long letId) {
		this.letId = letId;
	}
	public Date getDatumKupovine() {
		return datumKupovine;
	}
	public void setDatumKupovine(Date datumKupovine) {
		this.datumKupovine = datumKupovine;
	}
	
}
