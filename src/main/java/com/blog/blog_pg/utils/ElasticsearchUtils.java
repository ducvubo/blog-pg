package com.blog.blog_pg.utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchUtils {

    private final ElasticsearchClient elasticsearchClient;

    @Autowired
    public ElasticsearchUtils(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    // Thêm document vào Elasticsearch
    public <T> void addDocToElasticsearch(String index, String id, T data) throws Exception {
        try {
            IndexRequest<T> request = IndexRequest.of(i -> i
                    .index(index)
                    .id(id)
                    .document(data)
            );
            IndexResponse response = elasticsearchClient.index(request);
        } catch (Exception e) {
            throw new Exception("Error adding document to Elasticsearch: " + e.getMessage());
        }
    }

    // Cập nhật document trong Elasticsearch
    public <T> void updateDocByElasticsearch(String index, String id, T data) throws Exception {
        try {
            UpdateRequest<T, T> request = UpdateRequest.of(u -> u
                    .index(index)
                    .id(id)
                    .doc(data)
                    .upsert(data)
                    .refresh(co.elastic.clients.elasticsearch._types.Refresh.True)
            );
            UpdateResponse<T> response = elasticsearchClient.update(request, data.getClass());
        } catch (Exception e) {
            throw new Exception("Error updating document in Elasticsearch: " + e.getMessage());
        }
    }

    // Xóa một document từ Elasticsearch
    public void deleteDocByElasticsearch(String index, String id) throws Exception {
        try {
            DeleteRequest request = DeleteRequest.of(d -> d
                    .index(index)
                    .id(id)
            );
            DeleteResponse response = elasticsearchClient.delete(request);
        } catch (Exception e) {
            throw new Exception("Error deleting document from Elasticsearch: " + e.getMessage());
        }
    }

    // Xóa tất cả documents bằng cách xóa index
    public void deleteAllDocByElasticsearch(String index) throws Exception {
        try {
            DeleteIndexRequest request = DeleteIndexRequest.of(d -> d
                    .index(index)
            );
            elasticsearchClient.indices().delete(request);
        } catch (Exception e) {
            throw new Exception("Error deleting index from Elasticsearch: " + e.getMessage());
        }
    }

    // Kiểm tra xem index có tồn tại không
    public boolean indexElasticsearchExists(String index) throws Exception {
        try {
            ExistsRequest request = ExistsRequest.of(e -> e
                    .index(index)
            );
            return elasticsearchClient.indices().exists(request).value();
        } catch (Exception e) {
            throw new Exception("Error checking index existence in Elasticsearch: " + e.getMessage());
        }
    }
}
