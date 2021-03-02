/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Ziver Koc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package zutil.net.ws.openapi;

import org.junit.Test;
import zutil.net.ws.WebServiceDef;
import zutil.net.ws.soap.SOAPTest;

import static org.junit.Assert.assertEquals;

public class OpenAPIWriterTest {

    @Test
    public void basicTest() {
        OpenAPIWriter writer = new OpenAPIWriter(new WebServiceDef(SOAPTest.MainSOAPClass.class));
        writer.addServer("example.com", "Main Server");

        assertEquals("{\"components\": {\"schemas\": {\"SpecialReturnClass\": {\"type\": \"object\", \"properties\": {\"b\": {\"type\": \"array\", \"items\": {\"format\": \"byte\", \"type\": \"string\"}}, \"otherValue1\": {\"type\": \"string\"}, \"otherName2\": {\"type\": \"string\"}, \"inner\": {\"type\": \"object\", \"properties\": {\"innerClassParam2\": {\"type\": \"string\"}, \"innerClassParam1\": {\"type\": \"string\"}}}}}, \"SimpleReturnClass\": {\"type\": \"object\", \"properties\": {\"otherParam1\": {\"type\": \"string\"}, \"param2\": {\"type\": \"string\"}}}}}, \"servers\": [{\"description\": \"Main Server\", \"url\": \"example.com\"}], \"openapi\": \"3.0.1\", \"paths\": {\"/simpleReturnClassMethod\": {\"get\": {\"responses\": {\"200\": {\"description\": \"A successful response.\", \"content\": {\"application/json\": {\"schema\": {\"type\": \"object\", \"$ref\": \"#/components/schemas/SimpleReturnClass\"}}}}}, \"parameters\": [{\"schema\": {\"type\": \"string\"}, \"in\": \"query\", \"name\": \"byte\", \"required\": true}]}}, \"/exceptionMethod\": {\"get\": {\"description\": \"Documentation of method exceptionMethod()\", \"responses\": {\"200\": {\"description\": \"A successful response.\"}}, \"parameters\": [{\"schema\": {\"type\": \"integer\"}, \"in\": \"query\", \"name\": \"otherParam1\", \"required\": false}, {\"schema\": {\"type\": \"integer\"}, \"in\": \"query\", \"name\": \"otherParam2\", \"required\": false}]}}, \"/specialReturnMethod\": {\"get\": {\"responses\": {\"200\": {\"description\": \"A successful response.\", \"content\": {\"application/json\": {\"schema\": {\"type\": \"array\", \"items\": {\"type\": \"object\", \"$ref\": \"#/components/schemas/SpecialReturnClass\"}}}}}}, \"parameters\": [{\"schema\": {\"type\": \"string\"}, \"in\": \"query\", \"name\": \"StringName2\", \"required\": true}]}}, \"/stringArrayMethod\": {\"get\": {\"responses\": {\"200\": {\"description\": \"A successful response.\", \"content\": {\"application/json\": {\"schema\": {\"type\": \"array\", \"items\": {\"type\": \"array\", \"items\": {\"type\": \"string\"}}}}}}}, \"parameters\": [{\"schema\": {\"type\": \"string\"}, \"in\": \"query\", \"name\": \"StringName\", \"required\": true}]}}, \"/voidMethod\": {\"get\": {\"responses\": {\"200\": {\"description\": \"A successful response.\"}}, \"parameters\": []}}}, \"info\": {\"description\": \"\", \"title\": \"MainSOAPClass\", \"version\": \"\"}}", writer.write());
    }
}