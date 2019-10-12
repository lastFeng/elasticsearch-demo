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

import com.example.elasticsearchdemo.client.ESClientFactory;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Collections;

/**
 * <p> Title: </p>
 *
 * <p> Description: </p>
 *
 * @author: L.J
 * @Email: alieismy@gmail.com
 * @version: 1.0
 * @create: 2019/10/12 14:25
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class RestClient02Test {
    private String index;
    private String type;
    private String id;

    private RestClient restClient;
    private RestHighLevelClient restHighLevelClient;

    @Before
    public void before() {
        index = "demo2";
        type = "";
        id = "";
        restClient = ESClientFactory.getRestClient();
        restHighLevelClient = ESClientFactory.getRestHighLevelClient();
    }

    /**
     * 创建自定义索引的格式内容
     */
    @Test
    public void indexTest() {
        // 借助IndexRequest的JSON工具进行拼装
        try {
            IndexRequest indexRequest = new IndexRequest();
            XContentBuilder builder = JsonXContent.contentBuilder()
                .startObject()
                    .startObject("mappings")
                        .startObject("demo2")
                            .startObject("properties")
                                .startObject("title")
                                    .field("type", "text")
                                    .field("analyzer", "ik_max_word")
                                .endObject()
                                .startObject("content")
                                    .field("type", "text")
                                    .field("index", true)
                                    .field("analyzer", "ik_max_word")
                                .endObject()
                                .startObject("uniqueId")
                                    .field("type", "keyword")
                                    .field("index", false)
                                .endObject()
                                .startObject("created")
                                    .field("type", "date")
                                    .field("format", "strict_date_optional_time||epoch_millis")
                                .endObject()
                            .endObject()
                        .endObject()
                    .endObject()
                    .startObject("settings")
                        .field("number_of_shards", 2)
                        .field("number_of_replicas", 1)
                    .endObject()
                .endObject();
            indexRequest.source(builder);

            // 生成JSON字符串
            String source = indexRequest.source().utf8ToString();
            HttpEntity entity = new NStringEntity(source, ContentType.APPLICATION_JSON);

            // 使用RestClient操作，而不是RestHighLevelClient操作
            Response response = restClient.performRequest("PUT", "/demo2",
                Collections.<String, String>emptyMap(), entity);
            System.out.println("------------------------>");
            System.out.println(response);
            System.out.println("<------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 创建类型映射
     * 前提是索引已经存在,
     * 在新建的索引中创建sports类型的映射信息(6+好像移除了这一内容)
     * */
    @Test
    public void typeTest() {
        try {
            IndexRequest indexRequest = new IndexRequest();
            XContentBuilder builder = JsonXContent.contentBuilder()
                .startObject()
                    .startObject("mappings")
                        .startObject("content")
                            .field("type", "text")
                            .field("analyzer", "ik_max_word")
                            .field("index", true)
                        .endObject()
                    .endObject()
                .endObject();
            indexRequest.source(builder);

            String source = indexRequest.source().utf8ToString();
            HttpEntity entity = new NStringEntity(source, ContentType.APPLICATION_JSON);

            Response response = restClient.performRequest("POST", "/news/sports/_mapping",
                Collections.<String, String>emptyMap(), entity);
            System.out.println("------------------------>");
            System.out.println(response);
            System.out.println("<------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void indexExistsTest() {
        System.out.println("--------------------------------->");
        System.out.println(indexExists(index));
        System.out.println("<---------------------------------");
    }

    /**
     * 查看索引是否存在
     * */
    private boolean indexExists(String index) {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);

        try {
            boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
            return exists;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}