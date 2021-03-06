/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.netty.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import rx.Observable;

/**
 * An implementation of {@link PipelineConfigurator} which is ALWAYS added at the end of the pipeline. This
 * pipeline configurator bridges between netty's pipeline processing and Rx {@link Observable}
 *
 * @param <I> Input type for the pipeline. This is the type one writes to this pipeline.
 * @param <O> Output type of the emitted observable.  This is the type one reads from this pipeline.
 *
 * @author Nitesh Kant
 */
public abstract class RxRequiredConfigurator<I, O> implements PipelineConfigurator<I, O> {

    public static final String CONN_LIFECYCLE_HANDLER_NAME = "conn_lifecycle_handler";
    public static final String NETTY_OBSERVABLE_ADAPTER_NAME = "netty_observable_adapter";

    @Override
    public void configureNewPipeline(ChannelPipeline pipeline) {

        /**
         * This method is called for each new connection & the following two channel handlers are not shareable, so
         * we need to create a new instance every time.
         */
        ChannelHandler lifecycleHandler = newConnectionLifecycleHandler(pipeline);
        ObservableAdapter observableAdapter = new ObservableAdapter();

        pipeline.addLast(CONN_LIFECYCLE_HANDLER_NAME, lifecycleHandler);
        pipeline.addLast(NETTY_OBSERVABLE_ADAPTER_NAME, observableAdapter);
    }

    protected abstract ChannelHandler newConnectionLifecycleHandler(ChannelPipeline pipeline);
}
