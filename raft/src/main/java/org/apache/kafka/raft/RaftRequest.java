/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.raft;

import org.apache.kafka.common.protocol.ApiMessage;
import org.apache.kafka.common.Node;

import java.util.concurrent.CompletableFuture;

public abstract class RaftRequest implements RaftMessage {
    private final int correlationId;
    private final short apiVersion;
    private final ApiMessage data;
    private final long createdTimeMs;

    public RaftRequest(int correlationId, short apiVersion, ApiMessage data, long createdTimeMs) {
        this.correlationId = correlationId;
        this.apiVersion = apiVersion;
        this.data = data;
        this.createdTimeMs = createdTimeMs;
    }

    @Override
    public int correlationId() {
        return correlationId;
    }

    @Override
    public short apiVersion() {
        return apiVersion;
    }

    @Override
    public ApiMessage data() {
        return data;
    }

    public long createdTimeMs() {
        return createdTimeMs;
    }

    public static final class Inbound extends RaftRequest {
        public final CompletableFuture<RaftResponse.Outbound> completion = new CompletableFuture<>();

        public Inbound(int correlationId, short apiVersion, ApiMessage data, long createdTimeMs) {
            super(correlationId, apiVersion, data, createdTimeMs);
        }

        @Override
        public String toString() {
            return String.format(
                "InboundRequest(correlationId=%d, data=%s, createdTimeMs=%d)",
                correlationId(),
                data(),
                createdTimeMs()
            );
        }
    }

    public static final class Outbound extends RaftRequest {
        private final Node destination;
        public final CompletableFuture<RaftResponse.Inbound> completion = new CompletableFuture<>();

        public Outbound(int correlationId, short apiVersion, ApiMessage data, Node destination, long createdTimeMs) {
            super(correlationId, apiVersion, data, createdTimeMs);
            this.destination = destination;
        }

        public Node destination() {
            return destination;
        }

        @Override
        public String toString() {
            return String.format(
                "OutboundRequest(correlationId=%d, data=%s, createdTimeMs=%d, destination=%s)",
                correlationId(),
                data(),
                createdTimeMs(),
                destination
            );
        }
    }
}
