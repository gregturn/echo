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

import com.netflix.astyanax.Keyspace
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException
import com.netflix.spinnaker.kork.astyanax.AstyanaxKeyspaceFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
/**
 * Enables Cassandra for this project
 */
@Configuration
@ConditionalOnExpression('${spinnaker.cassandra.enabled:true}')
@EnableConfigurationProperties(SpinnakerConfigurationProperties)
@SuppressWarnings('GStringExpressionWithinString')
class CassandraConfig {

    @Autowired
    Environment environment

    @Autowired
    AstyanaxKeyspaceFactory factory

    @Bean
    Keyspace keySpace(SpinnakerConfigurationProperties spinnakerConfigurationProperties) throws ConnectionException {
        factory.getKeyspace(spinnakerConfigurationProperties.cluster.cluster, spinnakerConfigurationProperties.cluster.keyspace)
    }

}
