/*
 * Copyright 2014-2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
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
package org.hawkular.metrics.clients.ptrans.statsd;

import org.hawkular.metrics.clients.ptrans.backend.NettyToVertxHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

/**
 * Channel initializer to be used when bootstrapping a statsd server.
 *
 * @author Thomas Segismont
 */
public class StatsdChannelInitializer extends ChannelInitializer<Channel> {
    private final NettyToVertxHandler nettyToVertxHandler;

    public StatsdChannelInitializer(NettyToVertxHandler nettyToVertxHandler) {
        this.nettyToVertxHandler = nettyToVertxHandler;
    }


    @Override
    public void initChannel(Channel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new StatsdDecoder());
        pipeline.addLast(nettyToVertxHandler);
    }
}
