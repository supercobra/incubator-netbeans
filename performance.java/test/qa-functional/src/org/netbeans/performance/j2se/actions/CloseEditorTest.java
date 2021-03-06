/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.netbeans.performance.j2se.actions;

import javax.swing.JComponent;
import junit.framework.Test;
import org.netbeans.modules.performance.utilities.PerformanceTestCase;
import org.netbeans.performance.j2se.setup.J2SESetup;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.actions.CloseViewAction;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.nodes.SourcePackagesNode;
import org.netbeans.jellytools.modules.form.FormDesignerOperator;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.modules.performance.guitracker.LoggingRepaintManager;

/**
 * Test of Closing Editor tabs.
 *
 * @author mmirilovic@netbeans.org
 */
public class CloseEditorTest extends PerformanceTestCase {

    /**
     * Folder with data
     */
    public static String fileProject;
    /**
     * Folder with data "gui/data"
     */
    public static String filePackage;
    /**
     * Name of file to open
     */
    public static String fileName;

    /**
     * Creates a new instance of CloseEditor
     *
     * @param testName the name of the test
     */
    public CloseEditorTest(String testName) {
        super(testName);
        expectedTime = WINDOW_OPEN;
    }

    /**
     * Creates a new instance of CloseEditor
     *
     * @param testName the name of the test
     * @param performanceDataName measured values will be saved under this name
     */
    public CloseEditorTest(String testName, String performanceDataName) {
        super(testName, performanceDataName);
        expectedTime = WINDOW_OPEN;
    }

    public static Test suite() {
        return emptyConfiguration()
                .addTest(J2SESetup.class, "testCloseMemoryToolbar", "testOpenDataProject")
                .addTest(CloseEditorTest.class)
                .suite();
    }

    public void testClosing20kBJavaFile() {
        fileProject = "PerformanceTestData";
        filePackage = "org.netbeans.test.performance";
        fileName = "Main20kB.java";
        doMeasurement();
    }

    public void testClosing20kBFormFile() {
        fileProject = "PerformanceTestData";
        filePackage = "org.netbeans.test.performance";
        fileName = "JFrame20kB.java";
        doMeasurement();
    }

    @Override
    public void initialize() {
        EditorOperator.closeDiscardAll();
        repaintManager().addRegionFilter(new LoggingRepaintManager.RegionFilter() {

            @Override
            public boolean accept(JComponent c) {
                return !c.getClass().getName().contains("ActiveConfigAction");
            }

            @Override
            public String getFilterName() {
                return "Ignore o.n.m.project.ui.actions.ActiveConfigAction repaints because they are done asynchronously";
            }
        });
    }

    @Override
    public void shutdown() {
        EditorOperator.closeDiscardAll();
    }

    @Override
    public void prepare() {
        new OpenAction().performAPI(new Node(new SourcePackagesNode(fileProject), filePackage + '|' + fileName));
    }

    public ComponentOperator open() {
        if (fileName.equalsIgnoreCase("JFrame20kB.java")) {
            new CloseViewAction().performMenu(new FormDesignerOperator(fileName));
        } else {
            new CloseViewAction().performMenu(new EditorOperator(fileName));
        }
        return null;
    }

}
