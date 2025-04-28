package com.academy.orders.apirest.products.controller;

import com.academy.orders.apirest.ModelUtils;
import com.academy.orders.apirest.common.TestSecurityConfig;
import com.academy.orders.apirest.common.mapper.PageableDTOMapper;
import com.academy.orders.apirest.products.mapper.PageProductSearchResultDTOMapper;
import com.academy.orders.apirest.products.mapper.ProductDetailsResponseDTOMapper;
import com.academy.orders.apirest.products.mapper.ProductPreviewDTOMapper;
import com.academy.orders.apirest.products.mapper.ProductsOnSaleFilterMapper;
import com.academy.orders.apirest.products.mapper.ProductsOnSaleResponseDTOMapper;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.usecase.*;
import com.academy.orders_api_rest.generated.model.PageProductSearchResultDTO;
import com.academy.orders_api_rest.generated.model.PageProductsDTO;
import com.academy.orders_api_rest.generated.model.PageableDTO;
import com.academy.orders_api_rest.generated.model.ProductPreviewDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static com.academy.orders.apirest.ModelUtils.*;
import static com.academy.orders.apirest.TestConstants.*;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductsController.class)
@ContextConfiguration(classes = {ProductsController.class})
@Import(value = {AopAutoConfiguration.class, TestSecurityConfig.class})
class ProductsControllerTest {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GetAllProductsUseCase getAllProductsUseCase;

  @MockBean
  private GetProductSearchResultsUseCase getProductSearchResultsUseCase;

  @MockBean
  private ProductsOnSaleFilterMapper productsOnSaleFilterMapper;

  @MockBean
  private ProductsOnSaleResponseDTOMapper productsOnSaleResponseDTOMapper;

  @MockBean
  private GetProductDetailsByIdUseCase getProductDetailsByIdUseCase;

  @MockBean
  private ProductPreviewDTOMapper productPreviewDTOMapper;

  @MockBean
  private PageProductSearchResultDTOMapper pageProductSearchResultDTOMapper;

  @MockBean
  private PageableDTOMapper pageableDTOMapper;

  @MockBean
  private GetProductsOnSaleUseCase getProductsOnSaleUseCase;

  @MockBean
  private FindProductsBestsellersUseCase findProductsBestsellersUseCase;

  @MockBean
  private ProductDetailsResponseDTOMapper productDetailsResponseDTOMapper;

  @MockBean
  private FindMostSoldProductByTagUseCase findMostSoldProductByTagUseCase;

  @Test
  void getProductsTest() throws Exception {
    var pageableDTO = getPageableDTO();
    var pageable = getPageable();
    var pageProducts = getProductsPage();
    var pageProductsDTO = getPageProductsDTO();
    List<String> tags = emptyList();

    when(pageableDTOMapper.fromDto(pageableDTO)).thenReturn(pageable);
    when(getAllProductsUseCase.getAllProducts(LANGUAGE_UK, pageable, tags)).thenReturn(pageProducts);
    when(productPreviewDTOMapper.toPageProductsDTO(pageProducts)).thenReturn(pageProductsDTO);

    mockMvc.perform(get(GET_ALL_PRODUCTS_URL).param("lang", LANGUAGE_UK).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(pageProductsDTO)));

    verify(pageableDTOMapper).fromDto(pageableDTO);
    verify(getAllProductsUseCase).getAllProducts(LANGUAGE_UK, pageable, emptyList());
    verify(productPreviewDTOMapper).toPageProductsDTO(pageProducts);
  }

  @Test
  @SneakyThrows
  void getProductsOnSaleTest() {
    var pageableDTO = getPageableDTO();
    var pageable = getPageable();
    var productsOnSaleFilterDto = getProductsOnSaleFilterDto();
    var productsOnSaleResponseDto = getProductsOnSaleResponseDto();
    var productsOnSaleResponseDTO = getProductsOnSaleResponseDTO();
    var language = LANGUAGE_EN;

    when(pageableDTOMapper.fromDto(pageableDTO)).thenReturn(pageable);
    when(productsOnSaleFilterMapper.fromDto(any())).thenReturn(productsOnSaleFilterDto);
    when(getProductsOnSaleUseCase.getProductsOnSale(productsOnSaleFilterDto, pageable, language))
        .thenReturn(productsOnSaleResponseDto);
    when(productsOnSaleResponseDTOMapper.toProductsOnSaleResponseDTO(productsOnSaleResponseDto))
        .thenReturn(productsOnSaleResponseDTO);

    mockMvc.perform(get(GET_PRODUCTS_ON_SALES_URL).param("lang", language)
        .param("minimumDiscount", String.valueOf(productsOnSaleFilterDto.minimumDiscount())))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(productsOnSaleResponseDTO)));

    verify(productsOnSaleFilterMapper).fromDto(any());
    verify(pageableDTOMapper).fromDto(pageableDTO);
    verify(getProductsOnSaleUseCase).getProductsOnSale(productsOnSaleFilterDto, pageable, language);
    verify(productsOnSaleResponseDTOMapper).toProductsOnSaleResponseDTO(productsOnSaleResponseDto);
  }

  @Test
  @SneakyThrows
  void searchProductsTest() {
    // Given
    String searchQuery = "some text";
    String lang = "en";
    PageableDTO pageableDTO = getPageableDTO();
    Pageable pageable = getPageable();
    Page<Product> productPage = getPageOf(getProduct());
    var expected = ModelUtils.getPageProductSearchResultDTO();

    when(pageableDTOMapper.fromDto(pageableDTO)).thenReturn(pageable);
    when(getProductSearchResultsUseCase.findProductsBySearchQuery(searchQuery, lang, pageable))
        .thenReturn(productPage);
    when(pageProductSearchResultDTOMapper.toDto(productPage)).thenReturn(expected);

    // When
    String response = mockMvc
        .perform(get(SEARCH_PRODUCTS_URL).param("searchQuery", searchQuery).param("lang", lang)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

    // Then
    assertEquals(expected, objectMapper.readValue(response, PageProductSearchResultDTO.class));
    verify(pageableDTOMapper).fromDto(pageableDTO);
    verify(getProductSearchResultsUseCase).findProductsBySearchQuery(searchQuery, lang, pageable);
    verify(pageProductSearchResultDTOMapper).toDto(productPage);
  }

  @Test
  @SneakyThrows
  void getProductDetailsByIdTest() {
    var response = getProductDetailsResponseDTO();
    var product = getProduct();
    when(getProductDetailsByIdUseCase.getProductDetailsById(TEST_UUID, "en")).thenReturn(product);
    when(productDetailsResponseDTOMapper.toDTO(product)).thenReturn(response);

    String result = mockMvc.perform(get(GET_PRODUCT_DETAILS_URL, TEST_UUID).param("lang", "en"))
        .andExpect(status().isOk()).andExpect(content().json(objectMapper.writeValueAsString(response)))
        .andReturn().getResponse().getContentAsString();

    assertEquals(objectMapper.writeValueAsString(response), result);
  }

  @Test
  @SneakyThrows
  void findBestsellersTest() {
    final String language = LANGUAGE_EN;
    final Page<Product> productPage = getPageOf(getProduct());
    final PageProductsDTO result = getPageProductsDTO();

    when(findProductsBestsellersUseCase.findProductsBestsellers(any(Pageable.class), eq(language)))
        .thenReturn(productPage);
    when(productPreviewDTOMapper.toPageProductsDTO(productPage)).thenReturn(result);
    final MvcResult mvcResult = mockMvc.perform(get(FIND_BESTSELLERS_URL)
        .param("lang", language)
        .param("size", "1")
        .param("page", "0")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn();

    verify(findProductsBestsellersUseCase).findProductsBestsellers(any(Pageable.class), eq(language));
    verify(productPreviewDTOMapper).toPageProductsDTO(productPage);
    assertEquals(objectMapper.writeValueAsString(result), mvcResult.getResponse().getContentAsString());
  }

  @SneakyThrows
  @Test
  void findMostSoldProductByTagTest() {
    final String lang = LANGUAGE_EN;
    final String tag = "tag";
    final Product product = getProduct();
    final ProductPreviewDTO productPreviewDTO = getProductPreviewDTO();

    when(findMostSoldProductByTagUseCase.findMostSoldProductByTag(lang, tag)).thenReturn(Optional.of(product));
    when(productPreviewDTOMapper.toDto(product)).thenReturn(productPreviewDTO);

    final MvcResult mvcResult = mockMvc.perform(get(FIND_BESTSELLER_URL)
        .param("tag", tag)
        .param("lang", lang)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn();

    assertEquals(objectMapper.writeValueAsString(productPreviewDTO), mvcResult.getResponse().getContentAsString());

    verify(findMostSoldProductByTagUseCase).findMostSoldProductByTag(lang, tag);
    verify(productPreviewDTOMapper).toDto(product);
  }

  @SneakyThrows
  @Test
  void findMostSoldProductByTagWhenNoContentTest() {
    final String lang = LANGUAGE_EN;
    final String tag = "tag";
    final Product product = getProduct();
    final ProductPreviewDTO productPreviewDTO = getProductPreviewDTO();

    when(findMostSoldProductByTagUseCase.findMostSoldProductByTag(lang, tag)).thenReturn(Optional.empty());
    when(productPreviewDTOMapper.toDto(product)).thenReturn(productPreviewDTO);

    mockMvc.perform(get(FIND_BESTSELLER_URL)
        .param("tag", tag)
        .param("lang", lang)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent()).andReturn();

    verify(findMostSoldProductByTagUseCase).findMostSoldProductByTag(lang, tag);
    verify(productPreviewDTOMapper, never()).toDto(product);
  }
}
