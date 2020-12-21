package com.bosanskilonac.szak.mapper;

import java.sql.Date;

import org.springframework.stereotype.Component;

import com.bosanskilonac.szak.model.Karta;

import dto.KartaCUDto;
import dto.KartaDto;

@Component
public class KartaMapper {
	public KartaDto kartaToKartaDto(Karta karta) {
		KartaDto kartaDto = new KartaDto();
		kartaDto.setId(karta.getId());
		kartaDto.setLetId(karta.getLetId());
		kartaDto.setDatumKupovine(karta.getDatumKupovine());
		return kartaDto;
	}
	
	public Karta kartaCreateDtoToKarta(Long id, KartaCUDto kartaCreateDto) {
		Karta karta = new Karta();
		karta.setKorisnikId(id);
		karta.setLetId(kartaCreateDto.getLetId());
		karta.setDatumKupovine(new Date(System.currentTimeMillis()));
		return karta;
	}
}
