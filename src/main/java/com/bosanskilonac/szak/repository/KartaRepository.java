package com.bosanskilonac.szak.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bosanskilonac.szak.model.Karta;

public interface KartaRepository extends JpaRepository<Karta, Long> {
	long countByLetId(Long letId);
	Page<Karta> findByKorisnikId(Long korisnikId, Pageable pageable);
	void deleteByLetId(Long letId);
}
