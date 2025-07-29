package com.academy.orders.infrastructure;

import com.academy.orders.domain.account.dto.AccountManagementFilterDto;
import com.academy.orders.domain.account.entity.Account;
import com.academy.orders.domain.account.entity.CreateAccountDTO;
import com.academy.orders.domain.account.entity.enumerated.Role;
import com.academy.orders.domain.account.entity.enumerated.UserStatus;
import com.academy.orders.domain.accountv2.dto.UpdateUserAccountV2InfoDto;
import com.academy.orders.domain.accountv2.entity.AccountV2;
import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.article.entity.ArticleContent;
import com.academy.orders.domain.cart.entity.CartItem;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.discount.entity.Discount;
import com.academy.orders.domain.order.dto.OrdersFilterParametersDto;
import com.academy.orders.domain.order.entity.Order;
import com.academy.orders.domain.order.entity.OrderItem;
import com.academy.orders.domain.order.entity.OrderReceiver;
import com.academy.orders.domain.order.entity.PostAddress;
import com.academy.orders.domain.order.entity.enumerated.DeliveryMethod;
import com.academy.orders.domain.order.entity.enumerated.OrderStatus;
import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenStatus;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import com.academy.orders.domain.product.dto.ProductManagementFilterDto;
import com.academy.orders.domain.product.entity.Language;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.entity.ProductManagement;
import com.academy.orders.domain.product.entity.ProductTranslationManagement;
import com.academy.orders.domain.product.entity.Tag;
import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import com.academy.orders.domain.orderV2.entity.OrderV2;
import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import com.academy.orders.infrastructure.account.entity.AccountEntity;
import com.academy.orders.infrastructure.article.entity.ArticleContentEntity;
import com.academy.orders.infrastructure.article.entity.ArticleEntity;
import com.academy.orders.infrastructure.cart.entity.CartItemEntity;
import com.academy.orders.infrastructure.cart.entity.CartItemId;
import com.academy.orders.infrastructure.discount.entity.DiscountEntity;
import com.academy.orders.infrastructure.language.entity.LanguageEntity;
import com.academy.orders.infrastructure.order.entity.OrderEntity;
import com.academy.orders.infrastructure.order.entity.OrderItemEntity;
import com.academy.orders.infrastructure.order.entity.OrderReceiverVO;
import com.academy.orders.infrastructure.orderV2.entity.OrderV2Entity;
import com.academy.orders.infrastructure.orderV2.entity.OrderItemV2Entity;
import com.academy.orders.infrastructure.postaddress.entity.PostAddressV2Entity;
import com.academy.orders.infrastructure.order.entity.PostAddressEntity;
import com.academy.orders.infrastructure.passwordreset.entity.PasswordResetTokenEntity;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationId;
import com.academy.orders.infrastructure.tag.entity.TagEntity;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.academy.orders.domain.order.entity.enumerated.DeliveryMethod.NOVA;
import static com.academy.orders.infrastructure.TestConstants.LANGUAGE_EN;
import static com.academy.orders.infrastructure.TestConstants.TEST_AMOUNT;
import static com.academy.orders.infrastructure.TestConstants.TEST_END_DATE;
import static com.academy.orders.infrastructure.TestConstants.TEST_FIRST_NAME;
import static com.academy.orders.infrastructure.TestConstants.TEST_ID;
import static com.academy.orders.infrastructure.TestConstants.TEST_LAST_NAME;
import static com.academy.orders.infrastructure.TestConstants.TEST_PHONE_NUMBER;
import static com.academy.orders.infrastructure.TestConstants.TEST_START_DATE;
import static com.academy.orders.infrastructure.TestConstants.TEST_UUID;

public class ModelUtils {
  public static final String TEST_IMAGE_LINK = "http://localhost:8080/image-1";

  private static final LocalDateTime DATE_TIME = LocalDateTime.of(1, 1, 1, 1, 1);

  private static final String TEST_IMAGE_NAME = "image-1";

  public static DiscountEntity getDiscountEntity() {
    return DiscountEntity.builder().amount(TEST_AMOUNT).startDate(TEST_START_DATE).endDate(TEST_END_DATE).build();
  }

  public static Discount getDiscount() {
    return Discount.builder().amount(TEST_AMOUNT).startDate(TEST_START_DATE).endDate(TEST_END_DATE).build();
  }

  public static AccountEntity getAccountEntity() {
    return AccountEntity.builder().id(1L).password("$2a$12$5ZEfkhNQUREmioQ54TaFaOEM7h/QBgASIeqZceFGKPT80aTfYdvV.")
        .email("mock@mail.com").firstName("MockFirst").lastName("MockLast").role(Role.ROLE_ADMIN)
        .status(UserStatus.ACTIVE).createdAt(DATE_TIME).phone("+380631234567").photo("https://somenotexistsurl.com/mynotexistsphoto.jpg")
        .build();
  }

  public static Account getAccount() {
    return Account.builder().id(1L).password("$2a$12$5ZEfkhNQUREmioQ54TaFaOEM7h/QBgASIeqZceFGKPT80aTfYdvV.")
        .email("mock@mail.com").firstName("MockFirst").lastName("MockLast").role(Role.ROLE_ADMIN)
        .status(UserStatus.ACTIVE).createdAt(DATE_TIME).build();
  }

  public static AccountV2 getAccountV2() {
    return AccountV2.builder().id(1L).email("user@mail.com").firstName("first").lastName("last")
        .password("$2a$12$j6tAmpJpMhU6ATtgRIS0puHsPVxs2upwoBUbTtakSt9tlZ6uZ04IC").role(Role.ROLE_ADMIN)
        .status(UserStatus.ACTIVE).createdAt(DATE_TIME)
        .phone("+380631234567").photo("https://somenotexistsurl.com/mynotexistsphoto.jpg").build();
  }

  public static UpdateUserAccountV2InfoDto getUpdateUserAccountV2InfoDto() {
    return UpdateUserAccountV2InfoDto.builder()
        .firstName(TEST_FIRST_NAME)
        .lastName(TEST_LAST_NAME)
        .phone(TEST_PHONE_NUMBER)
        .build();
  }

  public static CreateAccountDTO getCreateAccountDTO() {
    return CreateAccountDTO.builder().password("$2a$12$5ZEfkhNQUREmioQ54TaFaOEM7h/QBgASIeqZceFGKPT80aTfYdvV.")
        .email("mock@mail.com").firstName("MockFirst").lastName("MockLast").build();
  }

  public static ProductEntity getProductEntity() {
    return ProductEntity.builder().id(UUID.fromString("c39314ce-b659-4776-86b9-8201b05bb339"))
        .status(ProductStatus.VISIBLE).image(TEST_IMAGE_NAME).createdAt(DATE_TIME).quantity(100)
        .price(BigDecimal.valueOf(100.00)).build();
  }

  public static ProductEntity getProductEntityWithProductTranslations() {
    final ProductEntity productEntity = ProductEntity.builder().id(TEST_UUID)
        .status(ProductStatus.VISIBLE).image(TEST_IMAGE_NAME).createdAt(DATE_TIME).quantity(100)
        .price(BigDecimal.valueOf(100.00)).build();
    productEntity.setProductTranslations(Set.of(getProductTranslationEntity(productEntity)));
    return productEntity;
  }

  public static ProductTranslationEntity getProductTranslationEntity(final ProductEntity productEntity) {
    return ProductTranslationEntity.builder().productTranslationId(new ProductTranslationId(productEntity.getId(), 1L))
        .name("Name").description("Description").product(productEntity).language(getLanguageEntity())
        .build();
  }

  public static ProductEntity getProductEntityWithDiscount() {
    return ProductEntity.builder().id(UUID.fromString("c39314ce-b659-4776-86b9-8201b05bb339"))
        .status(ProductStatus.VISIBLE).image(TEST_IMAGE_NAME).createdAt(DATE_TIME).quantity(100)
        .discount(getDiscountEntity()).price(BigDecimal.valueOf(100.00)).build();
  }

  public static ProductEntity getProductEntityWithTranslation() {
    return ProductEntity.builder().id(UUID.fromString("c39314ce-b659-4776-86b9-8201b05bb339"))
        .status(ProductStatus.VISIBLE).image(TEST_IMAGE_NAME).createdAt(DATE_TIME).quantity(100)
        .price(BigDecimal.valueOf(100.00)).productTranslations(Set.of(getProductTranslationEntity())).build();
  }

  public static Product getProduct() {
    return Product.builder().id(UUID.fromString("c39314ce-b659-4776-86b9-8201b05bb339"))
        .status(ProductStatus.VISIBLE).image(TEST_IMAGE_NAME).createdAt(DATE_TIME).quantity(100)
        .price(BigDecimal.valueOf(100.00)).build();
  }

  public static OrderEntity getOrderEntity() {
    return OrderEntity.builder().id(UUID.fromString(String.valueOf(TEST_UUID))).createdAt(DATE_TIME)
        .editedAt(DATE_TIME).isPaid(false).orderStatus(OrderStatus.IN_PROGRESS).receiver(getOrderReceiverVO())
        .build();
  }

  public static OrderReceiverVO getOrderReceiverVO() {
    return OrderReceiverVO.builder().email("mock@mail.com").firstName("MockFirst").lastName("MockLast").build();
  }

  public static PostAddressEntity getPostAddressEntity() {
    return PostAddressEntity.builder().id(UUID.fromString("4602edda-6e9f-4a35-a472-2f6eac06e203"))
        .city("Mocked city").department("Mocked department").deliveryMethod(DeliveryMethod.NOVA).build();
  }

  public static OrderItemEntity getOrderItemEntity() {
    return OrderItemEntity.builder().price(BigDecimal.valueOf(100.00)).quantity(1).build();
  }

  public static OrderReceiver getOrderReceiver() {
    return OrderReceiver.builder().email("mock@mail.com").firstName("MockFirst").lastName("MockLast").build();
  }

  public static CartItemEntity getCartItemEntity() {
    var productEntity = getProductEntity();
    var accountEntity = getAccountEntity();
    return CartItemEntity.builder().cartItemId(new CartItemId(productEntity.getId(), accountEntity.getId()))
        .account(accountEntity).product(productEntity).quantity(1).build();
  }

  public static CartItem getCartItem() {
    return CartItem.builder().product(getProduct()).quantity(1).build();
  }

  public static <T> Page<T> getPage(List<T> content, long totalElements, int totalPages, int number, int size) {
    return Page.<T>builder().totalElements(totalElements).totalPages(totalPages).first(number == 0)
        .last(number == totalPages - 1).number(number).numberOfElements(content.size()).size(size)
        .empty(content.isEmpty()).content(content).build();
  }

  public static Pageable getPageable() {
    return getPageable(0, 8, List.of("id"));
  }

  public static Pageable getPageable(Integer page, Integer size, List<String> sort) {
    return Pageable.builder().page(page).size(size).sort(sort).build();
  }

  @SafeVarargs
  public static <T> Page<T> getPageOf(T... elements) {
    return Page.<T>builder().content(List.of(elements)).empty(false).first(true).last(false).number(1)
        .numberOfElements(10).totalPages(10).totalElements(100L).size(1).build();
  }

  public static Order getOrder() {
    return Order.builder().id(UUID.fromString("4602edda-6e9f-4a35-a472-2f6eac06e203"))
        .createdAt(LocalDateTime.of(1, 1, 1, 1, 1)).isPaid(false).orderStatus(OrderStatus.IN_PROGRESS)
        .postAddress(PostAddress.builder().city("Kyiv").deliveryMethod(NOVA).department("1").build())
        .receiver(getOrderReceiver()).orderItems(List.of(getOrderItem())).build();
  }

  public static OrderItem getOrderItem() {
    return OrderItem.builder().product(getProduct()).quantity(3).price(BigDecimal.valueOf(200)).build();
  }

  public static OrdersFilterParametersDto getOrdersFilterParametersDto() {
    return OrdersFilterParametersDto.builder().deliveryMethods(List.of(DeliveryMethod.NOVA))
        .statuses(List.of(OrderStatus.IN_PROGRESS)).isPaid(false).createdBefore(DATE_TIME)
        .createdAfter(DATE_TIME).totalMore(BigDecimal.ZERO).totalLess(BigDecimal.TEN)
        .accountEmail("test@mail.com").build();
  }

  public static ProductManagementFilterDto getManagementFilterDto() {
    return ProductManagementFilterDto.builder().status(ProductStatus.VISIBLE).createdBefore(DATE_TIME)
        .createdAfter(DATE_TIME).priceMore(BigDecimal.ZERO).priceLess(BigDecimal.TEN).build();
  }

  @SafeVarargs
  public static <T> PageImpl<T> getPageImplOf(T... elements) {
    return new PageImpl<>(List.of(elements));
  }

  public static ProductTranslationEntity getProductTranslationEntity() {
    return ProductTranslationEntity.builder().productTranslationId(new ProductTranslationId(TEST_UUID, 1L))
        .name("Name").description("Description").product(getProductEntity()).language(getLanguageEntity())
        .build();
  }

  public static ProductTranslationManagement getProductTranslationManagement() {
    return ProductTranslationManagement.builder().productId(TEST_UUID).languageId(1L).name("Name")
        .description("Description").language(new Language(1L, LANGUAGE_EN)).build();
  }

  public static ProductManagement getProductManagement() {
    return ProductManagement.builder().id(TEST_UUID).status(ProductStatus.VISIBLE).createdAt(LocalDateTime.now())
        .quantity(10).price(BigDecimal.valueOf(100.00)).tags(Set.of(new Tag(1L, "tag")))
        .productTranslationManagement(Set.of(getProductTranslationManagement())).build();
  }

  public static TagEntity getTagEntity() {
    return TagEntity.builder().id(TEST_ID).name("category:mobile").build();
  }

  public static Tag getTag() {
    return Tag.builder().id(TEST_ID).name("category:mobile").build();
  }

  public static LanguageEntity getLanguageEntity() {
    return LanguageEntity.builder().id(1L).code("en").build();
  }

  public static LanguageEntity getLanguageEntity(String code) {
    return LanguageEntity.builder().id(1L).code(code).build();
  }

  public static Language getLanguage() {
    return Language.builder().id(1L).code("en").build();
  }

  public static Page<Account> getAccountPage(List<Account> accountDomains, Pageable pageableDomain,
      long totalElements, int totalPages) {
    return Page.<Account>builder().content(accountDomains).number(pageableDomain.page()).size(pageableDomain.size())
        .totalElements(totalElements).totalPages(totalPages).first(pageableDomain.page() == 0)
        .last(pageableDomain.page() == totalPages - 1).numberOfElements(accountDomains.size())
        .empty(accountDomains.isEmpty()).build();
  }

  public static AccountManagementFilterDto getAccountManagementFilterDto() {
    return AccountManagementFilterDto.builder().status(UserStatus.ACTIVE).role(Role.ROLE_USER).build();
  }

  public static PageRequest getPageRequest() {
    return PageRequest.of(0, 10, org.springframework.data.domain.Sort.by("id").ascending());
  }

  public static ArticleContentEntity getArticleContentEntity() {
    final ArticleContentEntity articleContentEntity = new ArticleContentEntity();
    articleContentEntity.setTitle("Title");
    articleContentEntity.setContent("Content");
    articleContentEntity.setLanguage(getLanguageEntity());
    return articleContentEntity;
  }

  public static ArticleEntity getArticleEntity() {
    final ArticleEntity articleEntity = new ArticleEntity();
    articleEntity.setId(TEST_ID);
    articleEntity.setCreatedAt(TEST_START_DATE);
    articleEntity.setUpdatedAt(TEST_END_DATE);
    articleEntity.setContents(List.of(getArticleContentEntity()));
    return articleEntity;
  }

  public static ArticleContent getArticleContent() {
    return ArticleContent.builder()
        .title("Title")
        .content("Content")
        .language(getLanguage())
        .build();
  }

  public static Article getArticle() {
    final Article article = Article.builder()
        .id(TEST_ID)
        .createdAt(TEST_START_DATE)
        .updatedAt(TEST_END_DATE)
        .contents(List.of(getArticleContent()))
        .build();
    return article;
  }

  public static PasswordResetToken createSampleToken() {
    return PasswordResetToken.builder()
        .id(7L)
        .token("roundtrip-domain")
        .accountId(200L)
        .email("roundtripdomain@example.com")
        .type(TokenType.SECONDARY)
        .status(TokenStatus.USED)
        .createdAt(OffsetDateTime.now(ZoneOffset.UTC).minusDays(1))
        .expiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusDays(1))
        .build();
  }

  public static PasswordResetTokenEntity createSampleEntity() {
    PasswordResetTokenEntity entity = new PasswordResetTokenEntity();
    entity.setId(5L);
    entity.setToken("roundtrip");
    entity.setAccountId(100L);
    entity.setEmail("roundtrip@example.com");
    entity.setType(TokenType.PRIMARY);
    entity.setStatus(TokenStatus.ACTIVE);
    entity.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
    entity.setExpiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusHours(2));
    return entity;
  }
  
  public static OrderV2 getOrderV2() {
    return OrderV2.builder().id(UUID.fromString("4602edda-6e9f-4a35-a472-2f6eac06e203"))
        .createdAt(LocalDateTime.of(1, 1, 1, 1, 1)).isPaid(false).orderStatus(OrderStatus.IN_PROGRESS)
        .postAddress(PostAddressV2.builder().city("Kharkiv").deliveryMethod(NOVA).department("1")
            .recipientFirstName("Jane").recipientLastName("Doe").recipientPhone("+380960776655").title("Home")
            .account(AccountV2.builder().id(23L).build()).orders(List.of(OrderV2.builder()
                .id(UUID.fromString("2202edda-6e9f-4a35-a472-2f6eac06e203")).build()))
            .build())
        .account(AccountV2.builder().id(23L).build())
        .orderItems(List.of(getOrderItem())).build();
  }

  public static PostAddressV2 getPostAddressV2() {
    return PostAddressV2.builder().city("Kharkiv").deliveryMethod(NOVA).department("1")
        .recipientFirstName("Jim").recipientLastName("Doe").recipientPhone("+380960776655").title("Friend")
        .account(AccountV2.builder().id(23L).build()).orders(List.of(OrderV2.builder()
            .id(UUID.fromString("2202edda-6e9f-4a35-a472-2f6eac06e203")).build()))
        .build();
  }

  public static OrderItemV2Entity getOrderItemV2Entity() {
    return OrderItemV2Entity.builder().price(BigDecimal.valueOf(100.00)).quantity(1).build();
  }

  public static PostAddressV2Entity getPostAddressV2Entity() {
    return PostAddressV2Entity.builder().title("Home").city("Kharkiv").deliveryMethod(NOVA).department("43")
        .recipientFirstName("Sasha").recipientLastName("Bulhakova").recipientPhone("+380960997887").build();
  }

  public static OrderV2Entity getOrderV2Entity() {
    return OrderV2Entity.builder().orderItems(List.of(getOrderItemV2EntityWithProduct()))
        .orderStatus(OrderStatus.IN_PROGRESS)
        .createdAt(LocalDateTime.of(2025, 1, 1, 1, 1)).isPaid(false)
        .postAddress(getPostAddressV2EntityWithNewTitle())
        .build();
  }

  public static PostAddressV2Entity getPostAddressV2EntityWithNewTitle() {
    return PostAddressV2Entity.builder().title("permanent: Friend").city("Kharkiv").deliveryMethod(NOVA)
        .department("43")
        .recipientFirstName("Sasha").recipientLastName("Bulhakova").recipientPhone("+380960997887").build();
  }

  public static OrderV2Entity getOrderV2EntityWithId() {
    return OrderV2Entity.builder().id(UUID.randomUUID()).account(getAccountEntity()).orderItems(List.of(getOrderItemV2Entity()))
        .orderStatus(OrderStatus.IN_PROGRESS)
        .createdAt(LocalDateTime.of(2025, 1, 1, 1, 1)).isPaid(false)
        .postAddress(getPostAddressV2EntityWithNewTitle())
        .build();
  }

  public static OrderItemV2Entity getOrderItemV2EntityWithProduct() {
    return OrderItemV2Entity.builder().price(BigDecimal.valueOf(100.00)).quantity(1)
        .product(ProductEntity.builder().id(UUID.fromString("c39314ce-b659-4776-86b9-8201b05bb339"))
            .status(ProductStatus.VISIBLE).image(TEST_IMAGE_NAME).createdAt(DATE_TIME).quantity(100)
            .price(BigDecimal.valueOf(100.00)).build())
        .build();
  }

  public static OrderV2 getOrderV2WithoutId() {
    return OrderV2.builder()
        .createdAt(LocalDateTime.of(1, 1, 1, 1, 1)).isPaid(false).orderStatus(OrderStatus.IN_PROGRESS)
        .postAddress(PostAddressV2.builder().city("Kharkiv").deliveryMethod(NOVA).department("1")
            .recipientFirstName("Sasha").recipientLastName("Bulhakova").recipientPhone("+380960776655").title("Friend")
            .account(AccountV2.builder().id(23L).build()).orders(List.of(OrderV2.builder()
                .id(UUID.fromString("2202edda-6e9f-4a35-a472-2f6eac06e203")).build()))
            .build())
        .account(AccountV2.builder().id(23L).build())
        .orderItems(List.of(getOrderItem())).build();
  }

  public static PostAddressV2 getPostAddressV2WithNewRecipientInfo() {
    return PostAddressV2.builder().city("Kharkiv").deliveryMethod(NOVA).department("1")
        .recipientFirstName("Sasha").recipientLastName("Bulhakova").recipientPhone("+380960776655").title("Friend")
        .account(AccountV2.builder().id(23L).build()).orders(List.of(OrderV2.builder()
            .id(UUID.fromString("2202edda-6e9f-4a35-a472-2f6eac06e203")).build()))
        .build();
  }

  public static OrderV2 getOrderV2WithoutIdWithNewRecipientInfo() {
    return OrderV2.builder()
        .createdAt(LocalDateTime.of(1, 1, 1, 1, 1)).isPaid(false).orderStatus(OrderStatus.IN_PROGRESS)
        .postAddress(PostAddressV2.builder().city("Kharkiv").deliveryMethod(NOVA).department("43")
            .recipientFirstName("Sasha").recipientLastName("Bulhakova").recipientPhone("+380960997887").title("Friend")
            .account(AccountV2.builder().id(23L).build()).orders(List.of(OrderV2.builder()
                .id(UUID.fromString("2202edda-6e9f-4a35-a472-2f6eac06e203")).build()))
            .build())
        .account(AccountV2.builder().id(23L).build())
        .orderItems(List.of(getOrderItem())).build();
  }

  public static OrderV2 getOrderV2WithoutIdAndNullTitle() {
    return OrderV2.builder()
        .createdAt(LocalDateTime.of(1, 1, 1, 1, 1)).isPaid(false).orderStatus(OrderStatus.IN_PROGRESS)
        .postAddress(PostAddressV2.builder().city("Kharkiv").deliveryMethod(NOVA).department("1")
            .recipientFirstName("Sasha").recipientLastName("Bulhakova").recipientPhone("+380960776655")
            .account(AccountV2.builder().id(23L).build()).orders(List.of(OrderV2.builder()
                .id(UUID.fromString("2202edda-6e9f-4a35-a472-2f6eac06e203")).build()))
            .build())
        .account(AccountV2.builder().id(23L).build())
        .orderItems(List.of(getOrderItem())).build();
  }

  public static OrderV2Entity getOrderV2EntityWithTempTitle() {
    return OrderV2Entity.builder().orderItems(List.of(getOrderItemV2EntityWithProduct()))
        .orderStatus(OrderStatus.IN_PROGRESS)
        .createdAt(LocalDateTime.of(2025, 1, 1, 1, 1)).isPaid(false)
        .postAddress(getPostAddressV2EntityWithTempTitle())
        .build();
  }

  public static PostAddressV2Entity getPostAddressV2EntityWithTempTitle() {
    return PostAddressV2Entity.builder().title("temp: " + UUID.randomUUID()).city("Kharkiv").deliveryMethod(NOVA)
        .department("43")
        .recipientFirstName("Sasha").recipientLastName("Bulhakova").recipientPhone("+380960997887").build();
  }

  public static OrderV2Entity getOrderV2EntityWithNullTitle() {
    return OrderV2Entity.builder().orderItems(List.of(getOrderItemV2EntityWithProduct()))
        .orderStatus(OrderStatus.IN_PROGRESS)
        .createdAt(LocalDateTime.of(2025, 1, 1, 1, 1)).isPaid(false)
        .postAddress(PostAddressV2Entity.builder().city("Kharkiv").deliveryMethod(NOVA)
            .department("43")
            .recipientFirstName("Sasha").recipientLastName("Bulhakova").recipientPhone("+380960997887").build())
        .build();
  }

  public static PostAddressV2Entity getPostAddressV2EntityWithNoTitle() {
    return PostAddressV2Entity.builder().city("Kharkiv").deliveryMethod(NOVA)
        .department("43")
        .recipientFirstName("Sasha").recipientLastName("Bulhakova").recipientPhone("+380960997887").build();
  }
}
