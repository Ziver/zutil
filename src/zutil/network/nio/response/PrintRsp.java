package zutil.net.nio.response;

import zutil.io.MultiPrintStream;

public class PrintRsp extends ResponseEvent{

	@Override
	protected void responseEvent(Object rsp) {
		MultiPrintStream.out.println(rsp);
	}

}
