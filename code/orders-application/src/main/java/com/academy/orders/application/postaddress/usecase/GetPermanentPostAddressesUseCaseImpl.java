package com.academy.orders.application.postaddress.usecase;

import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import com.academy.orders.domain.postaddress.repository.PostAddressV2Repository;
import com.academy.orders.domain.postaddress.usecase.GetPermanentPostAddressesUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetPermanentPostAddressesUseCaseImpl implements GetPermanentPostAddressesUseCase {
  private final PostAddressV2Repository postAddressV2Repository;

  @Override
  public List<PostAddressV2> getPermanentPostAddressesByUserId(Long userId) {
    log.info("Get permanent post addresses for the user with id: {}", userId);
    return postAddressV2Repository.getPermanentPostAddressesByUserId(userId);
  }
}
