package zutil.net.torrent;

import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by Ziver on 2016-09-27.
 */
public class TorrentMetainfoTest {


    @Test
    public void decode() throws ParseException {
        TorrentMetainfo tmp = new TorrentMetainfo(
                "d8:announce39:http://torrent.ubuntu.com:6969/announce13:announce-listll" +
                        "39:http://torrent.ubuntu.com:6969/announceel44:http://ipv6.torrent.ubuntu.com:6969/announceee" +
                        "7:comment29:Ubuntu CD releases.ubuntu.com13:creation datei1469103218e4:infod" +
                        "6:lengthi1513308160e4:name32:ubuntu-16.04.1-desktop-amd64.iso12:piece lengthi524288e" +
                        "6:pieces60:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaee");

        assertEquals("http://torrent.ubuntu.com:6969/announce", tmp.getAnnounce());
        assertArrayEquals(new String[]{
                        "http://torrent.ubuntu.com:6969/announce",
                        "http://ipv6.torrent.ubuntu.com:6969/announce"},
                tmp.getAnnounceList().toArray());
        assertEquals("Ubuntu CD releases.ubuntu.com", tmp.getComment());
        assertEquals(1469103218, tmp.getCreationDate());
        assertEquals(1513308160, tmp.getSize());
        assertEquals("ubuntu-16.04.1-desktop-amd64.iso", tmp.getName());
        assertEquals(1, tmp.getFileList().size());
        assertEquals("ubuntu-16.04.1-desktop-amd64.iso", tmp.getFileList().get(0).getFilename());
        assertEquals(524288, tmp.getPieceLength());
        assertEquals(3, tmp.getPieceHashes().size());
        assertEquals("aaaaaaaaaaaaaaaaaaaa", tmp.getPieceHashes().get(0));
        assertEquals("aaaaaaaaaaaaaaaaaaaa", tmp.getPieceHashes().get(1));
        assertEquals("aaaaaaaaaaaaaaaaaaaa", tmp.getPieceHashes().get(2));
    }
}