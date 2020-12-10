package ru.nazariene.elasticmonitor.service.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentType.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.nazariene.elasticmonitor.domain.Query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticQueryRunner {

    public static final int MAX_HITS_COUNT = 10000;

    public static final long SCROLL_TTL = 10L;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RestHighLevelClient restHighLevelClient;

    public GetResponse get(String indexName, String id) throws IOException {
        GetRequest request = new GetRequest(indexName, id);
        return restHighLevelClient.get(request, RequestOptions.DEFAULT);
    }

    public IndexResponse index(String indexName, String doc) throws IOException {
        IndexRequest request = new IndexRequest(indexName);
        request.source(doc, XContentType.JSON);
        return restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }

    public IndexResponse index(String indexName, String id, String doc) throws IOException {
        IndexRequest request = new IndexRequest(indexName);
        request.id(id);
        request.create(true);
        request.source(doc, XContentType.JSON);
        return restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }

    public UpdateResponse updateWithUpsert(String indexName, String id, String doc) throws IOException {
        UpdateRequest request = new UpdateRequest(indexName, id)
                .doc(doc, XContentType.JSON)
                .upsert(doc, XContentType.JSON);
        return restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }

    public UpdateResponse update(String indexName, String id, String doc) throws IOException {
        UpdateRequest request = new UpdateRequest(indexName, id);
        request.doc(doc, XContentType.JSON);
        return restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }

    public UpdateResponse update(String indexName, String id, Script script) throws IOException {
        UpdateRequest request = new UpdateRequest(indexName, id)
                .script(script);
        return restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }

    public DeleteResponse delete(String indexName, String id) throws IOException {
        DeleteRequest request = new DeleteRequest(indexName, id);
        return restHighLevelClient.delete(request, RequestOptions.DEFAULT);
    }

    public JsonNode search(Query query) {
        SearchSourceBuilder scb = new SearchSourceBuilder();
        scb.size(MAX_HITS_COUNT);
        var mcb =
                QueryBuilders.wrapperQuery(query.getValue());
        scb.query(mcb);

        SearchRequest request = new SearchRequest(query.getIndex());
        request.scroll(TimeValue.timeValueSeconds(SCROLL_TTL))
                .source(scb);


        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return objectMapper.readTree(response.toString());
        } catch (IOException e) {
            log.error("Failed to run search query due to {}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public List<SearchHit> search(String indexName, SearchSourceBuilder searchSourceBuilder) throws IOException {

        searchSourceBuilder.size(MAX_HITS_COUNT);
        SearchRequest request = new SearchRequest(indexName);
        request.scroll(TimeValue.timeValueSeconds(SCROLL_TTL))
                .source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        List<SearchHits> allHits = new ArrayList<>();
        allHits.add(response.getHits());

        if (response.getHits().getTotalHits().value > MAX_HITS_COUNT) {
            allHits.addAll(continueSearch(response));
        }

        List<SearchHit> hits = new ArrayList<>();
        allHits.forEach(searchHits -> searchHits.iterator().forEachRemaining(hits::add));

        return hits;
    }

    private List<SearchHits> continueSearch(SearchResponse searchResponse)
            throws IOException {
        List<SearchHits> scrollSearchHits = new ArrayList<>();

        String scrollId = searchResponse.getScrollId();
        List<String> scrollIds = new ArrayList<>(List.of(scrollId));

        SearchResponse searchScrollResponse;
        do {
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId)
                    .scroll(TimeValue.timeValueSeconds(SCROLL_TTL));
            searchScrollResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            scrollIds.add(scrollId);
            scrollSearchHits.add(searchScrollResponse.getHits());
            scrollId = searchScrollResponse.getScrollId();
        } while (searchScrollResponse.getHits().iterator().hasNext());

        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        scrollIds.forEach(clearScrollRequest::addScrollId);
        restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);

        return scrollSearchHits;
    }

    public List<SearchHit> searchWithoutScroll(String indexName, SearchSourceBuilder searchSourceBuilder)
            throws IOException {
        searchSourceBuilder.size(MAX_HITS_COUNT);
        SearchRequest request = new SearchRequest(indexName)
                .source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        return Arrays.asList(response.getHits().getHits());
    }

    public BulkResponse bulk(BulkRequest request) throws IOException {
        return restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
    }

    public BulkResponse bulkUpdate(String indexName, Map<String, String> docById) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        docById.entrySet().stream()
                .map(entry -> new UpdateRequest().id(entry.getKey()).index(indexName).doc(entry.getValue(), JSON)
                        .upsert(entry.getValue(), JSON))
                .forEach(bulkRequest::add);

        return restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    public BulkByScrollResponse deleteByQuery(DeleteByQueryRequest request) throws IOException {
        return restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
    }

    public BulkByScrollResponse updateByQuery(UpdateByQueryRequest request) throws IOException {
        return restHighLevelClient.updateByQuery(request, RequestOptions.DEFAULT);
    }
}
