UPDATE products
SET discount_id = null
WHERE discount_id IN (
                      '505523c1-5801-4ea0-acc2-c51975361836',
                      '5d35f441-fc93-4ca2-8328-659653063dc0',
                      '6c136dcd-5c82-49c1-91e1-3e0c7bbd7fd8',
                      '22a8204b-0c33-40c3-b250-7074ad5c9e2e',
                      'efa322d3-cca1-4cb7-b070-e0336fb08df9',
                      '00a573cc-8ac0-4bbe-a28e-d8a98343a997',
                      '6b3ecc06-8ea6-47fa-b4c8-8590552bfbc2',
                      '99e1d8a5-c4c3-4573-9b3a-3bd7ff58f6d6',
                      'f965eb6b-f238-46f1-a20b-42e91f005e48',
                      '98bb7688-3f11-4421-83b6-bc25b95b5b75',
                      '3a4fe8c4-ce9c-4340-aec7-0cb87b4f9613'
    );

DELETE
FROM discounts
WHERE id IN (
                      '505523c1-5801-4ea0-acc2-c51975361836',
                      '5d35f441-fc93-4ca2-8328-659653063dc0',
                      '6c136dcd-5c82-49c1-91e1-3e0c7bbd7fd8',
                      '22a8204b-0c33-40c3-b250-7074ad5c9e2e',
                      'efa322d3-cca1-4cb7-b070-e0336fb08df9',
                      '00a573cc-8ac0-4bbe-a28e-d8a98343a997',
                      '6b3ecc06-8ea6-47fa-b4c8-8590552bfbc2',
                      '99e1d8a5-c4c3-4573-9b3a-3bd7ff58f6d6',
                      'f965eb6b-f238-46f1-a20b-42e91f005e48',
                      '98bb7688-3f11-4421-83b6-bc25b95b5b75',
                      '3a4fe8c4-ce9c-4340-aec7-0cb87b4f9613'
    );