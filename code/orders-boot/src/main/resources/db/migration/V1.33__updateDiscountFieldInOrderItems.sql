UPDATE products p
SET image_link = 'https://th.bing.com/th/id/OIP.MyiS08WBZ-Jf-3VuH9sX7AHaHa?w=1200&h=1200&rs=1&pid=ImgDetMain'
FROM products_tags pt
WHERE p.id = pt.product_id
  AND pt.tag_id = 1;

UPDATE products p
SET image_link = 'https://i5.walmartimages.com/asr/cf418f8a-8a33-40af-b944-94f858cc37ff.e80e55ae32e500e2df8c3611f2e10130.jpeg'
FROM products_tags pt
WHERE p.id = pt.product_id
    AND pt.tag_id = 2;

UPDATE products p
SET image_link = 'https://th.bing.com/th/id/OIP.p53peXcbhczyek1fnO3m0gHaHa?rs=1&pid=ImgDetMain'
FROM products_tags pt
WHERE p.id = pt.product_id
  AND pt.tag_id = 3;