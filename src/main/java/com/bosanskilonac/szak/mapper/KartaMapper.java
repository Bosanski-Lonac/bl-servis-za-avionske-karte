package com.bosanskilonac.szak.mapper;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bosanskilonac.szak.model.Karta;

import dto.KartaReserveDto;
import dto.LetDto;
import dto.PovracajNovcaDto;
import dto.RezervacijeKorisnikaDto;
import dto.KartaCreateDto;
import dto.KartaDto;
import dto.KartaKSDto;

@Component
public class KartaMapper {
	public KartaDto kartaToKartaDto(Karta karta) {
		KartaDto kartaDto = new KartaDto();
		kartaDto.setId(karta.getId());
		kartaDto.setLetId(karta.getLetId());
		kartaDto.setDatumKupovine(karta.getDatumKupovine());
		kartaDto.setCena(karta.getCena());
		return kartaDto;
	}
	
	public KartaKSDto kartaReserveDtoToKartaKSDto(KartaReserveDto kartaReserveDto, LetDto letDto) {
		KartaKSDto kartaKSDto = new KartaKSDto();
		kartaKSDto.setKreditnaKarticaId(kartaReserveDto.getKreditnaKarticaId());
		kartaKSDto.setMilje(letDto.getMilje() * kartaReserveDto.getKolicina());
		kartaKSDto.setCena(letDto.getCena().multiply(new BigDecimal(kartaReserveDto.getKolicina())));
		return kartaKSDto;
	}
	
	public List<Karta> kartaCreateDtoToKarta(KartaCreateDto kartaCreateDto, LetDto letDto, int kolicina) {
		List<Karta> karte = new ArrayList<>();
		for(int i = 0; i < kolicina; i++) {
			Karta karta = new Karta();
			karta.setKorisnikId(kartaCreateDto.getKorisnikId());
			karta.setLetId(letDto.getId());
			karta.setDatumKupovine(new Date(System.currentTimeMillis()));
			karta.setCena(kartaCreateDto.getCena().divide(new BigDecimal(kolicina)));
			karte.add(karta);
		}
		return karte;
	}
	
	public PovracajNovcaDto karteToPNDto(List<Karta> karte, LetDto letDto) {
		PovracajNovcaDto povracajNovcaDto = new PovracajNovcaDto();
		povracajNovcaDto.setListaKorisnikCena(new HashMap<>());
		for(Karta karta : karte) {
			RezervacijeKorisnikaDto rezervacijeKorisnikaDto = povracajNovcaDto.getListaKorisnikCena().get(karta.getKorisnikId());
			if(rezervacijeKorisnikaDto == null) {
				rezervacijeKorisnikaDto = new RezervacijeKorisnikaDto();
				rezervacijeKorisnikaDto.setBrojRezervacija(1);
				rezervacijeKorisnikaDto.setCena(karta.getCena());
				povracajNovcaDto.getListaKorisnikCena().put(karta.getKorisnikId(), rezervacijeKorisnikaDto);
			}
			else {
				rezervacijeKorisnikaDto.dodajRezervaciju(karta.getCena());
			}
		}
		povracajNovcaDto.setLetDto(letDto);
		return povracajNovcaDto;
	}
}
