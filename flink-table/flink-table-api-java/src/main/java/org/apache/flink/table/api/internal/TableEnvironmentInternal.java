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

package org.apache.flink.table.api.internal;

import org.apache.flink.annotation.Experimental;
import org.apache.flink.annotation.Internal;
import org.apache.flink.table.api.CompiledPlan;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;
import org.apache.flink.table.operations.utils.OperationTreeBuilder;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.table.sources.TableSource;

import java.util.List;

/**
 * An internal interface of {@link TableEnvironment} that defines extended methods used for {@link
 * TableImpl}.
 *
 * <p>Once old planner is removed, this class also can be removed. By then, these methods can be
 * moved into TableEnvironmentImpl.
 */
@Internal
public interface TableEnvironmentInternal extends TableEnvironment {

    /**
     * Return a {@link Parser} that provides methods for parsing a SQL string.
     *
     * @return initialized {@link Parser}.
     */
    Parser getParser();

    /** Returns a {@link CatalogManager} that deals with all catalog objects. */
    CatalogManager getCatalogManager();

    /** Returns a {@link OperationTreeBuilder} that can create {@link QueryOperation}s. */
    OperationTreeBuilder getOperationTreeBuilder();

    /**
     * Creates a table from a table source.
     *
     * @param source table source used as table
     */
    @Deprecated
    Table fromTableSource(TableSource<?> source);

    /**
     * Execute the given modify operations and return the execution result.
     *
     * @param operations The operations to be executed.
     * @return the affected row counts (-1 means unknown).
     */
    TableResultInternal executeInternal(List<ModifyOperation> operations);

    /**
     * Execute the given operation and return the execution result.
     *
     * @param operation The operation to be executed.
     * @return the content of the execution result.
     */
    TableResultInternal executeInternal(Operation operation);

    /**
     * Returns the AST of this table and the execution plan to compute the result of this table.
     *
     * @param operations The operations to be explained.
     * @param extraDetails The extra explain details which the explain result should include, e.g.
     *     estimated cost, changelog mode for streaming
     * @return AST and the execution plan.
     */
    String explainInternal(List<Operation> operations, ExplainDetail... extraDetails);

    /**
     * Registers an external {@link TableSource} in this {@link TableEnvironment}'s catalog.
     * Registered tables can be referenced in SQL queries.
     *
     * <p>Temporary objects can shadow permanent ones. If a permanent object in a given path exists,
     * it will be inaccessible in the current session. To make the permanent object available again
     * one can drop the corresponding temporary object.
     *
     * @param name The name under which the {@link TableSource} is registered.
     * @param tableSource The {@link TableSource} to register.
     */
    void registerTableSourceInternal(String name, TableSource<?> tableSource);

    /**
     * Registers an external {@link TableSink} with already configured field names and field types
     * in this {@link TableEnvironment}'s catalog. Registered sink tables can be referenced in SQL
     * DML statements.
     *
     * <p>Temporary objects can shadow permanent ones. If a permanent object in a given path exists,
     * it will be inaccessible in the current session. To make the permanent object available again
     * one can drop the corresponding temporary object.
     *
     * @param name The name under which the {@link TableSink} is registered.
     * @param configuredSink The configured {@link TableSink} to register.
     */
    void registerTableSinkInternal(String name, TableSink<?> configuredSink);

    /**
     * Compile a plan staring from a list of operations.
     *
     * <p><b>Note:</b> This API is <b>experimental</b> and subject to change in future releases.
     */
    @Experimental
    CompiledPlan compilePlan(List<ModifyOperation> operations);
}
