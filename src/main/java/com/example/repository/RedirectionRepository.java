package com.example.repository;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.model.Redirection;

@RepositoryRestResource(collectionResourceRel = "redirection", path = "redirection")
public interface RedirectionRepository extends JpaRepository<Redirection, Long> {
	 List<Redirection> findByUserEmail(String email);
	 Redirection findByShortUrl(String shortUrl) throws NoResultException;
	 
	 @Query("select r from Redirection r where r.url =:url and r.userEmail =:userEmail ")
     Redirection findByUserEmailAndUrl(@Param("url") String url, @Param("userEmail") String userEmail);
}
