package com.bosanskilonac.szak.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;

import dto.KartaReserveDto;
import dto.LetDto;
import dto.ListaLetovaDto;
import dto.RezervacijeLetovaDto;
import dto.KartaDto;
import exceptions.CustomException;

public interface KartaService {
	void reserve(Long korisnikId, KartaReserveDto kartaCreateDto) throws CustomException;
	RezervacijeLetovaDto countReservations(ListaLetovaDto listaLetovaDto);
	Page<KartaDto> findByKorisnikId(Long korisnikId, Integer brojStranice);
	void deleteById(Long id) throws EmptyResultDataAccessException;
	void deleteByLet(LetDto letDto);
}
