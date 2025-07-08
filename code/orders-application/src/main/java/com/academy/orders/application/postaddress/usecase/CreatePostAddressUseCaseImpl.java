package com.academy.orders.application.postaddress.usecase;

import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import com.academy.orders.domain.postaddress.usecase.CreatePostAddressUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePostAddressUseCaseImpl implements CreatePostAddressUseCase {
    @Override
    public PostAddressV2 createPostAddress(PostAddressV2 postAddress) {
        return null;
    }
}