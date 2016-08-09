package zutil.net.dns;

import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is a general wrapper for a whole DNS packet.
 *
 * @see <a href="https://tools.ietf.org/html/rfc1035">rfc1035</a>
 * @author Ziver on 2016-04-11.
 */
public class DNSPacket {
    private DNSPacketHeader header;
    private ArrayList<DNSPacketQuestion> questions;
    private ArrayList<DNSPacketResource> answerRecords;
    private ArrayList<DNSPacketResource> nameServers;
    private ArrayList<DNSPacketResource> additionalRecords;


    public DNSPacket(){
        header = new DNSPacketHeader();
        questions = new ArrayList<>();
        answerRecords = new ArrayList<>();
        nameServers = new ArrayList<>();
        additionalRecords = new ArrayList<>();
    }


    public DNSPacketHeader getHeader(){
        return header;
    }
    public List<DNSPacketQuestion> getQuestions(){
        return Collections.unmodifiableList(questions);
    }
    public List<DNSPacketResource> getAnswerRecords(){
        return Collections.unmodifiableList(answerRecords);
    }
    public List<DNSPacketResource> getNameServers(){
        return Collections.unmodifiableList(nameServers);
    }
    public List<DNSPacketResource> getAdditionalRecords(){
        return Collections.unmodifiableList(additionalRecords);
    }


    public void addQuestion(DNSPacketQuestion question){
        questions.add(question);
        header.countQuestion = questions.size();
    }
    public void addAnswerRecord(DNSPacketResource resource){
        answerRecords.add(resource);
        header.countAnswerRecord = answerRecords.size();
    }
    public void addNameServer(DNSPacketResource resource){
        nameServers.add(resource);
        header.countNameServer = nameServers.size();
    }
    public void addAdditionalRecord(DNSPacketResource resource){
        additionalRecords.add(resource);
        header.countAdditionalRecord = additionalRecords.size();
    }


    public static DNSPacket read(BinaryStructInputStream structIn) throws IOException {
        DNSPacket packet = new DNSPacket();
        structIn.read(packet.header);

        for (int i=0; i<packet.header.countQuestion; ++i) {
            DNSPacketQuestion question = new DNSPacketQuestion();
            structIn.read(question);
            packet.questions.add(question);
        }
        readResource(structIn, packet.header.countAnswerRecord, packet.answerRecords);
        readResource(structIn, packet.header.countNameServer, packet.nameServers);
        readResource(structIn, packet.header.countAdditionalRecord, packet.additionalRecords);
        return packet;
    }
    private static void readResource(BinaryStructInputStream structIn, int count, ArrayList<DNSPacketResource> list) throws IOException {
        for (int i=0; i<count; ++i){
            DNSPacketResource resource = new DNSPacketResource();
            structIn.read(resource);
            list.add(resource);
        }
    }

    public void write(BinaryStructOutputStream structOut) throws IOException {
        structOut.write(header);

        for (DNSPacketQuestion question : questions)
            structOut.write(question);
        for (DNSPacketResource answerRecord : answerRecords)
            structOut.write(answerRecord);
        for (DNSPacketResource nameServer : nameServers)
            structOut.write(nameServer);
        for (DNSPacketResource additionalRecord : additionalRecords)
            structOut.write(additionalRecord);

    }

}
