package io.services.contactapi.repository;

import io.services.contactapi.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * @author AshwinAnand868
 * @version 1.0
 * @since 01/25/2024 (MM/dd/yyyy)
 * @email anandashwin868@gmail.com
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {
    Optional<Contact> findById(String id);
}
