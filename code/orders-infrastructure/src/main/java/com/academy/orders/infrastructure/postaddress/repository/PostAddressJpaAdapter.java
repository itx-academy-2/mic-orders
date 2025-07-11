package com.academy.orders.infrastructure.postaddress.repository;

import com.academy.orders.infrastructure.postaddress.entity.PostAddressV2Entity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostAddressJpaAdapter extends CrudRepository<PostAddressV2Entity, UUID> {
    Optional<PostAddressV2Entity> findByTitleAndAccount_Id(String title, Long accountId);
}
