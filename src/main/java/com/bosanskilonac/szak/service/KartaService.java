package com.bosanskilonac.szak.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;

import dto.KartaCUDto;
import dto.KartaDto;
import exceptions.CustomException;

public interface KartaService {
	KartaDto reserve(Long korisnikId, KartaCUDto kartaCreateDto) throws CustomException;
	Long countByLetId(Long letId) throws EmptyResultDataAccessException;
	Page<KartaDto> findByKorisnikId(Long korisnikId, Integer brojStranice);
	void deleteById(Long id) throws EmptyResultDataAccessException;
	void deleteByLetId(Long letId);
}
