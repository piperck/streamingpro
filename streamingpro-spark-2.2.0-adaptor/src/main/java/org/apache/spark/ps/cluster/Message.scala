/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.ps.cluster

import org.apache.spark.rpc.RpcEndpointRef
import org.apache.spark.scheduler.cluster.CoarseGrainedClusterMessages.RegisteredExecutor

/**
  * Created by allwefantasy on 30/1/2018.
  */
object Message {

  case class RefreshPSExecutors()

  case class RegisteredExecutor()

  case class RegisterExecutorFailed(msg: String)

  case class RegisterPSExecutor(
                                 executorId: String,
                                 executorRef: RpcEndpointRef,
                                 hostname: String,
                                 cores: Int,
                                 logUrls: Map[String, String])

  case class TensorFlowModelClean(modelPath: String)

  case class CopyModelToLocal(modelPath: String, destPath: String)

  case object Ping

  case class Pong(executorId: String)

}
