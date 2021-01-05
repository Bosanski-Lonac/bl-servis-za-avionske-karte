package com.bosanskilonac.szak.controller;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bosanskilonac.szak.service.KartaService;

import dto.KartaReserveDto;
import dto.ListaLetovaDto;
import dto.RezervacijeLetovaDto;
import dto.KartaDto;
import enums.Role;
import io.swagger.annotations.ApiOperation;
import security.CheckSecurity;

@RestController
@RequestMapping("/karta")
public class KartaController {
	private KartaService kartaService;
	
	public KartaController(KartaService kartaService) {
		this.kartaService = kartaService;
	}

	@ApiOperation(value = "Rezervisanje karte")
	@PostMapping("/{id}")
	@CheckSecurity(roles = {Role.ROLE_USER})
	public ResponseEntity<KartaDto> reserve(@RequestHeader("Authorization") String authorization, @PathVariable("id") Long id,
			@RequestBody @Valid KartaReserveDto kartaReserveDto) {
		return new ResponseEntity<>(kartaService.reserve(id, kartaReserveDto), HttpStatus.CREATED);
	}
	
	@ApiOperation(value = "Vracanje broja rezervisanih mesta za letove")
	@PostMapping
	@CheckSecurity(roles = {Role.ROLE_SERVICE}, checkOwnership = false)
	public ResponseEntity<RezervacijeLetovaDto> getReservedSeats(@RequestHeader("Authorization") String authorization,
			@RequestBody @Valid ListaLetovaDto listaLetovaDto) {
		return new ResponseEntity<>(kartaService.countReservations(listaLetovaDto), HttpStatus.OK);
	}
	
	@ApiOperation(value = "Prikaz svih karata za trenutnog korisnika")
	@GetMapping("/{id}")
	@CheckSecurity(roles = {Role.ROLE_USER})
	public ResponseEntity<Page<KartaDto>> getAllKarte(@RequestHeader("Authorization") String authorization, @PathVariable("id") Long id,
			@RequestParam(value = "bstr", required = false, defaultValue="0") Integer brojStranice) {
		return new ResponseEntity<>(kartaService.findByKorisnikId(id, brojStranice), HttpStatus.OK);
	}
	
	@ApiOperation(value = "Brisanje karte")
	@DeleteMapping("/{kartaId}")
	@CheckSecurity(roles = {Role.ROLE_USER}, checkOwnership = false)
	public ResponseEntity<?> delete(@RequestHeader("Authorization") String authorization, @PathVariable("kartaId") Long kartaId) {
		kartaService.deleteById(kartaId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
