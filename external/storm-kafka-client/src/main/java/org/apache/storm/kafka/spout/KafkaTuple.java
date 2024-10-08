/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.apache.storm.kafka.spout;

import org.apache.storm.tuple.Values;

/**
 * A list of Values in a tuple that can be routed 
 * to a given stream: {@link RecordTranslator#apply}.
 */
public class KafkaTuple extends Values {
    private static final long serialVersionUID = 4803794470450587992L;
    private String stream = null;
    
    public KafkaTuple() {
        super();
    }
    
    public KafkaTuple(Object... vals) {
        super(vals);
    }
    
    /**
     * Sets the target stream of this Tuple.
     * @param stream The target stream
     * @return This
     */
    public KafkaTuple routedTo(String stream) {
        assert this.stream == null;
        this.stream = stream;
        return this;
    }

    public String getStream() {
        return stream;
    }
}
