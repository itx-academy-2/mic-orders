INSERT INTO articles (id, created_at, updated_at)
VALUES (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SELECT setval('articles_id_seq', (SELECT MAX(id) FROM articles));

INSERT INTO article_contents (article_id, language_id, title, content)
VALUES
    (1, 1, 'What payment methods do you accept?',
     'We strive to make your shopping experience as convenient as possible by accepting a variety of payment methods. At our Gadget Store, you can pay using major credit and debit cards, including Visa, MasterCard, and American Express. This ensures that you can complete your purchase securely and efficiently.

 In addition to credit and debit cards, we also accept payments through PayPal. PayPal offers a quick and secure way to pay for your orders, providing an extra layer of protection for your financial information. Simply select PayPal at checkout, log in to your account, and confirm your payment.

 For those who prefer other payment methods, we are continuously working to expand our options to better serve our customers. Stay tuned for updates as we introduce new ways to pay, ensuring that your shopping experience remains seamless and enjoyable.');

-- Inserting Ukrainian content into article_contents table
INSERT INTO article_contents (article_id, language_id, title, content)
VALUES
    (1, 2, 'Які способи оплати ви приймаєте?',
     'Ми прагнемо зробити ваш досвід покупок якомога зручнішим, приймаючи різноманітні способи оплати. У нашому магазині гаджетів ви можете оплатити за допомогою основних кредитних та дебетових карток, включаючи Visa, MasterCard та American Express. Це гарантує, що ви зможете безпечно та ефективно завершити свою покупку.

 Крім кредитних та дебетових карток, ми також приймаємо платежі через PayPal. PayPal пропонує швидкий та безпечний спосіб оплати ваших замовлень, надаючи додатковий рівень захисту вашої фінансової інформації. Просто виберіть PayPal при оформленні замовлення, увійдіть у свій обліковий запис і підтвердьте оплату.

 Для тих, хто віддає перевагу іншим способам оплати, ми постійно працюємо над розширенням наших можливостей, щоб краще обслуговувати наших клієнтів. Слідкуйте за оновленнями, оскільки ми впроваджуємо нові способи оплати, забезпечуючи, щоб ваш досвід покупок залишався безперешкодним та приємним.');
