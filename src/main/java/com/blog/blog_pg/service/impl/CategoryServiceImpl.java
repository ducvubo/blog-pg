package com.blog.blog_pg.service.impl;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.blog.blog_pg.dto.request.category.CreateCategoryDto;
import com.blog.blog_pg.dto.request.category.UpdateCategoryDto;
import com.blog.blog_pg.dto.request.category.UpdateStatusCategoryDto;
import com.blog.blog_pg.dto.response.MetaPagination;
import com.blog.blog_pg.dto.response.ResPagination;
import com.blog.blog_pg.dto.response.category.CategoryDTO;
import com.blog.blog_pg.dto.response.category.CategoryName;
import com.blog.blog_pg.entities.CategoryEntity;
import com.blog.blog_pg.entities.LabelEntity;
import com.blog.blog_pg.enums.EnumStatus;
import com.blog.blog_pg.exception.BadRequestError;
import com.blog.blog_pg.listener.CategoryEntityListener;
import com.blog.blog_pg.middleware.Account;
import com.blog.blog_pg.models.CreateNotification;
import com.blog.blog_pg.repository.CategoryRepository;
import com.blog.blog_pg.service.CategoryService;
import com.blog.blog_pg.utils.AccountUtils;
import com.blog.blog_pg.utils.ElasticsearchUtils;
import com.blog.blog_pg.utils.Slug;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityListeners;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.*;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private static final String CATEGORY_BLOG_PG_ELASTICSEARCH_INDEX = "category-blog-pg";

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ElasticsearchUtils elasticsearchUtils;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Value("${sync.elasticsearch.enabled:false}")
    private boolean syncElasticsearchEnabled;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @EventListener(ApplicationReadyEvent.class)
    public void syncDataToElasticsearchOnStartup() {
        if (!syncElasticsearchEnabled) {
            log.info("Elasticsearch sync is disabled");
            return;
        }

        try {
            log.info("Starting data sync to Elasticsearch...");
            List<CategoryEntity> categoryEntities = categoryRepository.findAll();

            // Kiểm tra xem index đã tồn tại chưa
            boolean indexExists = elasticsearchUtils.indexElasticsearchExists(CATEGORY_BLOG_PG_ELASTICSEARCH_INDEX);
            if (indexExists) {
                elasticsearchUtils.deleteAllDocByElasticsearch(CATEGORY_BLOG_PG_ELASTICSEARCH_INDEX);
            }

            // Đồng bộ tất cả labels
            for (CategoryEntity category : categoryEntities) {
                elasticsearchUtils.addDocToElasticsearch(CATEGORY_BLOG_PG_ELASTICSEARCH_INDEX, category.getCatId().toString(), category);
            }
            log.info("Data sync Category to Elasticsearch completed successfully");
        } catch (Exception e) {
            log.error("Failed to sync data to Elasticsearch: ", e);
            throw new RuntimeException("Elasticsearch sync failed", e);
        }
    }

    @Override
    public List<CategoryDTO> getCategoryAllView(String catResId) {
        try {

            var searchResponse = elasticsearchClient.search(s -> s
                            .index(CATEGORY_BLOG_PG_ELASTICSEARCH_INDEX)
                            .source(src -> src
                                    .filter(f -> f
                                            .includes("catName", "catSlug", "catId") // Chỉ lấy 3 field này
                                    )
                            )
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("catResId.keyword")
                                                            .value(catResId)))
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("isDeleted")
                                                            .value(0)))
                                            .must(m -> m
                                                    .match(t -> t
                                                            .field("catStatus.keyword")
                                                            .query("ENABLED")))

                                    )
                            ),
                    CategoryDTO.class
            );

            return searchResponse.hits().hits().stream()
                    .map(hit -> hit.source())
                    .filter(Objects::nonNull)
                    .toList();

        } catch (Exception e) {
            log.error("Error retrieving categories from Elasticsearch: ", e);
            throw new RuntimeException("Failed to fetch categories from Elasticsearch", e);
        }
    }


    @Override
    public CategoryEntity createCategory(CreateCategoryDto createCategoryDto, Account account) {
        try{
            Random random = new Random();
            int number = 100000 + random.nextInt(900000);
            String slug = Slug.createSlug(createCategoryDto.getCatName()) + "-" + number;
            CategoryEntity categoryEntity = CategoryEntity.builder()
                    .catName(createCategoryDto.getCatName())
                    .catDescription(createCategoryDto.getCatDescription())
                    .catSlug(slug)
                    .catOrder(createCategoryDto.getCatOrder())
                    .catResId(account.getAccountRestaurantId())
                    .catStatus(EnumStatus.ENABLED)
                    .createdBy(AccountUtils.convertAccountToJson(account))
                    .updatedBy(AccountUtils.convertAccountToJson(account))
                    .isDeleted(0)
                    .build();

            categoryRepository.save(categoryEntity);

            // Gửi thông báo đến Kafka sau khi tạo danh mục
            CreateNotification createNotification = CreateNotification.builder()
                    .notiAccId(account.getAccountRestaurantId())
                    .notiTitle("Danh mục bài viết mới được tạo")
                    .notiContent("Danh mục bài viết mới: " + createCategoryDto.getCatName())
                    .notiType("category_create")
                    .notiMetadata("no metadata")
                    .sendObject("all_account")
                    .build();
            String json = new ObjectMapper().writeValueAsString(createNotification);
            kafkaTemplate.send("NOTIFICATION_ACCOUNT_CREATE", json);

            return categoryEntity;
        }
        catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryEntity getCategory(String id) {
        try {
            UUID cat_id = UUID.fromString(id);
            Optional<CategoryEntity> categoryEntity = categoryRepository.findById(cat_id);
            if(categoryEntity.isEmpty()){
                throw  new BadRequestError("Danh mục không tồn tại");
            }
            return categoryEntity.get();
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryEntity updateCategory(UpdateCategoryDto updateCategoryDto, Account account) {
        UUID cat_id = UUID.fromString(updateCategoryDto.getCatId());
        Optional<CategoryEntity> categoryEntityOptional = categoryRepository.findById(cat_id);
        if(categoryEntityOptional.isEmpty()){
            throw new BadRequestError("Danh mục không tồn tại");
        }
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        String slug = Slug.createSlug(updateCategoryDto.getCatName()) + "-" + number;
        CategoryEntity categoryEntity = categoryEntityOptional.get();
        categoryEntity.setCatSlug(slug);
        categoryEntity.setCatOrder(updateCategoryDto.getCatOrder());
        categoryEntity.setCatName(updateCategoryDto.getCatName());
        categoryEntity.setCatDescription(updateCategoryDto.getCatDescription());
        categoryEntity.setCatOrder(updateCategoryDto.getCatOrder());
        categoryEntity.setUpdatedBy(AccountUtils.convertAccountToJson(account));
        categoryRepository.save(categoryEntity);
        // Gửi thông báo đến Kafka sau khi cập nhật danh mục
        CreateNotification createNotification = CreateNotification.builder()
                .notiAccId(account.getAccountRestaurantId())
                .notiTitle("Danh mục bài viết đã được cập nhật")
                .notiContent("Danh mục bài viết: " + updateCategoryDto.getCatName() + " đã được cập nhật")
                .notiType("category_update")
                .notiMetadata("no metadata")
                .sendObject("all_account")
                .build();
        String json = new ObjectMapper().writeValueAsString(createNotification);
        kafkaTemplate.send("NOTIFICATION_ACCOUNT_CREATE", json);
        return categoryEntity;
    }

    @Override
    public CategoryEntity deleteCategory(String id, Account account) {
        UUID cat_id = UUID.fromString(id);
        Optional<CategoryEntity> categoryEntityOptional = categoryRepository.findById(cat_id);
        if(categoryEntityOptional.isEmpty()){
            throw new BadRequestError("Danh mục không tồn tại");
        }
        CategoryEntity categoryEntity = categoryEntityOptional.get();
        categoryEntity.setIsDeleted(1);
        categoryEntity.setDeletedBy(AccountUtils.convertAccountToJson(account));
        categoryEntity.setDeletedAt(new Date(System.currentTimeMillis()));
        categoryRepository.save(categoryEntity);
        return categoryEntity;
    }

    @Override
    public CategoryEntity restoreCategory(String id, Account account) {
        UUID cat_id = UUID.fromString(id);
        Optional<CategoryEntity> categoryEntityOptional = categoryRepository.findById(cat_id);
        if(categoryEntityOptional.isEmpty()){
            throw new BadRequestError("Danh mục không tồn tại");
        }
        CategoryEntity categoryEntity = categoryEntityOptional.get();
        categoryEntity.setIsDeleted(0);
        categoryEntity.setDeletedBy(null);
        categoryEntity.setDeletedAt(null);
        categoryRepository.save(categoryEntity);
        // Gửi thông báo đến Kafka sau khi khôi phục danh mục
        CreateNotification createNotification = CreateNotification.builder()
                .notiAccId(account.getAccountRestaurantId())
                .notiTitle("Danh mục bài viết đã được khôi phục")
                .notiContent("Danh mục bài viết: " + categoryEntity.getCatName() + " đã được khôi phục")
                .notiType("category_restore")
                .notiMetadata("no metadata")
                .sendObject("all_account")
                .build();
        String json = new ObjectMapper().writeValueAsString(createNotification);
        kafkaTemplate.send("NOTIFICATION_ACCOUNT_CREATE", json);
        return categoryEntity;
    }

    @Override
    public CategoryEntity updateStatus(UpdateStatusCategoryDto updateStatusCategoryDto, Account account) {
        UUID cat_id = UUID.fromString(updateStatusCategoryDto.getCatId());
        Optional<CategoryEntity> categoryEntityOptional = categoryRepository.findById(cat_id);
        if(categoryEntityOptional.isEmpty()){
            throw new BadRequestError("Danh mục không tồn tại");
        }
        CategoryEntity categoryEntity = categoryEntityOptional.get();
        categoryEntity.setCatStatus(updateStatusCategoryDto.getCatStatus());
        categoryEntity.setUpdatedBy(AccountUtils.convertAccountToJson(account));
        categoryRepository.save(categoryEntity);
        // Gửi thông báo đến Kafka sau khi cập nhật trạng thái danh mục
        CreateNotification createNotification = CreateNotification.builder()
                .notiAccId(account.getAccountRestaurantId())
                .notiTitle("Trạng thái danh mục bài viết đã được cập nhật")
                .notiContent("Danh mục bài viết: " + categoryEntity.getCatName() + " đã được cập nhật trạng thái")
                .notiType("category_update_status")
                .notiMetadata("no metadata")
                .sendObject("all_account")
                .build();
        String json = new ObjectMapper().writeValueAsString(createNotification);
        kafkaTemplate.send("NOTIFICATION_ACCOUNT_CREATE", json);
        return categoryEntity;
    }

    @Override
    public ResPagination<CategoryEntity> getAllCategory(int pageIndex, int pageSize, String catName, Account account) {
        try {
            Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
            Page<CategoryEntity> categoryEntities = categoryRepository.findByFilters(catName, account.getAccountRestaurantId(), 0, pageable);

            return ResPagination.<CategoryEntity>builder()
                    .result(categoryEntities.getContent())
                    .meta(MetaPagination.builder()
                            .current(pageIndex)
                            .pageSize(pageSize)
                            .totalPage(categoryEntities.getTotalPages())
                            .totalItem(categoryEntities.getTotalElements())
                            .build())
                    .build();
        }catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResPagination<CategoryEntity> getAllCategoryRecycleBin(int pageIndex, int pageSize, String catName, Account account) {
        try {
            Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
            Page<CategoryEntity> categoryEntities = categoryRepository.findByFilters(catName, account.getAccountRestaurantId(), 1, pageable);

            return ResPagination.<CategoryEntity>builder()
                    .result(categoryEntities.getContent())
                    .meta(MetaPagination.builder()
                            .current(pageIndex)
                            .pageSize(pageSize)
                            .totalPage(categoryEntities.getTotalPages())
                            .totalItem(categoryEntities.getTotalElements())
                            .build())
                    .build();
        }catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CategoryName> getAllCategoryByRestaurantId(Account account) {
        try {
            return categoryRepository.findByCatResIdAndIsDeletedAndCatStatus(account.getAccountRestaurantId(), 0,EnumStatus.ENABLED);
        }catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }



}
