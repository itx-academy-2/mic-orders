package com.academy.orders.infrastructure.postaddress.repository;

import com.academy.orders.infrastructure.postaddress.entity.PostAddressV2Entity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostAddressJpaAdapter extends CrudRepository<PostAddressV2Entity, UUID> {
  /**
   * Retrieves a post address V2 entity by its title and associated account ID. This method is used to ensure that each account has unique
   * permanent post address titles.
   *
   * @param title the title of the post address (e.g., "permanent: Home").
   * @param accountId the ID of the account that owns the post address.
   * @return {@link Optional} containing the {@link PostAddressV2Entity} if found, or an empty {@link Optional} if not.
   * @author Oleksandra Bulhakova
   */
  Optional<PostAddressV2Entity> findByTitleAndAccount_Id(String title, Long accountId);

  @Query("""
      SELECT p FROM PostAddressV2Entity p WHERE p.account.id = :accountId AND p.title like 'permanent: %'
      """)
  List<PostAddressV2Entity> findPermanentPostAddressesByAccountId(@Param("accountId") Long accountId);
}
