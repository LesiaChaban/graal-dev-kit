/*
 * Copyright 2023 Oracle and/or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cloud.graal.gcn.feature.service.logging;

import cloud.graal.gcn.GcnGeneratorContext;
import cloud.graal.gcn.feature.service.logging.template.LogControllerGroovy;
import cloud.graal.gcn.feature.service.logging.template.LogControllerJava;
import cloud.graal.gcn.feature.service.logging.template.LogControllerKotlin;
import cloud.graal.gcn.feature.service.logging.template.LogbackXml;
import cloud.graal.gcn.model.GcnCloud;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.starter.application.Project;
import io.micronaut.starter.build.dependencies.Dependency;
import io.micronaut.starter.template.RockerTemplate;
import jakarta.inject.Singleton;

import static cloud.graal.gcn.model.GcnCloud.AWS;

/**
 * AWS logging service feature.
 *
 * @since 1.0.0
 */
@Singleton
public class AwsLogging extends AbstractLoggingFeature {

    @Override
    protected void doApply(GcnGeneratorContext generatorContext) {

        generatorContext.addDependency(Dependency.builder()
                .groupId("io.micronaut.aws")
                .artifactId("micronaut-aws-cloudwatch-logging")
                .compile());

        generatorContext.addTemplate("loggingConfig-aws",
                new RockerTemplate(getModuleName(), "src/main/resources/logback.xml",
                        LogbackXml.template("CLOUDWATCH",
                                "io.micronaut.aws.cloudwatch.logging.CloudWatchLoggingAppender",
                                "io.micronaut.aws.cloudwatch.logging.CloudWatchJsonFormatter",
                                getModuleName())));

        if (generatorContext.generateExampleCode()) {

            Project project = generatorContext.getProject();

            generatorContext.addTemplate(getModuleName(), "AwsLogController",
                    generatorContext.getSourcePath("/{packagePath}/LogController"),
                    LogControllerJava.template(project),
                    LogControllerKotlin.template(project),
                    LogControllerGroovy.template(project));
        }
    }

    @NonNull
    @Override
    public GcnCloud getCloud() {
        return AWS;
    }

    @NonNull
    @Override
    public String getName() {
        return "gcn-aws-logging";
    }
}
