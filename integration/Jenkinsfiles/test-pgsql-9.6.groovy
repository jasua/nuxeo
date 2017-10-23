/*
 * (C) Copyright 2017 Nuxeo (http://nuxeo.com/) and others.
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
 *
 * Contributors:
 *     jcarsique
 */

currentBuild.setDescription("PostgreSQL 9.6")
timeout(time: 2, unit: 'HOURS') {
    timestamps {
        node('SLAVE') {
            stage 'clone'
                checkout([$class: 'GitSCM', branches: [[name: '*/${BRANCH}']], browser: [$class: 'GithubWeb', repoUrl: 'https://github.com/nuxeo/nuxeo'], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [
                        [url: 'git@github.com:nuxeo/nuxeo.git']
                    ]])
                sh """#!/bin/bash -xe
                    ./clone.py $BRANCH -f $PARENT_BRANCH
                """

            try {
                stage 'tests'
                    sh """#!/bin/bash -x
                        docker-compose -f integration/Jenkinsfiles/docker-compose-pgsql-9.6.yml pull
                        docker-compose -f integration/Jenkinsfiles/docker-compose-pgsql-9.6.yml --project-name $JOB_NAME-$BUILD_NUMBER up --build --abort-on-container-exit db tests
                    """
                // setBuildStatus("Build complete", "SUCCESS");
            } finally {
                archive '**/target/failsafe-reports/*, **/target/*.png, **/target/**/*.log, **/target/**/log/*'
                junit '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml, **/target/failsafe-reports/**/*.xml'
                // missing Claim plugin
                // emailext body: '$DEFAULT_CONTENT', recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'CulpritsRecipientProvider'], [$class: 'RequesterRecipientProvider']], replyTo: '$DEFAULT_RECIPIENTS', subject: '$DEFAULT_SUBJECT', to: '$DEFAULT_RECIPIENTS,ecm-qa@lists.nuxeo.com'
                emailext body: '$DEFAULT_CONTENT', replyTo: '$DEFAULT_RECIPIENTS', subject: '$DEFAULT_SUBJECT', to: 'jcarsique@nuxeo.com'
                // missing Jabber plugin
                sh """#!/bin/bash -x
                    docker-compose -f integration/Jenkinsfiles/docker-compose-pgsql-9.6.yml down
                """
            }
        }
    }
}

void setBuildStatus(String message, String state) {
  step([
      $class: "GitHubCommitStatusSetter",
//      reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/my-org/my-repo"],
//      contextSource: [$class: "ManuallyEnteredCommitContextSource", context: "ci/jenkins/build-status"],
      errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
      statusResultSource: [ $class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]] ]
  ]);
}

