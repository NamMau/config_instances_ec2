package org.example.ec2confi.repository;

import org.example.ec2confi.entity.FileShare;
import org.example.ec2confi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileShareRepository extends JpaRepository<FileShare, Long> {
    //find share file by user
    List<FileShare> findBySharedWith(User sharedWith);

    // check if share
    boolean existsByFileIdAndSharedWithId(Long fileId, Long userId);
}