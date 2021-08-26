/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

package zutil.net.dns.packet;

import zutil.io.PositionalInputStream;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is a general wrapper for a whole DNS packet.
 *
 * @see <a href="https://tools.ietf.org/html/rfc1035">DNS Spec (rfc1035)</a>
 * @author Ziver on 2016-04-11.
 */
public class DnsPacket {
    private DnsPacketHeader header;
    private ArrayList<DnsPacketQuestion> questions;
    private ArrayList<DnsPacketResource> answerRecords;
    private ArrayList<DnsPacketResource> nameServers;
    private ArrayList<DnsPacketResource> additionalRecords;


    public DnsPacket() {
        header = new DnsPacketHeader();
        questions = new ArrayList<>();
        answerRecords = new ArrayList<>();
        nameServers = new ArrayList<>();
        additionalRecords = new ArrayList<>();
    }


    public DnsPacketHeader getHeader() {
        return header;
    }
    public List<DnsPacketQuestion> getQuestions() {
        return Collections.unmodifiableList(questions);
    }
    public List<DnsPacketResource> getAnswerRecords() {
        return Collections.unmodifiableList(answerRecords);
    }
    public List<DnsPacketResource> getNameServers() {
        return Collections.unmodifiableList(nameServers);
    }
    public List<DnsPacketResource> getAdditionalRecords() {
        return Collections.unmodifiableList(additionalRecords);
    }


    public void addQuestion(DnsPacketQuestion question) {
        questions.add(question);
        header.countQuestion = questions.size();
    }
    public void addAnswerRecord(DnsPacketResource resource) {
        answerRecords.add(resource);
        header.countAnswerRecord = answerRecords.size();
    }
    public void addAnswerRecord(List<DnsPacketResource> resources) {
        answerRecords.addAll(resources);
        header.countAnswerRecord = answerRecords.size();
    }
    public void addNameServer(DnsPacketResource resource) {
        nameServers.add(resource);
        header.countNameServer = nameServers.size();
    }
    public void addAdditionalRecord(DnsPacketResource resource) {
        additionalRecords.add(resource);
        header.countAdditionalRecord = additionalRecords.size();
    }


    public static DnsPacket read(InputStream in) throws IOException {
        BinaryStructInputStream structIn = new BinaryStructInputStream(new PositionalInputStream(in));

        DnsPacket packet = new DnsPacket();
        structIn.read(packet.header);

        for (int i=0; i<packet.header.countQuestion; ++i) {
            DnsPacketQuestion question = new DnsPacketQuestion();
            structIn.read(question);
            packet.questions.add(question);
        }
        readResource(structIn, packet.header.countAnswerRecord, packet.answerRecords);
        readResource(structIn, packet.header.countNameServer, packet.nameServers);
        readResource(structIn, packet.header.countAdditionalRecord, packet.additionalRecords);
        return packet;
    }
    private static void readResource(BinaryStructInputStream structIn, int count, ArrayList<DnsPacketResource> list) throws IOException {
        for (int i=0; i<count; ++i) {
            DnsPacketResource resource = new DnsPacketResource();
            structIn.read(resource);
            list.add(resource);
        }
    }

    public void write(OutputStream out) throws IOException {
        BinaryStructOutputStream structOut = new BinaryStructOutputStream(out);

        structOut.write(header);

        for (DnsPacketQuestion question : questions)
            structOut.write(question);
        for (DnsPacketResource answerRecord : answerRecords)
            structOut.write(answerRecord);
        for (DnsPacketResource nameServer : nameServers)
            structOut.write(nameServer);
        for (DnsPacketResource additionalRecord : additionalRecords)
            structOut.write(additionalRecord);

    }

}
