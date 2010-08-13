package zutil.network.upnp.services;

import zutil.network.ws.WSReturnValueList;

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