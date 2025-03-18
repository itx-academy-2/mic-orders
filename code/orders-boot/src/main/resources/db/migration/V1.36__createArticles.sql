CREATE TABLE articles
(
    id         SERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE article_contents
(
    article_id  BIGINT NOT NULL,
    language_id BIGINT NOT NULL,
    title       VARCHAR(255),
    content     TEXT,
    PRIMARY KEY (article_id, language_id),
    CONSTRAINT fk_article FOREIGN KEY (article_id) REFERENCES articles (id),
    CONSTRAINT fk_language FOREIGN KEY (language_id) REFERENCES languages (id)
);