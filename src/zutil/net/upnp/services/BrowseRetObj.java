package zutil.net.upnp.services;

import zutil.net.ws.WSReturnValueList;

public class BrowseRetObj extends WSReturnValueList{
		@WSValueName("Result")
		public String Result;
		@WSValueName("NumberReturned")
		public int NumberReturned;
		@WSValueName("TotalMatches")
		public int TotalMatches;
		@WSValueName("UpdateID")
		public int UpdateID;
	}