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
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

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
public class RestClientTest {
    private String index;
    private String type;

    private RestHighLevelClient restHighLevelClient = ESClientFactory.getRestHighLevelClient();

    @Before
    public void prepare() {
        index = "demo";
        type = "demo";
    }

    @Test
    public void addTest() {
        IndexRequest indexRequest = new IndexRequest(index, type);
        News news = new News();
        news.setTitle("中国产小型无人机的“对手”来了，俄微型拦截导弹便宜量又多,heihei");
        news.setTag("军事");
        news.setPublishTime("2019-01-24T23:59:30Z");

        String source = JSON.toJSONString(news);
        indexRequest.source(source, XContentType.JSON);
        try {
            restHighLevelClient.index(indexRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}