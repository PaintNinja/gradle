/*
 * Copyright 2023 the original author or authors.
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

package org.gradle.internal.declarativedsl.serialization

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.gradle.internal.declarativedsl.analysis.AnalysisSchema
import org.gradle.internal.declarativedsl.analysis.AnalysisSchemaImpl
import org.gradle.internal.declarativedsl.analysis.DataBuilderFunction
import org.gradle.internal.declarativedsl.analysis.DataClass
import org.gradle.internal.declarativedsl.analysis.DataClassImpl
import org.gradle.internal.declarativedsl.analysis.DataMemberFunction
import org.gradle.internal.declarativedsl.analysis.FqName
import org.gradle.internal.declarativedsl.analysis.FqNameImpl
import org.gradle.internal.declarativedsl.analysis.SchemaMemberFunction
import org.gradle.internal.declarativedsl.language.DataType


object SchemaSerialization {

    private
    val json = Json {
        serializersModule = SerializersModule {
            polymorphic(DataType::class) {
                subclass(DataType.IntDataType::class)
                subclass(DataType.LongDataType::class)
                subclass(DataType.StringDataType::class)
                subclass(DataType.BooleanDataType::class)
                subclass(DataType.NullType::class)
                subclass(DataType.UnitType::class)
            }
            polymorphic(DataClass::class) {
                subclass(DataClassImpl::class)
            }
            polymorphic(FqName::class) {
                subclass(FqNameImpl::class)
            }
            polymorphic(SchemaMemberFunction::class) {
                subclass(DataMemberFunction::class)
                subclass(DataBuilderFunction::class)
            }
        }
        prettyPrint = true
        allowStructuredMapKeys = true
    }

    fun schemaToJsonString(analysisSchema: AnalysisSchema) = json.encodeToString(analysisSchema as AnalysisSchemaImpl)

    fun schemaFromJsonString(schemaString: String) = json.decodeFromString<AnalysisSchemaImpl>(schemaString)
}
