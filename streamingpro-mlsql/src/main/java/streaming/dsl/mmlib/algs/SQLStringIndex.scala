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

package streaming.dsl.mmlib.algs

import org.apache.spark.ml.feature.{StringIndexer, StringIndexerModel}
import org.apache.spark.ml.help.HSQLStringIndex
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.types.{ArrayType, StringType}
import streaming.dsl.mmlib.SQLAlg
import org.apache.spark.sql.{functions => F}

/**
  * Created by allwefantasy on 15/1/2018.
  */
class SQLStringIndex extends SQLAlg with Functions {

  override def train(df: DataFrame, path: String, params: Map[String, String]): DataFrame = {
    require(params.contains("inputCol"), "inputCol is required")
    val inputCol = params("inputCol")
    var newDf = df
    df.schema.filter(f => f.name == inputCol).head.dataType match {
      case ArrayType(StringType, _) =>
        newDf = df.select(F.explode(F.col(inputCol)).as(inputCol))
      case StringType => // do nothing
      case _ => throw new IllegalArgumentException(s"${params("inputCol")} should be arraytype or stringtype")
    }
    val rfc = new StringIndexer()
    configureModel(rfc, params)
    val model = rfc.fit(newDf)
    model.write.overwrite().save(path)
    emptyDataFrame()(df)
  }

  override def load(sparkSession: SparkSession, path: String, params: Map[String, String]): Any = {
    val model = StringIndexerModel.load(path)
    model
  }


  override def batchPredict(df: DataFrame, path: String, params: Map[String, String]): DataFrame = {
    val model = load(df.sparkSession, path, params).asInstanceOf[StringIndexerModel]
    model.transform(df)
  }

  override def predict(sparkSession: SparkSession, _model: Any, name: String, params: Map[String, String]): UserDefinedFunction = {
    HSQLStringIndex.predict(sparkSession, _model, name)
  }

  def internal_predict(sparkSession: SparkSession, _model: Any, name: String) = {
    HSQLStringIndex.internal_predict(sparkSession, _model, name)
  }
}
