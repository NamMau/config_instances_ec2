package org.example.ec2confi.repository;

import org.example.ec2confi.entity.FileMetadata;
import org.example.ec2confi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByOwner(User owner);

    Optional<FileMetadata> findByIdAndOwner(Long id, User owner);
}
