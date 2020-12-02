package model;

import java.time.Instant;

import javax.persistence.*;

public class Karta {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@Column(name="datum_kupovine")
	private Instant datumKupovine;
	
	public Karta(Instant datumKupovine) {
		super();
		this.datumKupovine = datumKupovine;
	}

	public Instant getDatumKupovine() {
		return datumKupovine;
	}

	public void setDatumKupovine(Instant datumKupovine) {
		this.datumKupovine = datumKupovine;
	}
}
