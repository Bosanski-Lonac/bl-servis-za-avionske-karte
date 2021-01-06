package com.bosanskilonac.szak.service.implementation;

import java.util.Collections;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.bosanskilonac.szak.mapper.KartaMapper;
import com.bosanskilonac.szak.model.Karta;
import com.bosanskilonac.szak.repository.KartaRepository;
import com.bosanskilonac.szak.service.KartaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dto.KartaReserveDto;
import dto.KartaCreateDto;
import dto.KartaDto;
import dto.KartaKSDto;
import dto.LetDto;
import dto.ListaLetovaDto;
import dto.PovracajNovcaDto;
import dto.RezervacijeLetovaDto;
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
	private JmsTemplate jmsTemplate;
	private ObjectMapper objectMapper;

	public KartaServiceImpl(KartaRepository kartaRepository,
			KartaMapper kartaMapper,
			RestTemplate serviceCommunicationRestTemplate,
			JmsTemplate jmsTemplate,
			ObjectMapper objectMapper) {
		this.kartaRepository = kartaRepository;
		this.kartaMapper = kartaMapper;
		this.serviceCommunicationRestTemplate = serviceCommunicationRestTemplate;
		this.jmsTemplate = jmsTemplate;
		this.objectMapper = objectMapper;
	}

	@Override
	public void reserve(Long korisnikId, KartaReserveDto kartaReserveDto) throws CustomException {
		// get flight from flight service
		LetDto letDto = null;
		try {
			ResponseEntity<LetDto> letDtoResponseEntity = serviceCommunicationRestTemplate.exchange(BLURL.getLetURL(kartaReserveDto.getLetId()), 
					HttpMethod.GET, null, LetDto.class);
			letDto = letDtoResponseEntity.getBody();
		} catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException("Let nije nađen.");
        }
		if(letDto.getKapacitet() <= kartaRepository.countByLetId(letDto.getId()) + kartaReserveDto.getKolicina()) {
			throw new InUseException("Nema dovoljno mesta na letu.");
		}
		
		KartaKSDto kartaKSDto = kartaMapper.kartaReserveDtoToKartaKSDto(kartaReserveDto, letDto);
		KartaCreateDto kartaCreateDto = null;
		// get discount, process payment and add miles to user
		HttpEntity<KartaKSDto> request = new HttpEntity<>(kartaKSDto);
		try {
			ResponseEntity<KartaCreateDto> kartaResponseEntity = serviceCommunicationRestTemplate.exchange(BLURL.getKSReserveURL(korisnikId), 
					HttpMethod.POST, request, KartaCreateDto.class);
			kartaCreateDto = kartaResponseEntity.getBody();
		} catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException("Kreditna kartica nije nađena.");
        }
		List<Karta> karte = kartaMapper.kartaCreateDtoToKarta(kartaCreateDto,
				letDto, kartaReserveDto.getKolicina());
		karte = kartaRepository.saveAll(karte);
	}
	
	@Override
	public RezervacijeLetovaDto countReservations(ListaLetovaDto listaLetovaDto) {
		RezervacijeLetovaDto rezervacijeLetovaDto = new RezervacijeLetovaDto();
		for(Long letId : listaLetovaDto.getLetovi()) {
			int rezervacije = kartaRepository.countByLetId(letId);
			rezervacijeLetovaDto.getListaLetRezervacije().put(letId, rezervacije);
		}
		return rezervacijeLetovaDto;
	}

	@Override
	public Page<KartaDto> findByKorisnikId(Long korisnikId, Integer brojStranice) {
		return kartaRepository.findByKorisnikId(korisnikId, PageRequest.of(brojStranice, velicinaStranice))
				.map(kartaMapper::kartaToKartaDto);
	}

	@Override
	public void deleteById(Long id) throws EmptyResultDataAccessException {
		Karta karta = kartaRepository
				.findById(id)
				.orElseThrow(() -> new NotFoundException("Rezervacija ne postoji."));
		// get flight from flight service
		LetDto letDto = null;
		try {
			ResponseEntity<LetDto> letDtoResponseEntity = serviceCommunicationRestTemplate.exchange(BLURL.getLetURL(karta.getLetId()), 
					HttpMethod.GET, null, LetDto.class);
			letDto = letDtoResponseEntity.getBody();
		} catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException("Ovo nije trebalo da se desi.");
        }
		kartaRepository.deleteById(id);
		List<Karta> karte = Collections.singletonList(karta);
		PovracajNovcaDto povracajNovcaDto = kartaMapper.karteToPNDto(karte, letDto);
		// javi korisnickom servisu da oduzme milje
		try {
			jmsTemplate.convertAndSend(BLURL.AMQUEUE_REFUND, objectMapper.writeValueAsString(povracajNovcaDto));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	@Transactional
	public void deleteByLet(LetDto letDto) {
		// Ako ne postoji karata to nije problem
		try {
			List<Karta> karte = kartaRepository.findByLetId(letDto.getId());
			kartaRepository.deleteByLetId(letDto.getId());
			PovracajNovcaDto povracajNovcaDto = kartaMapper.karteToPNDto(karte, letDto);
			// javi korisnickom servisu da oduzme milje
			jmsTemplate.convertAndSend(BLURL.AMQUEUE_REFUND, objectMapper.writeValueAsString(povracajNovcaDto));
		} catch(EmptyResultDataAccessException e) {
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
