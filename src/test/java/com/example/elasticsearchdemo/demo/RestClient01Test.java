/*
 * Copyright 2001-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.elasticsearchdemo.demo;

import com.alibaba.fastjson.JSON;
import com.example.elasticsearchdemo.client.ESClientFactory;
import com.example.elasticsearchdemo.domain.News;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> Title: </p>
 *
 * <p> Description: </p>
 *
 * @author: L.J
 * @Email: alieismy@gmail.com
 * @version: 1.0
 * @create: 2019/10/12 11:48
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class RestClient01Test {
    private String index;
    private String type;
    private String id;

    private RestHighLevelClient restHighLevelClient = ESClientFactory.getRestHighLevelClient();

    @Before
    public void prepare() {
        index = "demo";
        type = "demo";
        id = "H-x_vm0BSILTfRv59FCQ";

    }

    /**
     * 单个记录新增
     * */
    @Test
    public void addTest() {
        IndexRequest indexRequest = generateNewsRequest("中国产小型无人机的“对手”来了，俄微型拦截导弹便宜量又多,heihei",
            "军事", "2019-01-24T23:59:30Z");

        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量增加
     * */
    @Test
    public void bulkAddTest() {
        BulkRequest bulkRequest = new BulkRequest();
        List<IndexRequest> requests = generateRequest();

        for (IndexRequest indexRequest: requests) {
            bulkRequest.add(indexRequest);
        }

        try {
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<IndexRequest> generateRequest() {
        List<IndexRequest> requests = new ArrayList<>();

        requests.add(generateNewsRequest("中印边防军于拉达克举行会晤 强调维护边境和平", "军事", "2018-01-27T08:34:00Z"));
        requests.add(generateNewsRequest("费德勒收郑泫退赛礼 进决赛战西里奇", "体育", "2018-01-26T14:34:00Z"));
        requests.add(generateNewsRequest("欧文否认拿动手术威胁骑士 兴奋全明星联手詹皇", "体育", "2018-01-26T08:34:00Z"));
        requests.add(generateNewsRequest("皇马官方通告拉莫斯伊斯科伤情 将缺阵西甲关键战", "体育", "2018-01-26T20:34:00Z"));

        return requests;
    }

    private IndexRequest generateNewsRequest(String title, String tag, String publishTime) {
        IndexRequest indexRequest = new IndexRequest(index, type);
        News news = new News(title, tag, publishTime);

        String source = JSON.toJSONString(news);
        indexRequest.source(source, XContentType.JSON);

        return indexRequest;
    }

    /**
     * 数据查询
     * example：2018年1月26日早八点到晚八点关于费德勒的前十条体育新闻的标题
     * */
    @Test
    public void queryTest() {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 设置从哪开始读取
        sourceBuilder.from(0);
        // 设置大小位置
        sourceBuilder.size(10);
        // 设置获取的内容（只获取title内容）
        sourceBuilder.fetchSource(new String[]{"title"}, new String[]{});

        // 模糊匹配设置
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", "费德勒");

        // 设置查询的字符的tag
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("tag", "体育");

        // 设置查询范围
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("publishTime");
        rangeQueryBuilder.gte("2018-01-26T08:00:00Z");
        rangeQueryBuilder.lte("2018-01-26T20:00:00Z");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(matchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);
        boolQueryBuilder.must(rangeQueryBuilder);

        sourceBuilder.query(boolQueryBuilder);

        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);

        // 设置对source的复合查询，这个必须加，不加就是全量查询
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println("==================");
            System.out.println(response);
            System.out.println("==================");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新数据
     * */
    @Test
    public void updateTest() {
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);

        Map<String, String> doc = new HashMap<>();
        doc.put("tag", "军事新闻");
        updateRequest.doc(doc);

        try {
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据给定的id进行查询
     */
    @Test
    public void getOneTest() {
        GetRequest getRequest = new GetRequest(index, type, id);

        try {
            GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            System.out.println("------------------------------------------>");
            System.out.println(response);
            System.out.println("<------------------------------------------");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}