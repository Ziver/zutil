package zutil.net.dns;

import zutil.parser.binary.BinaryStructOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * This class is a general wrapper for a whole DNS packet.
 *
 * Created by ezivkoc on 2016-04-11.
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


    public static DNSPacket read(InputStream in){
        return null;
    }

    public void write(OutputStream out) throws IOException {
        BinaryStructOutputStream structOut = new BinaryStructOutputStream(out);
        structOut.write(header);
        out.flush();

        /*for (DNSPacketQuestion question : questions)
            question.write(out);
        for (DNSPacketResource answerRecord : answerRecords)
            answerRecord.write(out);
        for (DNSPacketResource nameServer : nameServers)
            nameServer.write(out);
        for (DNSPacketResource additionalRecord : additionalRecords)
            additionalRecord.write(out);*/
    }

}
