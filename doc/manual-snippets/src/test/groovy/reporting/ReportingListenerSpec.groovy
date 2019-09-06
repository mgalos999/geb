/*
 * Copyright 2015 the original author or authors.
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
package reporting

import configuration.InlineConfigurationLoader
import geb.test.GebSpecWithCallbackServer
import org.junit.Rule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.rules.TemporaryFolder

class ReportingListenerSpec extends GebSpecWithCallbackServer implements InlineConfigurationLoader {

    @Rule
    TemporaryFolder temporaryFolder

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog()

    String getOutput() {
        systemOutRule.log
    }

    String getReportingListenerConfiguration() {
        '''
            // tag::config[]
            import geb.report.*

            reportingListener = new ReportingListener() {
                void onReport(Reporter reporter, ReportState reportState, List<File> reportFiles) {
                    reportFiles.each {
                        println "[[ATTACHMENT|$it.absolutePath]]"
                    }
                }
            }
            // end::config[]
        '''
    }

    def "registering a reporting listener"() {
        given:
        html { }

        when:
        configScript(reportingListenerConfiguration)
        browser.config.rawConfig.merge(config.rawConfig)

        and:
        go()
        report "some report"

        then:
        output =~ /\[\[ATTACHMENT\|.*some report\.html\]\]/
    }
}
