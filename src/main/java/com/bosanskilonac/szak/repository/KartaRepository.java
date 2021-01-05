package com.bosanskilonac.szak.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bosanskilonac.szak.model.Karta;

public interface KartaRepository extends JpaRepository<Karta, Long> {
	int countByLetId(Long letId);
	Page<Karta> findByKorisnikId(Long korisnikId, Pageable pageable);
	List<Karta> findByLetId(Long letId);
	void deleteByLetId(Long letId);
}
