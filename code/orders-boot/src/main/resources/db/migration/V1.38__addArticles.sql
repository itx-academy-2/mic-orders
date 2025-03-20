INSERT INTO articles (id, created_at, updated_at)
VALUES (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SELECT setval('articles_id_seq', (SELECT MAX(id) FROM articles));

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (1, 1, 'What payment methods do you accept?',
        'We strive to make your shopping experience as convenient as possible by accepting a variety of payment methods. At our Gadget Store, you can pay using major credit and debit cards, including Visa, MasterCard, and American Express. This ensures that you can complete your purchase securely and efficiently.

In addition to credit and debit cards, we also accept payments through PayPal. PayPal offers a quick and secure way to pay for your orders, providing an extra layer of protection for your financial information. Simply select PayPal at checkout, log in to your account, and confirm your payment.

For those who prefer other payment methods, we are continuously working to expand our options to better serve our customers. Stay tuned for updates as we introduce new ways to pay, ensuring that your shopping experience remains seamless and enjoyable.');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (1, 2, 'Які способи оплати ви приймаєте?',
        'Ми прагнемо зробити ваш досвід покупок якомога зручнішим, приймаючи різноманітні способи оплати. У нашому магазині гаджетів ви можете оплатити за допомогою основних кредитних та дебетових карток, включаючи Visa, MasterCard та American Express. Це гарантує, що ви зможете безпечно та ефективно завершити свою покупку.

Крім кредитних та дебетових карток, ми також приймаємо платежі через PayPal. PayPal пропонує швидкий та безпечний спосіб оплати ваших замовлень, надаючи додатковий рівень захисту вашої фінансової інформації. Просто виберіть PayPal при оформленні замовлення, увійдіть у свій обліковий запис і підтвердьте оплату.

Для тих, хто віддає перевагу іншим способам оплати, ми постійно працюємо над розширенням наших можливостей, щоб краще обслуговувати наших клієнтів. Слідкуйте за оновленнями, оскільки ми впроваджуємо нові способи оплати, забезпечуючи, щоб ваш досвід покупок залишався безперешкодним та приємним.');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (2, 1, 'What are your shipping options and costs?',
        'At our Gadget Store, we offer two reliable shipping options to ensure your purchases reach you quickly and safely. You can choose between Ukr Post and Nova Post, both of which provide efficient and secure delivery services. Whether you prefer the national postal service or a private courier, we''ve got you covered.

<img class="center-img" src="https://lucent-cannoli-79beba.netlify.app/delivery-methods.jpg"/>

We are delighted to offer <span style="color: red; font-weight: bold;">free shipping</span> for all our amazing users. That''s right – no matter the size or cost of your order, you won''t have to pay a dime for shipping. We believe in providing exceptional value and convenience to our customers, and free shipping is just one way we do that.

Our shipping process is designed to be hassle-free and transparent. Once your order is placed, you can track it with our partners: Ukr Post and Nova Post, allowing you to monitor your package''s journey from our warehouse to your doorstep. Enjoy the peace of mind that comes with knowing your gadgets are on their way, without any additional shipping costs. ');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (2, 2, 'Які у вас варіанти доставки та їх вартість?',
        'У нашому магазині гаджетів ми пропонуємо два надійні варіанти доставки, щоб ваші покупки швидко і безпечно дісталися до вас. Ви можете вибрати між Укрпоштою та Новою Поштою, обидві з яких забезпечують ефективні та безпечні послуги доставки. Незалежно від того, чи ви віддаєте перевагу національній поштовій службі або приватному кур''єру, ми подбали про це.

<img class="center-img" src="https://lucent-cannoli-79beba.netlify.app/delivery-methods.jpg"/>

Ми раді запропонувати <span style="color: red; font-weight: bold;">безкоштовну доставку</span> для всіх наших чудових користувачів. Так, це правильно – незалежно від розміру або вартості вашого замовлення, вам не доведеться платити за доставку. Ми віримо в надання виняткової цінності та зручності нашим клієнтам, і безкоштовна доставка – це один зі способів, яким ми це робимо.

Наш процес доставки розроблений, щоб бути безпроблемним і прозорим. Після оформлення замовлення ви зможете відстежувати його за допомогою наших партнерів: Укрпошти та Нової Пошти, що дозволяє вам стежити за подорожжю вашого пакунка від нашого складу до вашого порогу. Насолоджуйтесь спокоєм, знаючи, що ваші гаджети вже в дорозі, без додаткових витрат на доставку. ');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (3, 1, 'Can I modify or cancel my order after it has been placed? ',
        'We understand that sometimes you may need to make changes to your order after it has been placed. If you need to modify or cancel your order, please contact us as soon as possible. Our dedicated shop manager is here to assist you and will do their best to accommodate your request.

You can reach out to us via email or phone to initiate the modification or cancellation process. Provide your order number and details of the changes you wish to make, and our team will take it from there. We aim to respond promptly to ensure that your request is handled efficiently.

Please note that there may be a limited window of time to modify or cancel your order, especially if it has already been processed for shipping. However, we will do our utmost to assist you and ensure that your shopping experience with us is a positive one. ');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (3, 2, 'Чи можу я змінити або скасувати своє замовлення після його оформлення? ',
        'Ми розуміємо, що іноді вам може знадобитися внести зміни до вашого замовлення після його оформлення. Якщо вам потрібно змінити або скасувати замовлення, будь ласка, зв''яжіться з нами якомога швидше. Наш відданий менеджер магазину готовий допомогти вам і зробить усе можливе, щоб задовольнити ваш запит.

Ви можете зв''язатися з нами електронною поштою або телефоном, щоб ініціювати процес зміни або скасування замовлення. Надішліть нам номер вашого замовлення та деталі змін, які ви бажаєте внести, і наша команда займеться цим питанням. Ми прагнемо швидко реагувати, щоб забезпечити ефективне виконання вашого запиту.

Зверніть увагу, що може бути обмежений час для зміни або скасування вашого замовлення, особливо якщо його вже оброблено для доставки. Однак ми зробимо все можливе, щоб допомогти вам і забезпечити позитивний досвід покупок у нашому магазині.');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (4, 1, 'How do I find the right product for my needs?',
        'Finding the perfect gadget can be a daunting task, but we''re here to help. Start by reading our detailed product descriptions, which provide valuable insights into the features and specifications of each item. This information will help you understand what each gadget offers and how it can meet your needs.

Utilize our website''s filtering and sorting features to narrow down your options. You can filter products based on categories, price ranges, and specific features, making it easier to find gadgets that match your preferences. Sorting options also allow you to prioritize products by popularity, ratings, or newest arrivals.

Don''t forget to check out our Bestseller section, where you''ll find some of the most popular and highly-rated gadgets. These products have been tried and tested by other customers, giving you an idea of their performance and reliability. Additionally, explore our Sales page for exciting discounts and special offers on a wide range of gadgets. ');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (4, 2, 'Як знайти потрібний продукт для моїх потреб?',
        'Знайти ідеальний гаджет може бути непростим завданням, але ми тут, щоб допомогти. Почніть з читання наших детальних описів продуктів, які надають цінну інформацію про характеристики та специфікації кожного товару. Ця інформація допоможе вам зрозуміти, що пропонує кожен гаджет і як він може задовольнити ваші потреби.

Використовуйте функції фільтрації та сортування на нашому веб-сайті, щоб звузити вибір. Ви можете фільтрувати продукти за категоріями, ціновими діапазонами та конкретними характеристиками, що полегшить пошук гаджетів, які відповідають вашим уподобанням. Опції сортування також дозволяють пріоритезувати продукти за популярністю, рейтингами або новинками.

Не забудьте перевірити наш розділ "Бестселери", де ви знайдете деякі з найпопулярніших і високо оцінених гаджетів. Ці продукти були випробувані іншими клієнтами, що дає вам уявлення про їх продуктивність і надійність. Крім того, ознайомтеся з нашою сторінкою розпродажів, де ви знайдете захоплюючі знижки та спеціальні пропозиції на широкий асортимент гаджетів. ');


INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (5, 1, 'How do I set up and use my new gadget?',
        'Setting up and using your new gadget is a breeze with our comprehensive guides and resources. Each product comes with a user manual that provides step-by-step instructions for getting started. These manuals cover everything from initial setup to advanced features, ensuring you can make the most of your new device.

For additional assistance, we offer video tutorials that visually guide you through the setup and usage process. These videos are designed to be easy to follow, helping you understand each step clearly.

If you encounter any issues or have specific questions, our team is always here to help. Reach out to us via email or phone, and we''ll provide the guidance you need to get your gadget up and running smoothly. ');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (5, 2, 'Як налаштувати та використовувати свій новий гаджет? ',
        'Налаштувати та використовувати свій новий гаджет дуже просто з нашими комплексними посібниками та ресурсами. Кожен продукт постачається з інструкцією користувача, яка містить покрокові інструкції для початку роботи. Ці посібники охоплюють усе: від початкового налаштування до розширених функцій, забезпечуючи вам можливість максимально використовувати свій новий пристрій.

Для додаткової допомоги ми пропонуємо відеоуроки, які візуально проводять вас через процес налаштування та використання. Ці відео розроблені так, щоб їх було легко зрозуміти, допомагаючи вам чітко зрозуміти кожен крок.

Якщо ви зіткнетеся з будь-якими проблемами або маєте конкретні запитання, наша команда завжди готова допомогти. Зв''яжіться з нами електронною поштою або телефоном, і ми надамо вам необхідні вказівки для успішного запуску вашого гаджета. ');


INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (6, 1, 'How do you protect my personal information? ',
        'At our Gadget Store, we take the protection of your personal information very seriously. Our privacy policy outlines the measures we take to safeguard your data and ensure your privacy. We collect only the information necessary to process your orders and provide you with a seamless shopping experience.

We use advanced encryption technologies to protect your data during transmission and storage. This ensures that your personal and financial information is secure from unauthorized access. Our website is also regularly monitored and updated to prevent potential security breaches.

Additionally, we are committed to transparency and will never share your personal information with third parties without your explicit consent. You can shop with confidence, knowing that your privacy is our top priority. ');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (6, 2, 'Як ви захищаєте мою особисту інформацію? ',
        'У нашому магазині гаджетів ми дуже серйозно ставимося до захисту вашої особистої інформації. Наша політика конфіденційності описує заходи, які ми вживаємо для захисту ваших даних та забезпечення вашої конфіденційності. Ми збираємо лише ту інформацію, яка необхідна для обробки ваших замовлень та забезпечення безперебійного процесу покупок.

Ми використовуємо передові технології шифрування для захисту ваших даних під час передачі та зберігання. Це гарантує, що ваша особиста та фінансова інформація захищена від несанкціонованого доступу. Наш веб-сайт також регулярно моніториться та оновлюється для запобігання можливим порушенням безпеки.

Крім того, ми зобов''язуємося до прозорості та ніколи не будемо ділитися вашою особистою інформацією з третіми сторонами без вашої явної згоди. Ви можете робити покупки з упевненістю, знаючи, що ваша конфіденційність є нашим головним пріоритетом. ');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (7, 1, 'How do I apply a discount code to my order?',
        'We are excited to announce that the feature to apply discount codes to your order is currently under preparation. We understand the importance of providing our customers with opportunities to save, and we are working diligently to implement this feature. We anticipate that it <span style="font-weight: bold;">will be available by summer.</span>

Once the discount code feature is live, you will be able to enter your code during the checkout process. The discount will be applied to your total purchase amount, allowing you to enjoy savings on your favorite gadgets. We will provide detailed instructions on how to use this feature once it is available.

In the meantime, keep an eye on our website and newsletter for updates on this and other exciting features. We appreciate your patience and look forward to offering you even more value in the near future. ');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (7, 2, 'Як застосувати промокод до мого замовлення? ',
        'Ми раді повідомити, що функція застосування промокодів до вашого замовлення наразі готується. Ми розуміємо важливість надання нашим клієнтам можливостей для заощадження, і <span style="font-weight: bold;">ми наполегливо працюємо над впровадженням цієї функції. Ми очікуємо, що вона буде доступна до літа.</span>

Як тільки функція промокодів буде запущена, ви зможете ввести свій код під час процесу оформлення замовлення. Знижка буде застосована до загальної суми вашої покупки, дозволяючи вам насолоджуватися заощадженнями на ваших улюблених гаджетах. Ми надамо детальні інструкції щодо використання цієї функції, як тільки вона стане доступною.

Тим часом слідкуйте за оновленнями на нашому веб-сайті та в нашій розсилці для отримання інформації про цю та інші захоплюючі функції. Ми цінуємо ваше терпіння і з нетерпінням чекаємо можливості запропонувати вам ще більше цінності в найближчому майбутньому.');


INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (8, 1, 'How do I leave a review for a product I purchased? ',
        'We are currently working on implementing a feature that will allow you to leave reviews for products you have purchased. Customer feedback is incredibly important to us, as it helps us improve our products and services. <span style="font-weight: bold;">We expect this feature to be available by summer.</span>

Once the review feature is live, you will be able to share your experiences and opinions about the gadgets you have purchased. This will not only help other customers make informed decisions but also provide valuable insights to our team. We will provide clear instructions on how to leave a review once the feature is available.

In the meantime, we encourage you to reach out to us directly with any feedback or suggestions. Your input is invaluable, and we are committed to continuously improving our offerings based on your experiences. ');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (8, 2, 'Як залишити відгук про придбаний товар?',
        'Ми наразі працюємо над впровадженням функції, яка дозволить вам залишати відгуки про придбані товари. Відгуки клієнтів надзвичайно важливі для нас, оскільки вони допомагають нам покращувати наші продукти та послуги. Ми очікуємо, що <span style="font-weight: bold;">ця функція буде доступна до літа.</span>

Як тільки функція відгуків стане доступною, ви зможете ділитися своїми враженнями та думками про придбані гаджети. Це не лише допоможе іншим клієнтам приймати обґрунтовані рішення, але й надасть цінні інсайти нашій команді. Ми надамо чіткі інструкції щодо залишення відгуків, як тільки ця функція стане доступною.

Тим часом ми заохочуємо вас безпосередньо звертатися до нас з будь-якими відгуками або пропозиціями. Ваші відгуки безцінні, і ми прагнемо постійно покращувати наші пропозиції на основі вашого досвіду. ');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (9, 1, 'Do you offer gift cards?',
        'We are excited to announce that we are currently preparing to offer gift cards as a convenient option for our customers. Gift cards make for perfect presents, allowing recipients to choose their favorite gadgets from our store. <span style="font-weight: bold;">We anticipate that this feature will be available by summer.</span>

Once the gift card feature is live, you will be able to purchase and redeem gift cards directly on our website. Gift cards will be available in various denominations, making them suitable for any occasion. We will provide detailed information on how to buy and use gift cards once the feature is launched.

In the meantime, stay tuned for updates on this and other exciting features. We are committed to enhancing your shopping experience and look forward to offering you even more flexibility and convenience soon. ');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (9, 2, 'Чи пропонуєте ви подарункові картки? ',
        'Ми раді повідомити, що наразі готуємося запропонувати подарункові картки як зручний варіант для наших клієнтів. Подарункові картки є ідеальним подарунком, дозволяючи отримувачам вибирати свої улюблені гаджети з нашого магазину. Ми очікуємо, що <span style="font-weight: bold;">ця функція буде доступна до літа.</span>

Як тільки функція подарункових карток стане доступною, ви зможете купувати та використовувати подарункові картки безпосередньо на нашому веб-сайті. Подарункові картки будуть доступні в різних номіналах, що робить їх підходящими для будь-якої нагоди. Ми надамо детальну інформацію про те, як купувати та використовувати подарункові картки, як тільки ця функція буде запущена.

Тим часом слідкуйте за оновленнями щодо цієї та інших захоплюючих функцій. Ми прагнемо покращити ваш досвід покупок і з нетерпінням чекаємо можливості запропонувати вам ще більше гнучкості та зручності найближчим часом. ');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (10, 1, 'What should I do if I receive a damaged or defective item?',
        'If you receive a damaged or defective item, we are here to help. The first step is to contact us via email with a detailed explanation of the issue. Please include photos of the damaged or defective item, as this will help us assess the situation and provide a prompt resolution.

Once we receive your email and photos, our team will review the information and determine the best course of action. This may include sending a replacement item or issuing a refund. We aim to resolve such issues quickly and efficiently to ensure your satisfaction.

We understand how frustrating it can be to receive a damaged or defective product, and we are committed to making things right. Your satisfaction is our top priority, and we will work diligently to address any issues and provide a positive shopping experience. ');

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES (10, 2, 'Що робити, якщо я отримав пошкоджений або дефектний товар?',
        'Якщо ви отримали пошкоджений або дефектний товар, ми готові допомогти. Першим кроком є звернення до нас електронною поштою з детальним описом проблеми. Будь ласка, додайте фотографії пошкодженого або дефектного товару, оскільки це допоможе нам оцінити ситуацію та надати швидке вирішення.

Після отримання вашого електронного листа та фотографій наша команда перегляне інформацію та визначить найкращий спосіб дій. Це може включати відправку замінного товару або повернення коштів. Ми прагнемо швидко та ефективно вирішувати такі питання, щоб забезпечити вашу задоволеність.

Ми розуміємо, наскільки розчаровуючим може бути отримання пошкодженого або дефектного продукту, і ми прагнемо виправити ситуацію. Ваше задоволення є нашим головним пріоритетом, і ми будемо наполегливо працювати, щоб вирішити будь-які проблеми та забезпечити позитивний досвід покупок. ');
