package io.services.contactapi.service;

import io.services.contactapi.constant.Constants;
import io.services.contactapi.domain.Contact;
import io.services.contactapi.repository.ContactRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * @author AshwinAnand868
 * @version 1.0
 * @since 01/25/2024 (MM/dd/yyyy)
 * @email anandashwin868@gmail.com
 */

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ContactService {
    private ContactRepository contactRepo;

    public Page<Contact> getAllContacts(int page, int size) {
        return contactRepo.findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    public Contact getContact(String id) {
        return contactRepo.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    public Contact createContact(Contact contact) {
        return contactRepo.save(contact);
    }

    public void deleteContact(Contact contact) {
        contactRepo.deleteById(contact.getId());
    }

    public String uploadPhoto(String id, MultipartFile file) {
        Contact contact = getContact(id);
        String photoUrl = photoFunction.apply(id, file);
        contact.setPhotoUrl(photoUrl);
        contactRepo.save(contact);
        return photoUrl;
    }

    private final Function<String, String> fileExtension = fileName -> Optional.of(fileName).filter(name -> name.contains("."))
            .map(name -> "." + name.substring(fileName.lastIndexOf(".") + 1)).orElse(".png");

    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        String fileName = id + fileExtension.apply(image.getOriginalFilename());

        try {
            Path fileStorageLocation = Paths.get(Constants.PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if(!Files.exists(fileStorageLocation)) { Files.createDirectories(fileStorageLocation); }
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(fileName), REPLACE_EXISTING);
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/contacts/image/" + fileName).toUriString();
        } catch(IOException ioException) {
            throw new RuntimeException("Unable to save image");
        }
    };
}
