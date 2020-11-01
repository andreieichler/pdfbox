/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.fixup.AbstractFixup;
import org.apache.pdfbox.pdmodel.fixup.AcroFormDefaultFixup;
import org.apache.pdfbox.pdmodel.fixup.processor.AcroFormOrphanWidgetsProcessor;
import org.junit.Test;

/**
 * Tests for building AcroForm entries form Widget annotations.
 *
 */
public class PDAcroFormFromAnnotsTest
{
    /**
     * PDFBOX-4985 AcroForms entry but empty Fields array 
     * 
     * Using the default get acroform call with error correction
     * 
     * @throws IOException
     */
    @Test
    public void testFromAnnots4985DefaultMode() throws IOException
    {

        String sourceUrl = "https://issues.apache.org/jira/secure/attachment/13013354/POPPLER-806.pdf";
        String acrobatSourceUrl = "https://issues.apache.org/jira/secure/attachment/13013384/POPPLER-806-acrobat.pdf";

        int numFormFieldsByAcrobat = 0;

        try (PDDocument testPdf = Loader.loadPDF(new URL(acrobatSourceUrl).openStream()))
        {
            PDDocumentCatalog catalog = testPdf.getDocumentCatalog();
            PDAcroForm acroForm = catalog.getAcroForm(null);
            numFormFieldsByAcrobat = acroForm.getFields().size();
        }
                
        try (PDDocument testPdf = Loader.loadPDF(new URL(sourceUrl).openStream()))
        {
            PDDocumentCatalog catalog = testPdf.getDocumentCatalog();
            // need to do a low level cos access as the PDModel access will build the AcroForm 
            COSDictionary cosAcroForm = (COSDictionary) catalog.getCOSObject().getDictionaryObject(COSName.ACRO_FORM);
            COSArray cosFields = (COSArray) cosAcroForm.getDictionaryObject(COSName.FIELDS);
            assertEquals("Initially there shall be 0 fields", 0, cosFields.size());
            PDAcroForm acroForm = catalog.getAcroForm();
            assertEquals("After rebuild there shall be " + numFormFieldsByAcrobat + " fields", numFormFieldsByAcrobat, acroForm.getFields().size());
        }
    }

    /**
     * PDFBOX-4985 AcroForms entry but empty Fields array 
     * 
     * Using the acroform call with error correction
     * 
     * @throws IOException
     */
    @Test
    public void testFromAnnots4985CorrectionMode() throws IOException
    {

        String sourceUrl = "https://issues.apache.org/jira/secure/attachment/13013354/POPPLER-806.pdf";
        String acrobatSourceUrl = "https://issues.apache.org/jira/secure/attachment/13013384/POPPLER-806-acrobat.pdf";

        int numFormFieldsByAcrobat = 0;

        try (PDDocument testPdf = Loader.loadPDF(new URL(acrobatSourceUrl).openStream()))
        {
            PDDocumentCatalog catalog = testPdf.getDocumentCatalog();
            PDAcroForm acroForm = catalog.getAcroForm(null);
            numFormFieldsByAcrobat = acroForm.getFields().size();
        }
                
        try (PDDocument testPdf = Loader.loadPDF(new URL(sourceUrl).openStream()))
        {
            PDDocumentCatalog catalog = testPdf.getDocumentCatalog();
            // need to do a low level cos access as the PDModel access will build the AcroForm 
            COSDictionary cosAcroForm = (COSDictionary) catalog.getCOSObject().getDictionaryObject(COSName.ACRO_FORM);
            COSArray cosFields = (COSArray) cosAcroForm.getDictionaryObject(COSName.FIELDS);
            assertEquals("Initially there shall be 0 fields", 0, cosFields.size());
            PDAcroForm acroForm = catalog.getAcroForm(new AcroFormDefaultFixup(testPdf));
            assertEquals("After rebuild there shall be " + numFormFieldsByAcrobat + " fields", numFormFieldsByAcrobat, acroForm.getFields().size());
        }
    } 

    /**
     * PDFBOX-4985 AcroForms entry but empty Fields array 
     * 
     * Using the acroform call without error correction
     * 
     * @throws IOException
     */
    @Test
    public void testFromAnnots4985WithoutCorrectionMode() throws IOException
    {

        String sourceUrl = "https://issues.apache.org/jira/secure/attachment/13013354/POPPLER-806.pdf";

        int numCosFormFields = 0;
                
        try (PDDocument testPdf = Loader.loadPDF(new URL(sourceUrl).openStream()))
        {
            PDDocumentCatalog catalog = testPdf.getDocumentCatalog();
            // need to do a low level cos access as the PDModel access will build the AcroForm 
            COSDictionary cosAcroForm = (COSDictionary) catalog.getCOSObject().getDictionaryObject(COSName.ACRO_FORM);
            COSArray cosFields = (COSArray) cosAcroForm.getDictionaryObject(COSName.FIELDS);
            numCosFormFields = cosFields.size();
            assertEquals("Initially there shall be 0 fields", 0, cosFields.size());
            PDAcroForm acroForm = catalog.getAcroForm(null);
            assertEquals("After call without correction there shall be " + numCosFormFields + " fields", numCosFormFields, acroForm.getFields().size());
        }
    }

    /**
     * PDFBOX-3891 AcroForm with empty fields entry
     * 
     * With the default correction nothing shall be added
     * 
     * @throws IOException
     */
    @Test
    public void testFromAnnots3891DontCreateFields() throws IOException
    {

        String sourceUrl = "https://issues.apache.org/jira/secure/attachment/12881055/merge-test.pdf";

        try (PDDocument testPdf = Loader.loadPDF(new URL(sourceUrl).openStream()))
        {
            PDDocumentCatalog catalog = testPdf.getDocumentCatalog();
            // need to do a low level cos access as the PDModel access will build the AcroForm
            COSDictionary cosAcroForm = (COSDictionary) catalog.getCOSObject().getDictionaryObject(COSName.ACRO_FORM);
            COSArray cosFields = (COSArray) cosAcroForm.getDictionaryObject(COSName.FIELDS);
            assertEquals("Initially there shall be 0 fields", 0, cosFields.size());
            PDAcroForm acroForm = catalog.getAcroForm();
            assertEquals("After call with default correction there shall be 0 fields", 0, acroForm.getFields().size());
        }
    }

    /**
     * PDFBOX-3891 AcroForm with empty fields entry
     * 
     * Special fixup to create fields
     * 
     * @throws IOException
     */
    @Test
    public void testFromAnnots3891CreateFields() throws IOException
    {

        String sourceUrl = "https://issues.apache.org/jira/secure/attachment/12881055/merge-test.pdf";
        String acrobatSourceUrl = "https://issues.apache.org/jira/secure/attachment/13014447/merge-test-na-acrobat.pdf";

        int numFormFieldsByAcrobat = 0;

        // will build the expected fields using the acrobat source document
        Map<String, PDField> fieldsByName = new HashMap<>();

        try (PDDocument testPdf = Loader.loadPDF(new URL(acrobatSourceUrl).openStream()))
        {
            PDDocumentCatalog catalog = testPdf.getDocumentCatalog();
            PDAcroForm acroForm = catalog.getAcroForm(null);
            numFormFieldsByAcrobat = acroForm.getFields().size();
            for (PDField field : acroForm.getFieldTree())
            {
                fieldsByName.put(field.getFullyQualifiedName(), field);
            }
        }

        try (PDDocument testPdf = Loader.loadPDF(new URL(sourceUrl).openStream()))
        {
            PDDocumentCatalog catalog = testPdf.getDocumentCatalog();
            // need to do a low level cos access as the PDModel access will build the AcroForm
            COSDictionary cosAcroForm = (COSDictionary) catalog.getCOSObject().getDictionaryObject(COSName.ACRO_FORM);
            COSArray cosFields = (COSArray) cosAcroForm.getDictionaryObject(COSName.FIELDS);
            assertEquals("Initially there shall be 0 fields", 0, cosFields.size());
            PDAcroForm acroForm = catalog.getAcroForm(new CreateFieldsFixup(testPdf));
            assertEquals("After rebuild there shall be " + numFormFieldsByAcrobat + " fields", numFormFieldsByAcrobat, acroForm.getFields().size());

            // the the fields found are contained in the map
            for (PDField field : acroForm.getFieldTree())
            {
                assertNotNull(fieldsByName.get(field.getFullyQualifiedName()));
            }

            // test all fields in the map are also found in the AcroForm
            for (String fieldName : fieldsByName.keySet())
            {
                assertNotNull(acroForm.getField(fieldName));
            }
        }
    }

    /*
     * Create fields from widget annotations
     */
    class CreateFieldsFixup extends AbstractFixup
    {
        CreateFieldsFixup(PDDocument document)
        { 
            super(document); 
        }

        @Override
        public void apply() {
            new AcroFormOrphanWidgetsProcessor(document).process();

        }        
    }
}