/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package com.netflix.spinnaker.echo.config

import com.netflix.spinnaker.config.OkHttpClientConfiguration
import com.netflix.spinnaker.echo.services.Front50Service
import com.netflix.spinnaker.okhttp.OkHttpClientConfigurationProperties
import com.squareup.okhttp.ConnectionPool
import com.squareup.okhttp.OkHttpClient
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit.Endpoint
import retrofit.RestAdapter
import retrofit.RestAdapter.LogLevel
import retrofit.client.OkClient

import static retrofit.Endpoints.newFixedEndpoint

@Configuration
@EnableConfigurationProperties(Front50ConfigurationProperties)
@Slf4j
@CompileStatic
class Front50Config {

  @Autowired
  OkHttpClientConfiguration okHttpClientConfig

  @Bean
  OkHttpClient okHttpClient(OkHttpClientConfigurationProperties okHttpClientConfigurationProperties) {
    def cli = okHttpClientConfig.create()
    cli.connectionPool = new ConnectionPool(
            okHttpClientConfigurationProperties.connectionPool.maxIdleConnections,
            okHttpClientConfigurationProperties.connectionPool.keepAliveDurationMs)
    cli.retryOnConnectionFailure = okHttpClientConfigurationProperties.retryOnConnectionFailure
    return cli
  }

  @Bean
  LogLevel retrofitLogLevel() {
    LogLevel.BASIC
  }

  @Bean
  Endpoint front50Endpoint(Front50ConfigurationProperties front50ConfigurationProperties) {
    newFixedEndpoint(front50ConfigurationProperties.baseUrl)
  }

  @Bean
  Front50Service front50Service(Endpoint front50Endpoint, OkHttpClient okHttpClient, LogLevel retrofitLogLevel) {
    log.info('front50 service loaded')
    new RestAdapter.Builder()
      .setEndpoint(front50Endpoint)
      .setClient(new OkClient(okHttpClient))
      .setLogLevel(retrofitLogLevel)
      .build()
      .create(Front50Service)
  }

}
