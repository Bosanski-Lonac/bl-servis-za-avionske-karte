package com.bosanskilonac.szak.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;

import dto.KartaCUDto;
import dto.KartaDto;

public interface KartaService {
	KartaDto reserve(Long korisnikId, KartaCUDto kartaCreateDto);
	Long countByLetId(Long letId) throws EmptyResultDataAccessException;
	Page<KartaDto> findByKorisnikId(Long korisnikId, Integer brojStranice);
	void deleteById(Long id) throws EmptyResultDataAccessException;
	void deleteByLetId(Long letId) throws EmptyResultDataAccessException;
}
