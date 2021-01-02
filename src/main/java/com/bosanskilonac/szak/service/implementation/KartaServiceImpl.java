package com.bosanskilonac.szak.service.implementation;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.bosanskilonac.szak.mapper.KartaMapper;
import com.bosanskilonac.szak.model.Karta;
import com.bosanskilonac.szak.repository.KartaRepository;
import com.bosanskilonac.szak.service.KartaService;

import dto.KartaCUDto;
import dto.KartaDto;
import dto.LetDto;
import exceptions.CustomException;
import exceptions.InUseException;
import exceptions.NotFoundException;
import utility.BLURL;

@Service
public class KartaServiceImpl implements KartaService {
	private final int velicinaStranice = 4;
	
	private KartaRepository kartaRepository;
	private KartaMapper kartaMapper;
	private RestTemplate serviceCommunicationRestTemplate;

	public KartaServiceImpl(KartaRepository kartaRepository, KartaMapper kartaMapper,
			RestTemplate serviceCommunicationRestTemplate) {
		this.kartaRepository = kartaRepository;
		this.kartaMapper = kartaMapper;
		this.serviceCommunicationRestTemplate = serviceCommunicationRestTemplate;
	}

	@Override
	public KartaDto reserve(Long korisnikId, KartaCUDto kartaCreateDto) throws CustomException {
		// get flight from flight service
		LetDto letDto = null;
		try {
			ResponseEntity<LetDto> letDtoResponseEntity = serviceCommunicationRestTemplate.exchange(BLURL.SZL_URL
					+ BLURL.LET_URL + "/" + kartaCreateDto.getLetId(), HttpMethod.GET, null, LetDto.class);
			letDto = letDtoResponseEntity.getBody();
		} catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException("Let nije nađen.");
        }
		if(letDto.getKapacitet() <= kartaRepository.countByLetId(letDto.getId())) {
			throw new InUseException("Nema dovoljno mesta na letu.");
		}
		kartaCreateDto.setMilje(letDto.getMilje());
		// get discount, process payment and add miles to user
		kartaCreateDto.setCena(letDto.getCena());
		HttpEntity<KartaCUDto> request = new HttpEntity<>(kartaCreateDto);
		try {
			ResponseEntity<KartaCUDto> kartaResponseEntity = serviceCommunicationRestTemplate.exchange(BLURL.KS_URL
					+ BLURL.KORISNIK_URL + "/" + korisnikId.toString() + BLURL.CC_URL + BLURL.RESERVE_URL, HttpMethod.POST, request, KartaCUDto.class);
			kartaCreateDto = kartaResponseEntity.getBody();
		} catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException("Kreditna kartica nije nađena.");
        }
		Karta karta = kartaMapper.kartaCreateDtoToKarta(kartaCreateDto);
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
	public void deleteByLetId(Long letId) {
		// Ako ne postoji karata to nije problem
		try {
			kartaRepository.deleteByLetId(letId);
		} catch(EmptyResultDataAccessException e) {
			
		}
		// javi korisnickom servisu da oduzme milje
	}

}
