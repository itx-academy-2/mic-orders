package com.academy.orders.apirest.ordersV2.mapper;

import com.academy.orders_api_rest.generated.model.PlaceOrderRequestV2DTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.academy.orders.apirest.ModelUtils.getPlaceOrderRequestV2DTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderV2DtoMapperTest {
    private OrderV2DTOMapper mapper;

    @BeforeEach
    public void setUp() {mapper = Mappers.getMapper(OrderV2DTOMapper.class);}


    @Test
    void toCreateOrderV2Dto_WithValidValuesTest() {
        //Given
        var placeOrderRequestV2DTO = getPlaceOrderRequestV2DTO();

        //When
        var createOrderV2Dto = mapper.toCreateOrderV2Dto(placeOrderRequestV2DTO);

        //Then
        assertEquals(placeOrderRequestV2DTO.getFirstName(), createOrderV2Dto.firstName());
        assertEquals(placeOrderRequestV2DTO.getLastName(), createOrderV2Dto.lastName());
        assertEquals(placeOrderRequestV2DTO.getEmail(), createOrderV2Dto.email());
        assertEquals(placeOrderRequestV2DTO.getPhone(), createOrderV2Dto.phone());
        assertEquals(placeOrderRequestV2DTO.getCity(), createOrderV2Dto.city());
        assertEquals(placeOrderRequestV2DTO.getDeliveryMethod().getValue(), createOrderV2Dto.deliveryMethod().toString());
        assertEquals(placeOrderRequestV2DTO.getDepartment(), createOrderV2Dto.department());
        assertEquals(placeOrderRequestV2DTO.getTitle(), createOrderV2Dto.title());
    }

    @Test
    void toCreateOrderV2Dto_WithNullValuesTest() {
        //Given
        var placeOrderRequestV2DTO = new PlaceOrderRequestV2DTO();

        //When
        var createOrderV2Dto = mapper.toCreateOrderV2Dto(placeOrderRequestV2DTO);

        //Then
        assertNotNull(createOrderV2Dto);
        assertNull(createOrderV2Dto.firstName());
        assertNull(createOrderV2Dto.lastName());
        assertNull(createOrderV2Dto.email());
        assertNull(createOrderV2Dto.phone());
        assertNull(createOrderV2Dto.city());
        assertNull(createOrderV2Dto.deliveryMethod());
        assertNull(createOrderV2Dto.department());
        assertNull(createOrderV2Dto.title());
    }
}
