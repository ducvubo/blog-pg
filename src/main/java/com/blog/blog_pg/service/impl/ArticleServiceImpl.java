package com.blog.blog_pg.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.blog.blog_pg.dto.request.article.*;
import com.blog.blog_pg.dto.response.MetaPagination;
import com.blog.blog_pg.dto.response.ResPagination;
import com.blog.blog_pg.dto.response.article.ArticleDTO;
import com.blog.blog_pg.dto.response.article.ArticleName;
import com.blog.blog_pg.entities.ArticleEntity;
import com.blog.blog_pg.entities.CategoryEntity;
import com.blog.blog_pg.enums.ArticleStatus;
import com.blog.blog_pg.enums.ArticleType;
import com.blog.blog_pg.exception.BadRequestError;
import com.blog.blog_pg.middleware.Account;
import com.blog.blog_pg.repository.ArticleRepository;
import com.blog.blog_pg.repository.CategoryRepository;
import com.blog.blog_pg.service.ArticleService;
import com.blog.blog_pg.utils.AccountUtils;
import com.blog.blog_pg.utils.ElasticsearchUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    private static final String ARTICLE_ELASTICSEARCH_INDEX = "article-blog-pg";

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private ElasticsearchUtils elasticsearchUtils;

    @Value("${sync.elasticsearch.enabled:false}")
    private boolean syncElasticsearchEnabled;

    @EventListener(ApplicationReadyEvent.class)
    public void syncDataToElasticsearchOnStartup() {
        if (!syncElasticsearchEnabled) {
            log.info("Elasticsearch sync is disabled");
            return;
        }

        try {
            log.info("Starting data sync to Elasticsearch for articles...");
            List<ArticleEntity> articles = articleRepository.findAll();
            boolean indexExists = elasticsearchUtils.indexElasticsearchExists(ARTICLE_ELASTICSEARCH_INDEX);
            if (indexExists) {
                elasticsearchUtils.deleteAllDocByElasticsearch(ARTICLE_ELASTICSEARCH_INDEX);
            }
            for (ArticleEntity article : articles) {
                elasticsearchUtils.addDocToElasticsearch(ARTICLE_ELASTICSEARCH_INDEX, article.getAtlId().toString(), article);
            }
            log.info("Data sync Article to Elasticsearch completed successfully");
        } catch (Exception e) {
            log.error("Failed to sync articles to Elasticsearch: ", e);
            throw new RuntimeException("Elasticsearch sync failed", e);
        }
    }

    @Override
    public Boolean checkSlug(String slug, Account account) {
        try{
            return articleRepository.existsByAtlSlugAndAtlResId(slug, account.getAccountRestaurantId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity createArticleDefault(CreateArticleDefaultDto createArticleDefaultDto, Account account) {
        try {
            Boolean slugExist = articleRepository.existsByAtlSlugAndAtlResId(createArticleDefaultDto.getAtlSlug(), account.getAccountRestaurantId());
            if (slugExist) {
                throw new BadRequestError("Slug đã tồn tại");
            }

            Optional<CategoryEntity> categoryEntity = categoryRepository.findById(UUID.fromString(createArticleDefaultDto.getCatId()));
            if (categoryEntity.isEmpty()) {
                throw new BadRequestError("Danh mục không tồn tại");
            }

            String content = objectMapper.writeValueAsString(createArticleDefaultDto.getArticleDefaultModel());

            ArticleEntity articleEntity = ArticleEntity.builder()
                    .atlTitle(createArticleDefaultDto.getAtlTitle())
                    .atlContent(content)
                    .catId(createArticleDefaultDto.getCatId())
                    .atlSlug(createArticleDefaultDto.getAtlSlug())
                    .atlResId(account.getAccountRestaurantId())
                    .atlType(ArticleType.DEFAULT)
                    .atlStatus(ArticleStatus.DRAFT)
                    .atlDescription(createArticleDefaultDto.getAtlDescription())
                    .atlImage(createArticleDefaultDto.getAtlImage())
                    .listArticleNote(createArticleDefaultDto.getListArticleNote())
                    .listArticleRelated(createArticleDefaultDto.getListArticleRelated())
                    .build();

            articleRepository.save(articleEntity);

            return articleEntity;
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity createArticleVideo(CreateArticleVideoDto createArticleVideoDto, Account account) {
        try{
        Boolean slugExist = articleRepository.existsByAtlSlugAndAtlResId(createArticleVideoDto.getAtlSlug(), account.getAccountRestaurantId());
        if (slugExist) {
            throw new BadRequestError("Slug đã tồn tại");
        }

        Optional<CategoryEntity> categoryEntity = categoryRepository.findById(UUID.fromString(createArticleVideoDto.getCatId()));
        if (categoryEntity.isEmpty()) {
            throw new BadRequestError("Danh mục không tồn tại");
        }

        String content = objectMapper.writeValueAsString(createArticleVideoDto.getArticleVideoModel());

        ArticleEntity articleEntity = ArticleEntity.builder()
                .atlTitle(createArticleVideoDto.getAtlTitle())
                .atlContent(content)
                .catId(createArticleVideoDto.getCatId())
                .atlSlug(createArticleVideoDto.getAtlSlug())
                .atlResId(account.getAccountRestaurantId())
                .atlType(ArticleType.VIDEO)
                .atlStatus(ArticleStatus.DRAFT)
                .atlDescription(createArticleVideoDto.getAtlDescription())
                .atlImage(createArticleVideoDto.getAtlImage())
                .listArticleNote(createArticleVideoDto.getListArticleNote())
                .listArticleRelated(createArticleVideoDto.getListArticleRelated())
                .build();

        articleRepository.save(articleEntity);

        return articleEntity;
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity createArticleDocument(CreateArticleDocumentDto createArticleDocumentDto, Account account) {
        try{
            Boolean slugExist = articleRepository.existsByAtlSlugAndAtlResId(createArticleDocumentDto.getAtlSlug(), account.getAccountRestaurantId());
            if (slugExist) {
                throw new BadRequestError("Slug đã tồn tại");
            }

            Optional<CategoryEntity> categoryEntity = categoryRepository.findById(UUID.fromString(createArticleDocumentDto.getCatId()));
            if (categoryEntity.isEmpty()) {
                throw new BadRequestError("Danh mục không tồn tại");
            }

            String content = objectMapper.writeValueAsString(createArticleDocumentDto.getArticleDocumentModel());

            ArticleEntity articleEntity = ArticleEntity.builder()
                    .atlTitle(createArticleDocumentDto.getAtlTitle())
                    .atlContent(content)
                    .catId(createArticleDocumentDto.getCatId())
                    .atlSlug(createArticleDocumentDto.getAtlSlug())
                    .atlResId(account.getAccountRestaurantId())
                    .atlType(ArticleType.DOCUMENT)
                    .atlStatus(ArticleStatus.DRAFT)
                    .atlDescription(createArticleDocumentDto.getAtlDescription())
                    .atlImage(createArticleDocumentDto.getAtlImage())
                    .listArticleNote(createArticleDocumentDto.getListArticleNote())
                    .build();

            articleRepository.save(articleEntity);

            return articleEntity;
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity createArticleImage(CreateArticleImageDto createArticleImage, Account account) {
        try{
            Boolean slugExist = articleRepository.existsByAtlSlugAndAtlResId(createArticleImage.getAtlSlug(), account.getAccountRestaurantId());
            if (slugExist) {
                throw new BadRequestError("Slug đã tồn tại");
            }

            Optional<CategoryEntity> categoryEntity = categoryRepository.findById(UUID.fromString(createArticleImage.getCatId()));
            if (categoryEntity.isEmpty()) {
                throw new BadRequestError("Danh mục không tồn tại");
            }

            String content = objectMapper.writeValueAsString(createArticleImage.getListArticleImage());

            ArticleEntity articleEntity = ArticleEntity.builder()
                    .atlTitle(createArticleImage.getAtlTitle())
                    .atlContent(content)
                    .catId(createArticleImage.getCatId())
                    .atlSlug(createArticleImage.getAtlSlug())
                    .atlResId(account.getAccountRestaurantId())
                    .atlType(ArticleType.IMAGE)
                    .atlStatus(ArticleStatus.DRAFT)
                    .atlDescription(createArticleImage.getAtlDescription())
                    .atlImage(createArticleImage.getAtlImage())
                    .listArticleNote(createArticleImage.getListArticleNote())
                    .listArticleRelated(createArticleImage.getListArticleRelated())
                    .createdBy(AccountUtils.convertAccountToJson(account))
                    .build();

            articleRepository.save(articleEntity);

            return articleEntity;
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity updateArticleDefault(UpdateArticleDefaultDto updateArticleDefaultDto, Account account) {
        try{


            Optional<CategoryEntity> categoryEntity = categoryRepository.findById(UUID.fromString(updateArticleDefaultDto.getCatId()));
            if (categoryEntity.isEmpty()) {
                throw new BadRequestError("Danh mục không tồn tại");
            }

            Optional<ArticleEntity> articleExists = articleRepository.findById(UUID.fromString(updateArticleDefaultDto.getAtlId()));
            if (articleExists.isEmpty()) {
                throw new BadRequestError("Bài viết không tồn tại");
            }
            List<ArticleEntity> slugExist = articleRepository.findByAtlSlug(updateArticleDefaultDto.getAtlSlug(), account.getAccountRestaurantId());
            if (!slugExist.isEmpty()) {
                for (ArticleEntity articleEntity : slugExist) {
                    if (!articleEntity.getAtlId().equals(UUID.fromString(updateArticleDefaultDto.getAtlId()))) {
                        throw new BadRequestError("Slug đã tồn tại");
                    }
                }
            }
            String content = objectMapper.writeValueAsString(updateArticleDefaultDto.getArticleDefaultModel());

            ArticleEntity articleEntity = articleExists.get();
            articleEntity.setAtlTitle(updateArticleDefaultDto.getAtlTitle());
            articleEntity.setAtlContent(content);
            articleEntity.setCatId(updateArticleDefaultDto.getCatId());
            articleEntity.setAtlSlug(updateArticleDefaultDto.getAtlSlug());
            articleEntity.setAtlResId(account.getAccountRestaurantId());
            articleEntity.setAtlDescription(updateArticleDefaultDto.getAtlDescription());
            articleEntity.setAtlImage(updateArticleDefaultDto.getAtlImage());
            articleEntity.setListArticleNote(updateArticleDefaultDto.getListArticleNote());
            articleEntity.setListArticleRelated(updateArticleDefaultDto.getListArticleRelated());
            articleEntity.setUpdatedBy(AccountUtils.convertAccountToJson(account));

            articleRepository.save(articleEntity);
            return articleEntity;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity updateArticleVideo(UpdateArticleVideoDto updateArticleVideoDto, Account account) {
        try{


            Optional<CategoryEntity> categoryEntity = categoryRepository.findById(UUID.fromString(updateArticleVideoDto.getCatId()));
            if (categoryEntity.isEmpty()) {
                throw new BadRequestError("Danh mục không tồn tại");
            }

            Optional<ArticleEntity> articleExists = articleRepository.findById(UUID.fromString(updateArticleVideoDto.getAtlId()));
            if (articleExists.isEmpty()) {
                throw new BadRequestError("Bài viết không tồn tại");
            }

            List<ArticleEntity> slugExist = articleRepository.findByAtlSlug(updateArticleVideoDto.getAtlSlug(), account.getAccountRestaurantId());
            if (!slugExist.isEmpty()) {
                for (ArticleEntity articleEntity : slugExist) {
                    if (!articleEntity.getAtlId().equals(UUID.fromString(updateArticleVideoDto.getAtlId()))) {
                        throw new BadRequestError("Slug đã tồn tại");
                    }
                }
            }

            String content = objectMapper.writeValueAsString(updateArticleVideoDto.getArticleVideoModel());

            ArticleEntity articleEntity = articleExists.get();
            articleEntity.setAtlTitle(updateArticleVideoDto.getAtlTitle());
            articleEntity.setAtlContent(content);
            articleEntity.setCatId(updateArticleVideoDto.getCatId());
            articleEntity.setAtlSlug(updateArticleVideoDto.getAtlSlug());
            articleEntity.setAtlResId(account.getAccountRestaurantId());
            articleEntity.setAtlDescription(updateArticleVideoDto.getAtlDescription());
            articleEntity.setAtlImage(updateArticleVideoDto.getAtlImage());
            articleEntity.setListArticleNote(updateArticleVideoDto.getListArticleNote());
            articleEntity.setListArticleRelated(updateArticleVideoDto.getListArticleRelated());
            articleEntity.setUpdatedBy(AccountUtils.convertAccountToJson(account));

            articleRepository.save(articleEntity);
            return articleEntity;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity updateArticleDocument(UpdateArticleDocumentDto updateArticleDocumentDto, Account account) {
         try{
            Boolean slugExist = articleRepository.existsByAtlSlugAndAtlResId(updateArticleDocumentDto.getAtlSlug(), account.getAccountRestaurantId());
            if (slugExist) {
                throw new BadRequestError("Slug đã tồn tại");
            }

            Optional<CategoryEntity> categoryEntity = categoryRepository.findById(UUID.fromString(updateArticleDocumentDto.getCatId()));
            if (categoryEntity.isEmpty()) {
                throw new BadRequestError("Danh mục không tồn tại");
            }

            Optional<ArticleEntity> articleExists = articleRepository.findById(UUID.fromString(updateArticleDocumentDto.getAtlId()));
            if (articleExists.isEmpty()) {
                throw new BadRequestError("Bài viết không tồn tại");
            }

            String content = objectMapper.writeValueAsString(updateArticleDocumentDto.getArticleDocumentModel());

            ArticleEntity articleEntity = articleExists.get();
            articleEntity.setAtlTitle(updateArticleDocumentDto.getAtlTitle());
            articleEntity.setAtlContent(content);
             articleEntity.setCatId(updateArticleDocumentDto.getCatId());
            articleEntity.setAtlSlug(updateArticleDocumentDto.getAtlSlug());
            articleEntity.setAtlResId(account.getAccountRestaurantId());
            articleEntity.setAtlType(ArticleType.DEFAULT);
            articleEntity.setAtlStatus(ArticleStatus.DRAFT);
            articleEntity.setAtlDescription(updateArticleDocumentDto.getAtlDescription());
            articleEntity.setAtlImage(updateArticleDocumentDto.getAtlImage());
            articleEntity.setListArticleNote(updateArticleDocumentDto.getListArticleNote());
            articleEntity.setUpdatedBy(AccountUtils.convertAccountToJson(account));

            articleRepository.save(articleEntity);
            return articleEntity;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity updateArticleImage(UpdateArticleImageDto updateArticleImage, Account account) {
         try{


            Optional<CategoryEntity> categoryEntity = categoryRepository.findById(UUID.fromString(updateArticleImage.getCatId()));
            if (categoryEntity.isEmpty()) {
                throw new BadRequestError("Danh mục không tồn tại");
            }

            Optional<ArticleEntity> articleExists = articleRepository.findById(UUID.fromString(updateArticleImage.getAtlId()));
            if (articleExists.isEmpty()) {
                throw new BadRequestError("Bài viết không tồn tại");
            }

             List<ArticleEntity> slugExist = articleRepository.findByAtlSlug(updateArticleImage.getAtlSlug(), account.getAccountRestaurantId());
                if (!slugExist.isEmpty()) {
                    for (ArticleEntity articleEntity : slugExist) {
                        if (!articleEntity.getAtlId().equals(UUID.fromString(updateArticleImage.getAtlId()))) {
                            throw new BadRequestError("Slug đã tồn tại");
                        }
                    }
                }

            String content = objectMapper.writeValueAsString(updateArticleImage.getListArticleImage());

            ArticleEntity articleEntity = articleExists.get();
            articleEntity.setAtlTitle(updateArticleImage.getAtlTitle());
            articleEntity.setAtlContent(content);
             articleEntity.setCatId(updateArticleImage.getCatId());
            articleEntity.setAtlSlug(updateArticleImage.getAtlSlug());
            articleEntity.setAtlResId(account.getAccountRestaurantId());
            articleEntity.setAtlDescription(updateArticleImage.getAtlDescription());
            articleEntity.setAtlImage(updateArticleImage.getAtlImage());
            articleEntity.setListArticleNote(updateArticleImage.getListArticleNote());
             articleEntity.setListArticleRelated(updateArticleImage.getListArticleRelated());
            articleEntity.setUpdatedBy(AccountUtils.convertAccountToJson(account));

            articleRepository.save(articleEntity);
            return articleEntity;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity sendArticle(String id, Account account) {
        try {
            Optional<ArticleEntity> articleEntity = articleRepository.findById(UUID.fromString(id));
            if (articleEntity.isEmpty()) {
                throw new BadRequestError("Bài viết không tồn tại");
            }
            articleEntity.get().setAtlStatus(ArticleStatus.PENDING_APPROVAL);
            articleEntity.get().setUpdatedBy(AccountUtils.convertAccountToJson(account));
            articleRepository.save(articleEntity.get());
            return articleEntity.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity approveArticle(String id, Account account) {
        try {
            Optional<ArticleEntity> articleEntity = articleRepository.findById(UUID.fromString(id));
            if (articleEntity.isEmpty()) {
                throw new BadRequestError("Bài viết không tồn tại");
            }
            articleEntity.get().setAtlStatus(ArticleStatus.PENDING_PUBLISH);
            articleEntity.get().setUpdatedBy(AccountUtils.convertAccountToJson(account));
            articleRepository.save(articleEntity.get());
            return articleEntity.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity rejectArticle(String id, Account account) {
        try {
            Optional<ArticleEntity> articleEntity = articleRepository.findById(UUID.fromString(id));
            if (articleEntity.isEmpty()) {
                throw new BadRequestError("Bài viết không tồn tại");
            }
            articleEntity.get().setAtlStatus(ArticleStatus.REJECTED);
            articleEntity.get().setUpdatedBy(AccountUtils.convertAccountToJson(account));
            articleRepository.save(articleEntity.get());
            return articleEntity.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity publishArticle(String id, Account account) {
        try {
            Optional<ArticleEntity> articleEntity = articleRepository.findById(UUID.fromString(id));
            if (articleEntity.isEmpty()) {
                throw new BadRequestError("Bài viết không tồn tại");
            }
            articleEntity.get().setAtlStatus(ArticleStatus.PUBLISHED);
            articleEntity.get().setAtlPublishedTime(new Date(System.currentTimeMillis()));
            articleEntity.get().setUpdatedBy(AccountUtils.convertAccountToJson(account));
            articleRepository.save(articleEntity.get());
            return articleEntity.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity publishScheduleArticle(String id, Account account,String scheduleTime) {
        try {
            Optional<ArticleEntity> articleEntity = articleRepository.findById(UUID.fromString(id));
            if (articleEntity.isEmpty()) {
                throw new BadRequestError("Bài viết không tồn tại");
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date scheduleDate = formatter.parse(scheduleTime);

            articleEntity.get().setAtlStatus(ArticleStatus.PUBLISH_SCHEDULE);
            articleEntity.get().setAtlPublishedSchedule(scheduleDate);
            articleEntity.get().setUpdatedBy(AccountUtils.convertAccountToJson(account));
            articleRepository.save(articleEntity.get());
            return articleEntity.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity unpublishScheduleArticle(String id, Account account) {
        try {
            Optional<ArticleEntity> articleEntity = articleRepository.findById(UUID.fromString(id));
            if (articleEntity.isEmpty()) {
                throw new BadRequestError("Bài viết không tồn tại");
            }
            articleEntity.get().setAtlStatus(ArticleStatus.PENDING_PUBLISH);
            articleEntity.get().setAtlPublishedSchedule(null);
            articleEntity.get().setUpdatedBy(AccountUtils.convertAccountToJson(account));
            articleRepository.save(articleEntity.get());
            return articleEntity.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity unpublishArticle(String id, Account account) {
        try {
            Optional<ArticleEntity> articleEntity = articleRepository.findById(UUID.fromString(id));
            if (articleEntity.isEmpty()) {
                throw new BadRequestError("Bài viết không tồn tại");
            }
            articleEntity.get().setAtlStatus(ArticleStatus.UNPUBLISHED);
            articleEntity.get().setUpdatedBy(AccountUtils.convertAccountToJson(account));
            articleRepository.save(articleEntity.get());
            return articleEntity.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity deleteArticleDraft(String id, Account account) {
        try {
            Optional<ArticleEntity> articleEntity = articleRepository.findById(UUID.fromString(id));
            if (articleEntity.isEmpty()) {
                throw new BadRequestError("Bài viết không tồn tại");
            }

            if(articleEntity.get().getAtlStatus() == ArticleStatus.DRAFT || articleEntity.get().getAtlStatus() == ArticleStatus.REJECTED){
                articleEntity.get().setIsDeleted(1);
                articleEntity.get().setDeletedBy(AccountUtils.convertAccountToJson(account));
                articleEntity.get().setDeletedAt(new Date(System.currentTimeMillis()));
                articleRepository.save(articleEntity.get());
                return articleEntity.get();
            }else {
                throw new BadRequestError("Article is not draft or rejected");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity restoreArticle(String id, Account account) {
        try {
            Optional<ArticleEntity> articleEntity = articleRepository.findById(UUID.fromString(id));
            if (articleEntity.isEmpty()) {
                throw new BadRequestError("Bài viết không tồn tại");
            }

            if (articleEntity.get().getIsDeleted() == 0) {
                throw new BadRequestError("Article is not deleted");
            }

            articleEntity.get().setIsDeleted(0);
            articleEntity.get().setDeletedBy(null);
            articleEntity.get().setDeletedAt(null);
            articleRepository.save(articleEntity.get());
            return articleEntity.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleEntity getArticle(String id, Account account) {
        try {
            Optional<ArticleEntity> articleEntity = articleRepository.findById(UUID.fromString(id));
            if (articleEntity.isEmpty()) {
                throw new BadRequestError("Bài viết không tồn tại");
            }
            return articleEntity.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResPagination<ArticleEntity> getAllArticle(int pageIndex, int pageSize, String atlTitle, int isDeleted,Account account) {
        try {
            Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
            Page<ArticleEntity> articleEntities = articleRepository.findByFilters(atlTitle, account.getAccountRestaurantId(), isDeleted, pageable);
            return ResPagination.<ArticleEntity>builder()
                    .result(articleEntities.getContent())
                    .meta(MetaPagination.builder()
                            .current(pageIndex)
                            .pageSize(pageSize)
                            .totalPage(articleEntities.getTotalPages())
                            .totalItem(articleEntities.getTotalElements())
                            .build())
                    .build();
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ArticleName> getAllArticleByRestaurantId(Account account) {
        try {
            return articleRepository.findByAtlResId(account.getAccountRestaurantId(),0);
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ArticleDTO> getArticleAllView(String atlResId, String atlCatId) {
        try {
            var searchResponse = elasticsearchClient.search(s -> s
                            .index("article-blog-pg")
                            .source(src -> src
                                    .filter(f -> f.includes("atlId", "atlTitle", "atlSlug"))
                            )
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("atlResId.keyword")
                                                            .value(atlResId)))
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("catId.keyword")
                                                            .value(atlCatId)))
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("atlStatus.keyword")
                                                            .value("PUBLISHED")))
                                    )
                            ),
                    ArticleDTO.class
            );

            return searchResponse.hits().hits().stream()
                    .map(hit -> hit.source())
                    .filter(Objects::nonNull)
                    .toList();

        } catch (Exception e) {
            log.error("Error retrieving articles from Elasticsearch: ", e);
            throw new RuntimeException("Failed to fetch articles from Elasticsearch", e);
        }
    }

}
