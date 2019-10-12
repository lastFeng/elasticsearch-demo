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
package com.example.elasticsearchdemo.client;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

/**
 * <p> Title: </p>
 *
 * <p> Description: </p>
 *
 * @author: L.J
 * @Email: alieismy@gmail.com
 * @version: 1.0
 * @create: 2019/10/12 11:19
 */

public class ESClientFactory {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9200;
    private static final String SCHEMA = "http";
    private static final int CONNECT_TIME_OUT = 1000;
    private static final int SOCKED_TIME_OUT = 3000;
    private static final int CONNECTION_REQUEST_TIME_OUT = 500;

    private static final int MAX_CONNECT_NUM = 100;
    private static final int MAX_CONNECT_PER_ROUTE = 100;

    private static HttpHost HTTP_HOST = new HttpHost(HOST, PORT, SCHEMA);
    private static boolean uniqueConnectTimeConfig = false;
    private static boolean uniqueConnectNumConfig = true;
    private static RestClientBuilder builder;
    private static RestClient restClient;
    private static RestHighLevelClient restHighLevelClient;

    static {
        init();
    }

    /**
     * 初始化
     * */
    public static void init() {
        builder = RestClient.builder(HTTP_HOST);

        if (uniqueConnectTimeConfig) {
            setConnectTimeOutConfig();
        }

        if (uniqueConnectNumConfig) {
            setMultiConnectConfig();
        }

        restClient = builder.build();
        restHighLevelClient = new RestHighLevelClient(builder);
    }

    /**
     * 设置连接超时配置
     * */
    private static void setConnectTimeOutConfig() {
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(CONNECT_TIME_OUT);
            requestConfigBuilder.setSocketTimeout(SOCKED_TIME_OUT);
            requestConfigBuilder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT);
            return requestConfigBuilder;
        });
    }

    /**
     * 设置多连接配置
     * */
    private static void setMultiConnectConfig() {
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(MAX_CONNECT_NUM);
            httpClientBuilder.setMaxConnPerRoute(MAX_CONNECT_PER_ROUTE);
            return httpClientBuilder;
        });
    }

    public static RestClient getRestClient() {
        return restClient;
    }

    public static RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    public static void closeRestClient() {
        if (restClient != null) {
            try {
                restClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeRestHighLevelClient() {
        if (restHighLevelClient != null) {
            try {
                restHighLevelClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
