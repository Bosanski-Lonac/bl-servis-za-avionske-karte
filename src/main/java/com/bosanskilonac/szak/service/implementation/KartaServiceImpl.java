package com.bosanskilonac.szak.service.implementation;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.bosanskilonac.szak.mapper.KartaMapper;
import com.bosanskilonac.szak.model.Karta;
import com.bosanskilonac.szak.repository.KartaRepository;
import com.bosanskilonac.szak.service.KartaService;

import dto.KartaCUDto;
import dto.KartaDto;

@Service
public class KartaServiceImpl implements KartaService {
	private final int velicinaStranice = 4;
	
	private KartaRepository kartaRepository;
	private KartaMapper kartaMapper;
	
	public KartaServiceImpl(KartaRepository kartaRepository, KartaMapper kartaMapper) {
		this.kartaRepository = kartaRepository;
		this.kartaMapper = kartaMapper;
	}

	@Override
	public KartaDto reserve(Long korisnikId, KartaCUDto kartaCreateDto) {
		Karta karta = kartaMapper.kartaCreateDtoToKarta(korisnikId, kartaCreateDto);
		karta = kartaRepository.save(karta);
		KartaDto kartaDto = kartaMapper.kartaToKartaDto(karta);
		return kartaDto;
	}
	
	@Override
	public Long countByLetId(Long letId) throws EmptyResultDataAccessException {
		return kartaRepository.countByLetId(letId);
	}

	@Override
	public Page<KartaDto> findByKorisnikId(Long korisnikId, Integer brojStranice) {
		return kartaRepository.findByKorisnikId(korisnikId, PageRequest.of(brojStranice, velicinaStranice))
				.map(kartaMapper::kartaToKartaDto);
	}

	@Override
	public void deleteById(Long id) throws EmptyResultDataAccessException {
		kartaRepository.deleteById(id);
		// javi korisnickom servisu da oduzme milje
	}

	@Override
	public void deleteByLetId(Long letId) throws EmptyResultDataAccessException {
		kartaRepository.deleteByLetId(letId);
		// javi korisnickom servisu da oduzme milje
	}

}
