package zutil.net.dns.packet;

/**
 *
 */
public final class DnsConstants {

    public static final class OPCODE {
        /** a standard query (QUERY) */
        public static final int QUERY = 0;
        /** an inverse query (IQUERY) */
        public static final int IQUERY = 1;
        /** a server status request (STATUS) */
        public static final int STATUS = 2;
    }


    /** DNS Response Codes */
    public static final class RCODE {
        /**
         * No error condition
         */
        public static final int NO_ERROR = 0;
        /**
         * Format error - The name server was
         * unable to interpret the query.
         */
        public static final int FORMAT_ERROR = 1;
        /**
         * Server failure - The name server was
         * unable to process this query due to a
         * problem with the name server.
         */
        public static final int SERVER_FAILURE = 2;
        /**
         * Name Error - Meaningful only for
         * responses from an authoritative name
         * server, this code signifies that the
         * domain name referenced in the query does
         * not exist.
         */
        public static final int NAME_ERROR = 3;
        /**
         * Not Implemented - The name server does
         * not support the requested kind of query.
         */
        public static final int NOT_IMPLEMENTED = 4;
        /**
         * Refused - The name server refuses to
         * perform the specified operation for
         * policy reasons.
         */
        public static final int REFUSED = 5;
    }


    public static final class TYPE {
        /** a host address */
        public static final int A     = 1;
        /** an authoritative name server */
        public static final int NS    = 2;
        /** a mail destination (Obsolete - use MX) */
        public static final int MD    = 3;
        /** a mail forwarder (Obsolete - use MX) */
        public static final int MF    = 4;
        /** the canonical name for an alias */
        public static final int CNAME = 5;
        /** marks the start of a zone of authority */
        public static final int SOA   = 6;
        /** a mailbox domain name (EXPERIMENTAL) */
        public static final int MB    = 7;
        /** a mail group member (EXPERIMENTAL) */
        public static final int MG    = 8;
        /** a mail rename domain name (EXPERIMENTAL) */
        public static final int MR    = 9;
        /** a null RR (EXPERIMENTAL) */
        public static final int NULL  = 10;
        /** a well known service description */
        public static final int WKS   = 11;
        /** a domain name pointer */
        public static final int PTR   = 12;
        /** host information */
        public static final int HINFO = 13;
        /** mailbox or mail list information */
        public static final int MINFO = 14;
        /** mail exchange */
        public static final int MX    = 15;
        /** text strings */
        public static final int TXT   = 16;
        /** service location record in format {Instance}.{Service}.{Domain}*/
        public static final int SRV   = 33;
        /** A request for a transfer of an entire zone */
        public static final int AXFR  = 252;
        /** A request for mailbox-related records (MB, MG or MR) */
        public static final int MAILB = 253;
        /** A request for mail agent RRs (Obsolete - see MX) */
        public static final int MAILA = 254;
        /** A request for all records */
        public static final int ANY   = 255;
    }


    public static final class CLASS {
        /**  the Internet */
        public static final int IN   = 1;
        /**  the CSNET class (Obsolete - used only for examples in some obsolete RFCs) */
        public static final int CS   = 2;
        /**  the CHAOS class */
        public static final int CH   = 3;
        /**  Hesiod [Dyer 87] */
        public static final int HS   = 4;
        /** any class */
        public static final int ANY  = 255;
    }
}
