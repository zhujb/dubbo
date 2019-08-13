/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.bootstrap.compatible;

import org.apache.dubbo.bootstrap.DubboBootstrap;
import org.apache.dubbo.bootstrap.EchoService;
import org.apache.dubbo.bootstrap.rest.UserService;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.context.ConfigManager;

/**
 * Dubbo Provider Bootstrap
 *
 * @since 2.7.4
 */
public class DubboInterfaceConsumerBootstrap {

    public static void main(String[] args) throws Exception {
        RegistryConfig interfaceRegistry = new RegistryConfig();
        interfaceRegistry.setId("interfaceRegistry");
        interfaceRegistry.setAddress("zookeeper://127.0.0.1:2181");

        new DubboBootstrap()
                .application("dubbo-consumer-demo")
                // Zookeeper
                .registry(interfaceRegistry)
                // Nacos
//                .registry("consul", builder -> builder.address("consul://127.0.0.1:8500?registry.type=service&subscribed.services=dubbo-provider-demo"))
                .reference("echo", builder -> builder.interfaceClass(EchoService.class).protocol("dubbo"))
                .reference("user", builder -> builder.interfaceClass(UserService.class).protocol("rest"))
                .onlyRegisterProvider(true)
                .start()
                .await();

        ConfigManager configManager = ConfigManager.getInstance();

        ReferenceConfig<EchoService> referenceConfig = configManager.getReferenceConfig("echo");
        EchoService echoService = referenceConfig.get();

        ReferenceConfig<UserService> referenceConfig1 = configManager.getReferenceConfig("user");
        UserService userService = referenceConfig1.get();

        for (int i = 0; i < 500; i++) {
            Thread.sleep(2000L);
            System.out.println(echoService.echo("Hello,World"));
            System.out.println(userService.getUser(1L));
        }

    }
}