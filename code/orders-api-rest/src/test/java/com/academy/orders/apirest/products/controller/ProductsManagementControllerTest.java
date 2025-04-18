package com.academy.orders.apirest.products.controller;

import com.academy.orders.apirest.ModelUtils;
import com.academy.orders.apirest.common.TestSecurityConfig;
import com.academy.orders.apirest.common.mapper.PageableDTOMapper;
import com.academy.orders.apirest.products.mapper.ManagementProductMapper;
import com.academy.orders.apirest.products.mapper.ProductRequestDTOMapper;
import com.academy.orders.apirest.products.mapper.ProductResponseDTOMapper;
import com.academy.orders.apirest.products.mapper.ProductStatusDTOMapper;
import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import com.academy.orders.domain.product.usecase.CreateProductUseCase;
import com.academy.orders.domain.product.usecase.GetCountOfDiscountedProductsUseCase;
import com.academy.orders.domain.product.usecase.GetManagerProductsUseCase;
import com.academy.orders.domain.product.usecase.GetProductByIdUseCase;
import com.academy.orders.domain.product.usecase.UpdateProductUseCase;
import com.academy.orders.domain.product.usecase.UpdateStatusUseCase;
import com.academy.orders_api_rest.generated.model.PageableDTO;
import com.academy.orders_api_rest.generated.model.ProductManagementFilterDTO;
import com.academy.orders_api_rest.generated.model.ProductStatusDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.academy.orders.apirest.ModelUtils.getProduct;
import static com.academy.orders.apirest.ModelUtils.getProductManagementPageDTO;
import static com.academy.orders.apirest.ModelUtils.getProductRequestDTO;
import static com.academy.orders.apirest.ModelUtils.getProductRequestDto;
import static com.academy.orders.apirest.ModelUtils.getProductResponseDTO;
import static com.academy.orders.apirest.TestConstants.GET_PRODUCT_BY_ID_URL;
import static com.academy.orders.apirest.TestConstants.LANGUAGE_EN;
import static com.academy.orders.apirest.TestConstants.ROLE_MANAGER;
import static com.academy.orders.apirest.TestConstants.TEST_UUID;
import static com.academy.orders.apirest.TestConstants.UPDATE_PRODUCT_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductsManagementController.class)
@ContextConfiguration(classes = {ProductsManagementController.class})
@Import(value = {AopAutoConfiguration.class, TestSecurityConfig.class})
class ProductsManagementControllerTest {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UpdateStatusUseCase updateStatusUseCase;

  @MockBean
  private UpdateProductUseCase updateProductUseCase;

  @MockBean
  private ProductStatusDTOMapper productStatusDTOMapper;

  @MockBean
  private ProductRequestDTOMapper productRequestDTOMapper;

  @MockBean
  private PageableDTOMapper pageableDTOMapper;

  @MockBean
  private ManagementProductMapper managementProductMapper;

  @MockBean
  private GetManagerProductsUseCase managerProductsUseCase;

  @MockBean
  private CreateProductUseCase createProductUseCase;

  @MockBean
  private GetProductByIdUseCase getProductByIdUseCase;

  @MockBean
  private GetCountOfDiscountedProductsUseCase getCountOfDiscountedProductsUseCase;

  @MockBean
  private ProductResponseDTOMapper productResponseDTOMapper;

  @Test
  @WithMockUser(authorities = ROLE_MANAGER)
  @SneakyThrows
  void updateStatusTest() {
    // Given
    UUID productId = TEST_UUID;
    ProductStatusDTO productStatusDTO = ProductStatusDTO.VISIBLE;
    ProductStatus productStatus = ProductStatus.VISIBLE;

    when(productStatusDTOMapper.fromDTO(productStatusDTO)).thenReturn(productStatus);
    doNothing().when(updateStatusUseCase).updateStatus(productId, productStatus);

    // When
    mockMvc.perform(patch("/v1/management/products/{productId}/status", productId)
        .param("status", productStatusDTO.getValue()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // Then
    verify(productStatusDTOMapper).fromDTO(productStatusDTO);
    verify(updateStatusUseCase).updateStatus(productId, productStatus);
  }

  @Test
  @WithMockUser(authorities = ROLE_MANAGER)
  @SneakyThrows
  void updateProductTest() {
    var dto = getProductRequestDTO();
    var request = getProductRequestDto();

    when(productRequestDTOMapper.fromDTO(dto)).thenReturn(request);
    doNothing().when(updateProductUseCase).updateProduct(TEST_UUID, request);

    mockMvc.perform(patch(UPDATE_PRODUCT_URL, TEST_UUID).param("lang", LANGUAGE_EN).contentType("application/json")
        .content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk());

    verify(productRequestDTOMapper).fromDTO(dto);
    verify(updateProductUseCase).updateProduct(TEST_UUID, request);
  }

  @Test
  @WithMockUser(authorities = ROLE_MANAGER)
  @SneakyThrows
  void getProductsForManagerTest() {
    var lang = "uk";
    var pageable = ModelUtils.getPageable();
    var filter = ModelUtils.getManagementFilterDto();
    var pageOfProducts = ModelUtils.getPageOf(getProduct());
    var productManagementPageDTO = getProductManagementPageDTO();

    when(pageableDTOMapper.fromDto(any(PageableDTO.class))).thenReturn(pageable);
    when(managementProductMapper.fromProductManagementFilterDTO(any(ProductManagementFilterDTO.class)))
        .thenReturn(filter);
    when(managerProductsUseCase.getManagerProducts(pageable, filter, lang)).thenReturn(pageOfProducts);
    when(managementProductMapper.fromProductPage(pageOfProducts)).thenReturn(productManagementPageDTO);

    mockMvc.perform(get("/v1/management/products").queryParam("lang", lang)).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(productManagementPageDTO)));

    verify(pageableDTOMapper).fromDto(any(PageableDTO.class));
    verify(managementProductMapper).fromProductManagementFilterDTO(any(ProductManagementFilterDTO.class));
    verify(managerProductsUseCase).getManagerProducts(pageable, filter, lang);
    verify(managementProductMapper).fromProductPage(pageOfProducts);
  }

  @Test
  @WithMockUser(authorities = {"ROLE_MANAGER"})
  @SneakyThrows
  void createProductTest() {
    var createProductRequestDTO = getProductRequestDTO();
    var createProductRequest = productRequestDTOMapper.fromDTO(createProductRequestDTO);
    var createdProduct = ModelUtils.getProduct();
    var productResponseDTO = getProductResponseDTO();

    when(productRequestDTOMapper.fromDTO(createProductRequestDTO)).thenReturn(createProductRequest);
    when(createProductUseCase.createProduct(createProductRequest)).thenReturn(createdProduct);
    when(productResponseDTOMapper.toDTO(createdProduct)).thenReturn(productResponseDTO);

    mockMvc.perform(post("/v1/management/products").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createProductRequestDTO))).andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(productResponseDTO)));

    verify(productRequestDTOMapper, times(2)).fromDTO(createProductRequestDTO);
    verify(createProductUseCase).createProduct(createProductRequest);
    verify(productResponseDTOMapper).toDTO(createdProduct);
  }

  @Test
  @WithMockUser(authorities = {"ROLE_MANAGER"})
  @SneakyThrows
  void getProductByIdTest() {
    var product = getProduct();
    var response = getProductResponseDTO();

    when(productResponseDTOMapper.toDTO(product)).thenReturn(response);
    when(getProductByIdUseCase.getProductById(TEST_UUID)).thenReturn(product);

    mockMvc.perform(get(GET_PRODUCT_BY_ID_URL, TEST_UUID)).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(response)));

    verify(productResponseDTOMapper).toDTO(product);
    verify(getProductByIdUseCase).getProductById(TEST_UUID);
  }

  @Test
  @SneakyThrows
  @WithMockUser(authorities = {"ROLE_MANAGER"})
  void getCountOfDiscountedProducts() {
    var discountedProductsCount = 7;

    when(getCountOfDiscountedProductsUseCase.getCountOfDiscountedProducts()).thenReturn(discountedProductsCount);

    mockMvc.perform(get("/v1/management/products/discounted/count"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(discountedProductsCount));

    verify(getCountOfDiscountedProductsUseCase).getCountOfDiscountedProducts();
  }
}
